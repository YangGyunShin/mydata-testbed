package com.mydata.mydatatestbed.service.impl;

import com.mydata.mydatatestbed.entity.Board;
import com.mydata.mydatatestbed.entity.Member;
import com.mydata.mydatatestbed.dto.board.BoardDetailResponseDto;
import com.mydata.mydatatestbed.dto.board.BoardListResponseDto;
import com.mydata.mydatatestbed.dto.board.BoardRequestDto;
import com.mydata.mydatatestbed.mapper.BoardMapper;
import com.mydata.mydatatestbed.repository.BoardRepository;
import com.mydata.mydatatestbed.service.BoardService;
import com.mydata.mydatatestbed.service.FileService;
import com.mydata.mydatatestbed.service.FileService.FileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardMapper boardMapper;
    private final FileService fileService;

    private static final int PAGE_SIZE = 10;
    private static final String FILE_SUB_DIR = "board";

    @Override
    public Page<BoardListResponseDto> getBoardList(int page, String keyword, String searchType) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Board> boards;

        if (keyword == null || keyword.trim().isEmpty()) {
            boards = boardRepository.findAllByOrderByCreatedAtDesc(pageable);
        } else {
            boards = switch (searchType) {
                case "title" -> boardRepository.findByTitleContainingOrderByCreatedAtDesc(keyword, pageable);
                case "author" -> boardRepository.findByMemberNameContaining(keyword, pageable);
                default -> boardRepository.findByKeyword(keyword, pageable);
            };
        }
        return boards.map(boardMapper::toListResponseDto);
    }

    @Override
    @Transactional
    public BoardDetailResponseDto getBoardDetail(Long id) {
        Board board = boardRepository.findByIdWithMember(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        board.incrementViewCount();
        return boardMapper.toDetailResponseDto(board);
    }

    @Override
    public BoardDetailResponseDto getBoardDetailWithoutViewCount(Long id) {
        Board board = boardRepository.findByIdWithMember(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        return boardMapper.toDetailResponseDto(board);
    }

    /**
     * 게시글 생성 흐름:
     * 1. 첨부파일 존재 여부 확인
     * 2-A. 첨부파일 있음 → FileService로 파일 저장 → 파일 정보 포함하여 Board 엔티티 생성
     * 2-B. 첨부파일 없음 → 파일 정보 없이 Board 엔티티 생성
     * 3. Board 엔티티 DB에 저장
     * 4. 저장된 게시글 ID 반환
     */
    @Override
    @Transactional
    public Long createBoard(BoardRequestDto requestDto, Member member, MultipartFile file) {
        // 1. 첨부파일 존재 여부 확인
        Board board;
        if (file != null && !file.isEmpty()) {
            // 2-A. 첨부파일 있음 → 파일 저장 후 Board 생성
            FileInfo fileInfo = fileService.saveFile(file, FILE_SUB_DIR);
            board = boardMapper.toEntity(requestDto, member,
                    fileInfo.path(), fileInfo.originalName(), fileInfo.size());
        } else {
            // 2-B. 첨부파일 없음 → 파일 정보 없이 Board 생성
            board = boardMapper.toEntity(requestDto, member);
        }

        // 3. DB 저장 후 4. ID 반환
        return boardRepository.save(board).getId();
    }

    /**
     * 게시글 수정 흐름:
     * 1. 게시글 ID로 기존 게시글 조회 (없으면 예외)
     * 2. 작성자 본인 확인 (아니면 예외)
     * 3. 기존 첨부파일 삭제 요청 처리
     *    - deleteAttachment=true이고 기존 파일 있으면 → 파일 삭제 + 엔티티에서 파일 정보 제거
     * 4. 새 첨부파일 업로드 처리
     *    4-A. 새 파일 있음 → 기존 파일 삭제 → 새 파일 저장 → 엔티티 업데이트 (제목, 내용, 파일 정보)
     *    4-B. 새 파일 없음 → 엔티티 업데이트 (제목, 내용만)
     */
    @Override
    @Transactional
    public void updateBoard(Long id, BoardRequestDto requestDto, Member member,
                            MultipartFile file, boolean deleteAttachment) {
        // 1. 기존 게시글 조회
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 2. 작성자 본인 확인
        if (!board.isAuthor(member.getId())) {
            throw new IllegalArgumentException("게시글 수정 권한이 없습니다.");
        }

        // 3. 기존 첨부파일 삭제 요청 처리
        if (deleteAttachment && board.getAttachmentPath() != null) {
            fileService.deleteFile(board.getAttachmentPath());
            board.removeAttachment();
        }

        // 4. 새 첨부파일 업로드 처리
        if (file != null && !file.isEmpty()) {
            // 4-A. 새 파일 있음
            // 기존 파일이 있으면 먼저 삭제
            if (board.getAttachmentPath() != null) {
                fileService.deleteFile(board.getAttachmentPath());
            }
            // 새 파일 저장
            FileInfo fileInfo = fileService.saveFile(file, FILE_SUB_DIR);
            // 엔티티 업데이트 (제목, 내용, 파일 정보 모두)
            board.update(requestDto.getTitle(), requestDto.getContent(),
                    fileInfo.path(), fileInfo.originalName(), fileInfo.size());
        } else {
            // 4-B. 새 파일 없음 → 제목, 내용만 업데이트
            board.update(requestDto.getTitle(), requestDto.getContent(), null, null, null);
        }
    }

    /**
     * 게시글 삭제 흐름:
     * 1. 게시글 ID로 기존 게시글 조회 (없으면 예외)
     * 2. 삭제 권한 확인 (작성자 본인 또는 관리자만 가능, 아니면 예외)
     * 3. 첨부파일이 있으면 파일 시스템에서 삭제
     * 4. DB에서 게시글 삭제
     */
    @Override
    @Transactional
    public void deleteBoard(Long id, Member member) {
        // 1. 기존 게시글 조회
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 2. 삭제 권한 확인 (작성자 또는 관리자)
        if (!board.isAuthor(member.getId()) && !member.isAdmin()) {
            throw new IllegalArgumentException("게시글 삭제 권한이 없습니다.");
        }

        // 3. 첨부파일 삭제
        if (board.getAttachmentPath() != null) {
            fileService.deleteFile(board.getAttachmentPath());
        }

        // 4. DB에서 게시글 삭제
        boardRepository.delete(board);
    }
}
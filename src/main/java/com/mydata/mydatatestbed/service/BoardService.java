package com.mydata.mydatatestbed.service;

import com.mydata.mydatatestbed.entity.Member;
import com.mydata.mydatatestbed.dto.board.BoardDetailResponseDto;
import com.mydata.mydatatestbed.dto.board.BoardListResponseDto;
import com.mydata.mydatatestbed.dto.board.BoardRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface BoardService {

    Page<BoardListResponseDto> getBoardList(int page, String keyword, String searchType);

    BoardDetailResponseDto getBoardDetail(Long id);

    BoardDetailResponseDto getBoardDetailWithoutViewCount(Long id);

    Long createBoard(BoardRequestDto requestDto, Member member, MultipartFile file);

    void updateBoard(Long id, BoardRequestDto requestDto, Member member, MultipartFile file, boolean deleteAttachment);

    void deleteBoard(Long id, Member member);
}
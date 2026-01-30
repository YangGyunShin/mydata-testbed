package com.mydata.mydatatestbed.mapper;

import com.mydata.mydatatestbed.entity.Board;
import com.mydata.mydatatestbed.entity.Member;
import com.mydata.mydatatestbed.dto.board.BoardDetailResponseDto;
import com.mydata.mydatatestbed.dto.board.BoardListResponseDto;
import com.mydata.mydatatestbed.dto.board.BoardRequestDto;
import com.mydata.mydatatestbed.util.FileSizeFormatter;
import org.springframework.stereotype.Component;

@Component
public class BoardMapper {

    public Board toEntity(BoardRequestDto dto, Member member) {
        return Board.builder()
                .member(member)
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();
    }

    public Board toEntity(BoardRequestDto dto, Member member,
                          String attachmentPath, String attachmentName, Long attachmentSize) {
        return Board.builder()
                .member(member)
                .title(dto.getTitle())
                .content(dto.getContent())
                .attachmentPath(attachmentPath)
                .attachmentName(attachmentName)
                .attachmentSize(attachmentSize)
                .build();
    }

    public BoardListResponseDto toListResponseDto(Board board) {
        return BoardListResponseDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .authorName(board.getMember().getName())
                .viewCount(board.getViewCount())
                .hasAttachment(board.getAttachmentPath() != null)
                .createdAt(board.getCreatedAt())
                .build();
    }

    public BoardDetailResponseDto toDetailResponseDto(Board board) {
        return BoardDetailResponseDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .authorId(board.getMember().getId())
                .authorName(board.getMember().getName())
                .viewCount(board.getViewCount())
                .attachmentPath(board.getAttachmentPath())
                .attachmentName(board.getAttachmentName())
                .formattedFileSize(FileSizeFormatter.format(board.getAttachmentSize()))
                .hasAttachment(board.getAttachmentPath() != null && !board.getAttachmentPath().isEmpty())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }
}
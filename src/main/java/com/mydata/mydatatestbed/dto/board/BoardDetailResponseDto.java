package com.mydata.mydatatestbed.dto.board;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardDetailResponseDto {

    private final Long id;
    private final String title;
    private final String content;
    private final Long authorId;
    private final String authorName;
    private final int viewCount;
    private final String attachmentPath;
    private final String attachmentName;
    private final String formattedFileSize;
    private final boolean hasAttachment;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
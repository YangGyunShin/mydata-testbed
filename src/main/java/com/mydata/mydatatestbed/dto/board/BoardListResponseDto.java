package com.mydata.mydatatestbed.dto.board;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardListResponseDto {

    private final Long id;
    private final String title;
    private final String authorName;
    private final int viewCount;
    private final boolean hasAttachment;
    private final LocalDateTime createdAt;
}
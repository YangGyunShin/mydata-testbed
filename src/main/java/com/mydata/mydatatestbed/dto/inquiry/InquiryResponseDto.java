package com.mydata.mydatatestbed.dto.inquiry;

import com.mydata.mydatatestbed.entity.Enum.InquiryStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InquiryResponseDto {

    private Long id;
    private String title;
    private String content;
    private String memberName;
    private String memberEmail;
    private InquiryStatus status;
    private String statusDisplayName;
    private String answer;
    private String answererName;
    private LocalDateTime answeredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
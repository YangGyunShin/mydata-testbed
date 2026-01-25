package com.mydata.mydatatestbed.dto.inquiry;

import com.mydata.mydatatestbed.entity.Enum.InquiryStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InquiryListResponseDto {

    private Long id;
    private String title;
    private InquiryStatus status;
    private String statusDisplayName;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;
}
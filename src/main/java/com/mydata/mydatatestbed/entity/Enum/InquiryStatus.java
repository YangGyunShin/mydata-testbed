package com.mydata.mydatatestbed.entity.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InquiryStatus {
    WAITING("대기"),
    COMPLETED("완료");

    private final String displayName;
}
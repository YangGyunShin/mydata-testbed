package com.mydata.mydatatestbed.entity.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {

    ROLE_USER("일반회원"),
    ROLE_ADMIN("관리자");

    private final String displayName;
}
package com.mydata.mydatatestbed.entity.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 회원 권한 Enum
 *
 * Entity에서 @Enumerated(EnumType.STRING)과 함께 사용하면
 * DB에 "ROLE_USER", "ROLE_ADMIN" 문자열로 저장됨.
 *
 * EnumType.ORDINAL(숫자 저장)을 사용하면 Enum 순서가 바뀔 때
 * 기존 데이터가 엉망이 되므로 반드시 STRING 사용 권장.
 */
@Getter
@RequiredArgsConstructor
public enum MemberRole {

    ROLE_USER("일반회원"),
    ROLE_ADMIN("관리자");

    private final String displayName;
}
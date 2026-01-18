package com.mydata.mydatatestbed.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 Value Object
 *
 * @Embeddable: 독립 테이블 없이 Member Entity에 내장됨.
 *              members 테이블의 password 컬럼으로 저장됨.
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordVo {

    @Column(name = "password", nullable = false)
    private String value;

    @Builder
    public PasswordVo(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
    }
}
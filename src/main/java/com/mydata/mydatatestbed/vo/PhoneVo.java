package com.mydata.mydatatestbed.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

/**
 * 전화번호 Value Object
 *
 * @Embeddable: 독립 테이블 없이 Member Entity에 내장됨.
 *              members 테이블의 phone 컬럼으로 저장됨.
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhoneVo {

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^01[0-9]-?\\d{3,4}-?\\d{4}$");

    @Column(name = "phone", length = 20)
    private String value;

    @Builder
    public PhoneVo(String value) {
        if (value != null && !value.isBlank()) {
            validate(value);
            this.value = value.replaceAll("-", "");
        }
    }

    private void validate(String value) {
        if (!PHONE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("올바른 전화번호 형식이 아닙니다.");
        }
    }
}
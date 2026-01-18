package com.mydata.mydatatestbed.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

/**
 * 이메일 Value Object
 *
 * @Embeddable: 이 클래스를 독립 테이블로 만들지 않고,
 *              다른 Entity(@Entity)에 내장(embed)시켜 사용하겠다는 의미.
 *              Member Entity에서 @Embedded로 포함하면,
 *              EmailVo의 value 필드가 members 테이블의 email 컬럼으로 저장됨.
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVo {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String value;

    @Builder
    public EmailVo(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }
    }
}
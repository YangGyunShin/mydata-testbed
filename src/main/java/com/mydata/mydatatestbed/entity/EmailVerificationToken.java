package com.mydata.mydatatestbed.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 이메일 인증 토큰 Entity
 *
 * 이메일 인증 플로우:
 * 1. 회원가입 3단계 완료 시 토큰 생성 → DB 저장 + 이메일 발송
 * 2. 사용자가 이메일의 인증 링크 클릭
 * 3. 토큰으로 DB 조회 → 만료 확인 → 회원가입 완료
 * 4. 사용된 토큰 삭제
 */
@Entity
@Table(name = "email_verification_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private EmailVerificationToken(String email, int expirationHours) {
        this.token = UUID.randomUUID().toString();
        this.email = email;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusHours(expirationHours);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
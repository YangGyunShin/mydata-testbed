package com.mydata.mydatatestbed.service;

import java.time.LocalDateTime;

public interface EmailService {

    /**
     * 이메일 인증 메일 발송
     * @param email 수신자 이메일
     * @return 생성된 토큰의 만료 시간
     */
    LocalDateTime sendVerificationEmail(String email);

    /**
     * 토큰 검증 및 회원가입 완료 처리
     * @param token 인증 토큰
     * @return 인증된 이메일 주소
     */
    String verifyToken(String token);

    /**
     * 인증 메일 재발송
     * @param email 수신자 이메일
     * @return 새로 생성된 토큰의 만료 시간
     */
    LocalDateTime resendVerificationEmail(String email);
}
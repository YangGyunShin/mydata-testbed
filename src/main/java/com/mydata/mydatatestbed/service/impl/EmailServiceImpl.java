package com.mydata.mydatatestbed.service.impl;

import com.mydata.mydatatestbed.entity.EmailVerificationToken;
import com.mydata.mydatatestbed.repository.EmailVerificationTokenRepository;
import com.mydata.mydatatestbed.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationTokenRepository tokenRepository;

    private static final int TOKEN_EXPIRATION_HOURS = 24;
    private static final String BASE_URL = "http://localhost:8080";

    @Override
    public LocalDateTime sendVerificationEmail(String email) {
        // 기존 토큰 삭제 (재발송 시)
        tokenRepository.deleteByEmail(email);

        // 새 토큰 생성
        EmailVerificationToken token = EmailVerificationToken.builder()
                .email(email)
                .expirationHours(TOKEN_EXPIRATION_HOURS)
                .build();
        tokenRepository.save(token);

        // 이메일 발송
        sendEmail(email, token.getToken());

        return token.getExpiresAt();
    }

    @Override
    @Transactional(readOnly = true)
    public String verifyToken(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 인증 링크입니다."));

        if (verificationToken.isExpired()) {
            throw new IllegalArgumentException("인증 링크가 만료되었습니다. 회원가입을 다시 진행해주세요.");
        }

        return verificationToken.getEmail();
    }

    @Override
    public LocalDateTime resendVerificationEmail(String email) {
        return sendVerificationEmail(email);
    }

    private void sendEmail(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("[마이데이터 테스트베드] 이메일 인증을 완료해주세요");
            helper.setText(buildEmailContent(token), true);

            mailSender.send(message);
            log.info("인증 이메일 발송 완료: {}", to);

        } catch (MessagingException e) {
            log.error("이메일 발송 실패: {}", e.getMessage());
            throw new RuntimeException("이메일 발송에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    private String buildEmailContent(String token) {
        String verifyUrl = BASE_URL + "/member/verify-email?token=" + token;

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
            </head>
            <body style="font-family: 'Pretendard', sans-serif; padding: 40px; background-color: #f8f9fa;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #fff; border-radius: 12px; padding: 40px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                    <h1 style="color: #1e3a5f; margin-bottom: 24px;">마이데이터 테스트베드</h1>
                    <h2 style="color: #333; margin-bottom: 16px;">이메일 인증을 완료해주세요</h2>
                    <p style="color: #666; line-height: 1.8; margin-bottom: 32px;">
                        안녕하세요!<br><br>
                        마이데이터 테스트베드 회원가입을 진행해 주셔서 감사합니다.<br>
                        아래 버튼을 클릭하여 이메일 인증을 완료해주세요.
                    </p>
                    <div style="text-align: center; margin: 32px 0;">
                        <a href="%s" style="display: inline-block; padding: 16px 48px; background-color: #0d6efd; color: #fff; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px;">
                            이메일 인증하기
                        </a>
                    </div>
                    <p style="color: #999; font-size: 14px; line-height: 1.6;">
                        * 이 링크는 24시간 동안 유효합니다.<br>
                        * 본인이 요청하지 않은 경우, 이 메일을 무시해주세요.
                    </p>
                    <hr style="border: none; border-top: 1px solid #eee; margin: 32px 0;">
                    <p style="color: #aaa; font-size: 12px;">
                        © 금융보안원 마이데이터 테스트베드
                    </p>
                </div>
            </body>
            </html>
            """.formatted(verifyUrl);
    }
}
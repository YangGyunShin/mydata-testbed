package com.mydata.mydatatestbed.config;

import com.mydata.mydatatestbed.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스 허용
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**").permitAll()
                        
                        // H2 콘솔 허용 (개발용)
                        .requestMatchers("/h2-console/**").permitAll()
                        
                        // 회원 관련 (로그인, 회원가입) 허용
                        .requestMatchers("/member/login", "/member/signup/**", "/member/verify-email", "/member/resend-verification").permitAll()
                        
                        // 소개, API 가이드 페이지 허용
                        .requestMatchers("/intro/**", "/api-guide/**").permitAll()
                        
                        // 고객지원 - 인증 필요한 URL (구체적인 패턴 먼저!)
                        .requestMatchers("/support/inquiry/**").authenticated()
                        .requestMatchers("/support/board/write").authenticated()
                        .requestMatchers("/support/board/*/edit").authenticated()
                        .requestMatchers("/support/board/*/delete").authenticated()
                        
                        // 고객지원 - 나머지는 허용
                        .requestMatchers("/support/**").permitAll()
                        
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/member/login")
                        .usernameParameter("email")  // 로그인 폼의 name 속성
                        .passwordParameter("password")
                        .defaultSuccessUrl("/")
                        .failureUrl("/member/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/member/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .userDetailsService(customUserDetailsService);

        return http.build();
    }

    /**
     * 비밀번호 인코더 빈 등록
     *
     * BCryptPasswordEncoder:
     * - 단방향 해시 함수 (복호화 불가)
     * - 솔트(salt) 자동 생성 (같은 비밀번호도 매번 다른 해시값)
     * - 적응형 해시 (strength 조절로 속도/보안 트레이드오프)
     *
     * 비밀번호 저장 흐름:
     * 1. 회원가입 시 encode("평문비밀번호") → 해시값 저장
     * 2. 로그인 시 matches("입력값", "저장된해시값") → true/false
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

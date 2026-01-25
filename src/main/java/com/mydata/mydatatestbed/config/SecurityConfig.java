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
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/support/inquiry/**").authenticated()
                        .requestMatchers("/intro/**", "/api-guide/**", "/support/**").permitAll()
                        .requestMatchers("/member/login", "/member/signup/**", "/member/verify-email", "/member/resend-verification").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
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
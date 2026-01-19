package com.mydata.mydatatestbed.dto.member;

import com.mydata.mydatatestbed.validation.PasswordMatching;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 회원가입 요청 DTO
 *
 * @PasswordMatching: 커스텀 어노테이션으로 비밀번호 일치 검증
 * - 기본적으로 password, passwordConfirm 필드를 비교
 * - @Valid 실행 시 자동으로 검증됨
 *
 * 검증 순서:
 * 1. 필드 레벨 어노테이션 (@NotBlank, @Email, @Size 등)
 * 2. 클래스 레벨 어노테이션 (@PasswordMatching)
 * 3. 모든 검증 실패는 BindingResult에 수집됨
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatching
public class MemberSignupRequestDto {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String passwordConfirm;

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 50, message = "이름은 50자 이하여야 합니다.")
    private String name;

    @Pattern(regexp = "^01[0-9]-?\\d{3,4}-?\\d{4}$", message = "올바른 전화번호 형식이 아닙니다.")
    private String phone;

    @Size(max = 100, message = "회사명은 100자 이하여야 합니다.")
    private String company;

    @Size(max = 50, message = "부서명은 50자 이하여야 합니다.")
    private String department;
}
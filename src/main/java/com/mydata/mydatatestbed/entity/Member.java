package com.mydata.mydatatestbed.entity;

import com.mydata.mydatatestbed.entity.Enum.MemberRole;
import com.mydata.mydatatestbed.vo.EmailVo;
import com.mydata.mydatatestbed.vo.PasswordVo;
import com.mydata.mydatatestbed.vo.PhoneVo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Embedded: @Embeddable로 선언된 VO를 이 Entity에 내장시킴.
     *            EmailVo의 value 필드가 members 테이블의 email 컬럼이 됨.
     */
    @Embedded
    private EmailVo email;

    @Embedded
    private PasswordVo password;

    @Column(nullable = false, length = 50)
    private String name;

    @Embedded
    private PhoneVo phone;

    @Column(length = 100)
    private String company;

    @Column(length = 50)
    private String department;

    /**
     * @Enumerated(EnumType.STRING): Enum을 DB에 문자열로 저장.
     *
     * EnumType.ORDINAL(기본값)은 0, 1, 2... 숫자로 저장하는데,
     * Enum 순서가 바뀌면 기존 데이터가 잘못된 값을 가리키게 됨.
     * 예: ROLE_ADMIN이 1번이었는데, 중간에 ROLE_MANAGER를 추가하면
     *     기존 ADMIN 사용자가 MANAGER로 바뀌어버림!
     *
     * STRING을 사용하면 "ROLE_USER", "ROLE_ADMIN" 문자열로 저장되어 안전함.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Column(nullable = false)
    private boolean emailVerified;

    @Column(nullable = false)
    private boolean phoneVerified;

    private LocalDateTime lastLoginAt;

    /**
     * 생성자에 @Builder를 붙이는 이유:
     *
     * 1. 클래스 레벨에 @Builder를 붙이면 모든 필드가 Builder 대상이 됨.
     *    → id, createdAt, updatedAt도 직접 설정 가능해져서 위험!
     *
     * 2. 생성자에 붙이면 필요한 필드만 선택적으로 Builder 대상으로 지정 가능.
     *    → id는 DB가 자동 생성 (@GeneratedValue)
     *    → createdAt/updatedAt은 JPA Auditing이 자동 생성
     *    → 개발자의 실수를 컴파일 단계에서 방지
     *
     * 3. role의 기본값 처리:
     *    Builder에서 role을 안 넣으면 null이 되는데,
     *    null 체크 후 기본값(ROLE_USER)을 할당하여 안전하게 처리.
     */
    @Builder
    public Member(EmailVo email, PasswordVo password, String name, PhoneVo phone,
                  String company, String department, MemberRole role,
                  boolean emailVerified, boolean phoneVerified) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.company = company;
        this.department = department;
        this.role = role != null ? role : MemberRole.ROLE_USER;
        this.emailVerified = emailVerified;
        this.phoneVerified = phoneVerified;
    }

    // === 비즈니스 메서드 ===

    public void verifyEmail() {
        this.emailVerified = true;
    }

    public void verifyPhone() {
        this.phoneVerified = true;
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public boolean isAdmin() {
        return this.role == MemberRole.ROLE_ADMIN;
    }
}
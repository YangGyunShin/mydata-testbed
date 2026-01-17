# 📋 마이데이터 테스트베드 프로젝트 진행 상황

> 작성일: 2025-01-17  
> 목표: 금융분야 마이데이터 테스트베드 클론 코딩

---

## 🎯 아키텍처 & 코딩 컨벤션

### 클린 아키텍처 원칙

```
┌─────────────────────────────────────────────────────────────┐
│                      Presentation Layer                      │
│                    (Controller, DTO, Mapper)                 │
├─────────────────────────────────────────────────────────────┤
│                      Application Layer                       │
│                         (Service)                            │
├─────────────────────────────────────────────────────────────┤
│                       Domain Layer                           │
│                    (Entity, VO, Repository)                  │
├─────────────────────────────────────────────────────────────┤
│                    Infrastructure Layer                      │
│               (JPA Implementation, External API)             │
└─────────────────────────────────────────────────────────────┘
```

### 코딩 컨벤션

| 규칙 | 설명 | 예시 |
|------|------|------|
| **No Factory Method** | DTO, Entity, VO에 정적 팩토리 메서드 사용 금지 | `of()`, `from()`, `create()` 사용 ❌ |
| **No Setter** | 모든 클래스에서 Setter 사용 금지 | `@Setter` 사용 ❌, `@Builder` 사용 ✅ |
| **Use Mapper** | DTO ↔ Entity 변환은 Mapper 클래스 사용 | `MemberMapper.toEntity()`, `MemberMapper.toResponseDto()` |
| **Use VO** | 핵심 값은 VO로 래핑하여 타입 안전성 확보 | `EmailVo`, `PasswordVo`, `PhoneVo` |
| **File Suffix** | 파일명에 역할을 나타내는 접미사 필수 | 아래 네이밍 규칙 참고 |

### 파일 네이밍 규칙

| 타입 | 접미사 | 예시 |
|------|--------|------|
| Entity | `Entity` 없이 도메인명만 | `Member.java`, `Notice.java` |
| VO | `Vo` | `EmailVo.java`, `PasswordVo.java` |
| DTO (Request) | `RequestDto` | `MemberSignupRequestDto.java` |
| DTO (Response) | `ResponseDto` | `MemberResponseDto.java` |
| DTO (공통) | `Dto` | `PageDto.java` |
| Mapper | `Mapper` | `MemberMapper.java` |
| Repository | `Repository` | `MemberRepository.java` |
| Service | `Service` | `MemberService.java` |
| Controller | `Controller` | `MemberController.java` |
| Config | `Config` | `SecurityConfig.java` |
| Exception | `Exception` | `MemberNotFoundException.java` |

---

## 📐 작성 규칙 예시

### VO 작성 규칙

```java
@Getter
@EqualsAndHashCode  // 값 기반 동등성 비교
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVo {

    private String value;

    @Builder
    private EmailVo(String value) {
        validate(value);
        this.value = value;
    }

    // 생성 시점에 검증 → 잘못된 값은 생성 자체가 불가능
    private void validate(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다");
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다");
        }
    }

    // 도메인 로직
    public String getDomain() {
        return value.split("@")[1];
    }

    public String getMasked() {
        return value.charAt(0) + "***@" + getDomain();
    }
}
```

### Entity 작성 규칙

```java
@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private EmailVo email;  // VO 사용으로 타입 안전성 확보

    @Embedded
    private PasswordVo password;

    @Embedded
    private PhoneVo phone;

    @Column(nullable = false, length = 50)
    private String name;

    @Builder
    private Member(EmailVo email, PasswordVo password, PhoneVo phone, String name) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.name = name;
    }

    // 비즈니스 메서드로 상태 변경 (Setter 대신)
    public void updatePassword(PasswordVo newPassword) {
        this.password = newPassword;
    }
}
```

### DTO 작성 규칙

```java
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSignupRequestDto {

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    private String password;

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    private String phone;

    @Builder
    private MemberSignupRequestDto(String email, String password, String name, String phone) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
    }
}
```

### Mapper 작성 규칙

```java
@Component
@RequiredArgsConstructor
public class MemberMapper {

    private final PasswordEncoder passwordEncoder;

    // RequestDto → Entity (VO 변환 포함)
    public Member toEntity(MemberSignupRequestDto dto) {
        return Member.builder()
                .email(EmailVo.builder().value(dto.getEmail()).build())
                .password(PasswordVo.builder()
                        .value(passwordEncoder.encode(dto.getPassword()))
                        .build())
                .phone(dto.getPhone() != null 
                        ? PhoneVo.builder().value(dto.getPhone()).build() 
                        : null)
                .name(dto.getName())
                .build();
    }

    // Entity → ResponseDto
    public MemberResponseDto toResponseDto(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail().getValue())
                .name(member.getName())
                .phone(member.getPhone() != null ? member.getPhone().getMasked() : null)
                .build();
    }

    // Entity List → ResponseDto List
    public List<MemberResponseDto> toResponseDtoList(List<Member> members) {
        return members.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}
```

---

## ✅ ASIS (완료된 작업)

### 1. 프로젝트 설정

| 파일 | 상태 | 설명 |
|------|------|------|
| `build.gradle` | ✅ 완료 | Spring Boot 3.4.1, Java 21, 의존성 설정 |
| `application.yml` | ✅ 완료 | DB, JPA, Thymeleaf, Mail 설정 |
| `README.md` | ✅ 완료 | 프로젝트 문서화 |

### 2. 프론트엔드 - 레이아웃

| 파일 | 경로 | 상태 |
|------|------|------|
| `default-layout.html` | `templates/layout/` | ✅ 완료 |
| `header.html` | `templates/layout/` | ✅ 완료 |
| `footer.html` | `templates/layout/` | ✅ 완료 |
| `sidebar.html` | `templates/layout/` | ✅ 완료 |

### 3. 프론트엔드 - Fragments

| 파일 | 경로 | 상태 |
|------|------|------|
| `breadcrumb.html` | `templates/fragments/` | ✅ 완료 |
| `pagination.html` | `templates/fragments/` | ✅ 완료 |
| `page-banner.html` | `templates/fragments/` | ✅ 완료 |

### 4. 프론트엔드 - 페이지

| 파일 | 경로 | 상태 |
|------|------|------|
| `index.html` | `templates/main/` | ✅ 완료 |

### 5. 프론트엔드 - CSS

| 파일 | 경로 | 상태 |
|------|------|------|
| `common.css` | `static/css/` | ✅ 완료 |
| `header.css` | `static/css/` | ✅ 완료 |
| `footer.css` | `static/css/` | ✅ 완료 |
| `sidebar.css` | `static/css/` | ✅ 완료 |
| `main.css` | `static/css/` | ✅ 완료 |
| `sub-page.css` | `static/css/` | ✅ 완료 |
| `form.css` | `static/css/` | ✅ 완료 |

### 6. 프론트엔드 - JavaScript

| 파일 | 경로 | 상태 |
|------|------|------|
| `common.js` | `static/js/` | ✅ 완료 |
| `main.js` | `static/js/` | ✅ 완료 |

---

## 📝 TODO (해야 할 작업)

### Phase 1: 실행 가능한 기본 구조 (필수)

#### 1.1 Config 클래스

| 파일 | 경로 | 우선순위 | 설명 |
|------|------|----------|------|
| `SecurityConfig.java` | `config/` | 🔴 높음 | Spring Security 설정 |
| `WebConfig.java` | `config/` | 🟡 중간 | Web MVC 설정 |
| `AuditConfig.java` | `config/` | 🟡 중간 | JPA Auditing 설정 |

#### 1.2 기본 Controller

| 파일 | 경로 | 우선순위 | 설명 |
|------|------|----------|------|
| `MainController.java` | `controller/` | 🔴 높음 | 메인 페이지 (`/`) |

---

### Phase 2: 정적 페이지

#### 2.1 Controller

| 파일 | URL | 우선순위 |
|------|-----|----------|
| `IntroController.java` | `/intro/**` | 🟡 중간 |
| `ApiGuideController.java` | `/api-guide/**` | 🟡 중간 |
| `TestbedController.java` | `/testbed/**` | 🟢 낮음 |
| `ConformanceController.java` | `/conformance/**` | 🟢 낮음 |

#### 2.2 템플릿

| 파일 | 경로 | 설명 |
|------|------|------|
| `service.html` | `templates/intro/` | 마이데이터 서비스 소개 |
| `testbed.html` | `templates/intro/` | 테스트베드 소개 |
| `guide.html` | `templates/api-guide/` | API 가이드 |
| `auth-api.html` | `templates/api-guide/` | 인증 API 규격 |
| `support-api.html` | `templates/api-guide/` | 지원 API 규격 |
| `info-api.html` | `templates/api-guide/` | 정보제공 API 규격 |
| `service-test.html` | `templates/testbed/` | 서비스 테스트 |
| `api-test.html` | `templates/testbed/` | API 서버 테스트 |
| `functional.html` | `templates/conformance/` | 기능적합성 심사 |
| `security.html` | `templates/conformance/` | 보안취약점 점검 |

---

### Phase 3: 회원 기능

#### 3.1 Value Objects (VO)

| 파일 | 경로 | 설명 | 포함 로직 |
|------|------|------|----------|
| `EmailVo.java` | `domain/vo/` | 이메일 값 객체 | 형식 검증, 도메인 추출, 마스킹 |
| `PasswordVo.java` | `domain/vo/` | 비밀번호 값 객체 | 강도 검증 |
| `PhoneVo.java` | `domain/vo/` | 전화번호 값 객체 | 형식 검증, 마스킹 |

#### 3.2 Domain (Entity)

| 파일 | 경로 | 설명 |
|------|------|------|
| `BaseTimeEntity.java` | `domain/` | 공통 시간 필드 (createdAt, updatedAt) |
| `Member.java` | `domain/` | 회원 엔티티 (VO 사용) |
| `MemberRole.java` | `domain/enums/` | 회원 권한 enum |

#### 3.3 Repository

| 파일 | 경로 | 설명 |
|------|------|------|
| `MemberRepository.java` | `repository/` | 회원 JPA Repository |

#### 3.4 DTO

| 파일 | 경로 | 설명 |
|------|------|------|
| `MemberSignupRequestDto.java` | `dto/member/` | 회원가입 요청 |
| `MemberLoginRequestDto.java` | `dto/member/` | 로그인 요청 |
| `MemberResponseDto.java` | `dto/member/` | 회원 정보 응답 |

#### 3.5 Mapper

| 파일 | 경로 | 설명 |
|------|------|------|
| `MemberMapper.java` | `mapper/` | Member ↔ DTO 변환 (VO 변환 포함) |

#### 3.6 Service

| 파일 | 경로 | 설명 |
|------|------|------|
| `MemberService.java` | `service/` | 회원 비즈니스 로직 |
| `EmailService.java` | `service/` | 이메일 발송 |

#### 3.7 Security

| 파일 | 경로 | 설명 |
|------|------|------|
| `CustomUserDetails.java` | `security/` | UserDetails 구현 |
| `CustomUserDetailsService.java` | `security/` | UserDetailsService 구현 |
| `LoginFailureHandler.java` | `security/` | 로그인 실패 핸들러 |

#### 3.8 Controller

| 파일 | 경로 | URL |
|------|------|-----|
| `MemberController.java` | `controller/` | `/member/**` |

#### 3.9 템플릿

| 파일 | 경로 | 설명 |
|------|------|------|
| `login.html` | `templates/member/` | 로그인 |
| `signup-step1-terms.html` | `templates/member/` | 회원가입 1단계 - 약관동의 |
| `signup-step2-phone.html` | `templates/member/` | 회원가입 2단계 - 휴대폰인증 |
| `signup-step3-info.html` | `templates/member/` | 회원가입 3단계 - 정보입력 |
| `signup-step4-email.html` | `templates/member/` | 회원가입 4단계 - 이메일인증 |
| `signup-complete.html` | `templates/member/` | 회원가입 완료 |

---

### Phase 4: 게시판 기능

#### 4.1 Domain (Entity)

| 파일 | 경로 | 설명 |
|------|------|------|
| `Notice.java` | `domain/` | 공지사항 |
| `Faq.java` | `domain/` | FAQ |
| `FaqCategory.java` | `domain/enums/` | FAQ 카테고리 enum |
| `Inquiry.java` | `domain/` | 문의 |
| `InquiryStatus.java` | `domain/enums/` | 문의 상태 enum |
| `Resource.java` | `domain/` | 자료실 |
| `Board.java` | `domain/` | 자유게시판 |

#### 4.2 Repository

| 파일 | 경로 |
|------|------|
| `NoticeRepository.java` | `repository/` |
| `FaqRepository.java` | `repository/` |
| `InquiryRepository.java` | `repository/` |
| `ResourceRepository.java` | `repository/` |
| `BoardRepository.java` | `repository/` |

#### 4.3 DTO

| 파일 | 경로 | 설명 |
|------|------|------|
| `NoticeListResponseDto.java` | `dto/notice/` | 공지사항 목록 |
| `NoticeDetailResponseDto.java` | `dto/notice/` | 공지사항 상세 |
| `FaqResponseDto.java` | `dto/faq/` | FAQ |
| `InquiryRequestDto.java` | `dto/inquiry/` | 문의 등록 요청 |
| `InquiryResponseDto.java` | `dto/inquiry/` | 문의 응답 |
| `ResourceResponseDto.java` | `dto/resource/` | 자료실 |
| `BoardRequestDto.java` | `dto/board/` | 게시글 작성 요청 |
| `BoardResponseDto.java` | `dto/board/` | 게시글 응답 |
| `PageResponseDto.java` | `dto/common/` | 페이징 공통 |

#### 4.4 Mapper

| 파일 | 경로 |
|------|------|
| `NoticeMapper.java` | `mapper/` |
| `FaqMapper.java` | `mapper/` |
| `InquiryMapper.java` | `mapper/` |
| `ResourceMapper.java` | `mapper/` |
| `BoardMapper.java` | `mapper/` |

#### 4.5 Service

| 파일 | 경로 |
|------|------|
| `NoticeService.java` | `service/` |
| `FaqService.java` | `service/` |
| `InquiryService.java` | `service/` |
| `ResourceService.java` | `service/` |
| `BoardService.java` | `service/` |

#### 4.6 Controller

| 파일 | 경로 | URL |
|------|------|-----|
| `SupportController.java` | `controller/` | `/support/**` |

#### 4.7 템플릿

| 파일 | 경로 | 설명 |
|------|------|------|
| `notice-list.html` | `templates/support/` | 공지사항 목록 |
| `notice-detail.html` | `templates/support/` | 공지사항 상세 |
| `faq.html` | `templates/support/` | FAQ |
| `inquiry-form.html` | `templates/support/` | 문의 작성 |
| `inquiry-list.html` | `templates/support/` | 내 문의 목록 |
| `resource-list.html` | `templates/support/` | 자료실 |
| `board-list.html` | `templates/support/` | 자유게시판 목록 |
| `board-detail.html` | `templates/support/` | 자유게시판 상세 |
| `board-form.html` | `templates/support/` | 게시글 작성 |

---

### Phase 5: 예외 처리 & 완성도

#### 5.1 Exception

| 파일 | 경로 | 설명 |
|------|------|------|
| `GlobalExceptionHandler.java` | `exception/` | 전역 예외 처리 |
| `MemberNotFoundException.java` | `exception/` | 회원 없음 예외 |
| `DuplicateEmailException.java` | `exception/` | 이메일 중복 예외 |
| `UnauthorizedException.java` | `exception/` | 인증 실패 예외 |

#### 5.2 에러 페이지

| 파일 | 경로 | 설명 |
|------|------|------|
| `404.html` | `templates/error/` | Not Found |
| `500.html` | `templates/error/` | Internal Server Error |
| `403.html` | `templates/error/` | Forbidden |

#### 5.3 기타

| 파일 | 경로 | 설명 |
|------|------|------|
| `validation.js` | `static/js/` | 클라이언트 폼 검증 |
| `signup.js` | `static/js/` | 회원가입 전용 스크립트 |

---

## 📁 최종 디렉토리 구조

```
src/main/java/com/mydata/mydatatestbed/
├── MydataTestbedApplication.java
│
├── config/
│   ├── SecurityConfig.java
│   ├── WebConfig.java
│   └── AuditConfig.java
│
├── controller/
│   ├── MainController.java
│   ├── IntroController.java
│   ├── ApiGuideController.java
│   ├── TestbedController.java
│   ├── ConformanceController.java
│   ├── SupportController.java
│   └── MemberController.java
│
├── domain/
│   ├── BaseTimeEntity.java
│   ├── Member.java
│   ├── Notice.java
│   ├── Faq.java
│   ├── Inquiry.java
│   ├── Resource.java
│   ├── Board.java
│   ├── vo/                          # Value Objects
│   │   ├── EmailVo.java
│   │   ├── PasswordVo.java
│   │   └── PhoneVo.java
│   └── enums/
│       ├── MemberRole.java
│       ├── FaqCategory.java
│       └── InquiryStatus.java
│
├── repository/
│   ├── MemberRepository.java
│   ├── NoticeRepository.java
│   ├── FaqRepository.java
│   ├── InquiryRepository.java
│   ├── ResourceRepository.java
│   └── BoardRepository.java
│
├── dto/
│   ├── member/
│   │   ├── MemberSignupRequestDto.java
│   │   ├── MemberLoginRequestDto.java
│   │   └── MemberResponseDto.java
│   ├── notice/
│   │   ├── NoticeListResponseDto.java
│   │   └── NoticeDetailResponseDto.java
│   ├── faq/
│   │   └── FaqResponseDto.java
│   ├── inquiry/
│   │   ├── InquiryRequestDto.java
│   │   └── InquiryResponseDto.java
│   ├── resource/
│   │   └── ResourceResponseDto.java
│   ├── board/
│   │   ├── BoardRequestDto.java
│   │   └── BoardResponseDto.java
│   └── common/
│       └── PageResponseDto.java
│
├── mapper/
│   ├── MemberMapper.java
│   ├── NoticeMapper.java
│   ├── FaqMapper.java
│   ├── InquiryMapper.java
│   ├── ResourceMapper.java
│   └── BoardMapper.java
│
├── service/
│   ├── MemberService.java
│   ├── NoticeService.java
│   ├── FaqService.java
│   ├── InquiryService.java
│   ├── ResourceService.java
│   ├── BoardService.java
│   └── EmailService.java
│
├── security/
│   ├── CustomUserDetails.java
│   ├── CustomUserDetailsService.java
│   └── LoginFailureHandler.java
│
└── exception/
    ├── GlobalExceptionHandler.java
    ├── MemberNotFoundException.java
    ├── DuplicateEmailException.java
    └── UnauthorizedException.java
```

---

## 🚀 실행 체크리스트

### Phase 1 완료 후 확인사항
- [ ] 서버 실행 (`./gradlew bootRun`)
- [ ] 메인 페이지 접속 (`http://localhost:8080`)
- [ ] H2 콘솔 접속 (`http://localhost:8080/h2-console`)

### Phase 2 완료 후 확인사항
- [ ] 모든 메뉴 네비게이션 동작
- [ ] 소개 페이지 접근
- [ ] API 가이드 페이지 접근

### Phase 3 완료 후 확인사항
- [ ] VO 검증 로직 동작 (잘못된 이메일/비밀번호/전화번호 입력 시 예외 발생)
- [ ] 회원가입 4단계 프로세스 동작
- [ ] 로그인/로그아웃 동작
- [ ] 인증된 사용자만 테스트베드 접근

### Phase 4 완료 후 확인사항
- [ ] 공지사항 목록/상세 조회
- [ ] FAQ 아코디언 동작
- [ ] 문의 등록 및 목록 조회
- [ ] 자료 다운로드
- [ ] 게시판 CRUD

---

## 📅 예상 일정

| Phase | 작업 내용 | 예상 소요 |
|-------|----------|----------|
| Phase 1 | 실행 가능한 기본 구조 | 1일 |
| Phase 2 | 정적 페이지 | 2-3일 |
| Phase 3 | 회원 기능 (VO 포함) | 4-5일 |
| Phase 4 | 게시판 기능 | 4-5일 |
| Phase 5 | 예외 처리 & 완성도 | 2일 |
| **Total** | | **약 2주** |

---

## 📌 참고사항

1. **VO 사용 이점**
   - 타입 안전성: String 대신 EmailVo를 사용하여 컴파일 타임에 오류 검출
   - 검증 로직 집중: 생성 시점에 검증하여 잘못된 값의 시스템 침투 방지
   - 도메인 로직 응집: 마스킹, 도메인 추출 등의 로직을 VO 내부에 캡슐화

2. **MapStruct 사용 고려**: 현재는 수동 Mapper를 사용하지만, 추후 MapStruct 라이브러리 도입 검토 가능

3. **테스트 코드**: 각 Phase 완료 후 단위 테스트 작성 권장 (특히 VO 검증 로직)

4. **API 문서화**: Phase 4 완료 후 Swagger 도입 검토

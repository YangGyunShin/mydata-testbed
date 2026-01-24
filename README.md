# 🏦 금융분야 마이데이터 테스트베드

> 금융보안원 마이데이터 테스트베드 클론 프로젝트  
> 원본 사이트: https://developers.mydatakorea.org/mdtb/

마이데이터 서비스 개발자를 위한 API 테스트 환경을 제공하는 웹 애플리케이션입니다.

---

## 📋 목차

- [프로젝트 개요](#프로젝트-개요)
- [기술 스택](#기술-스택)
- [아키텍처 & 코딩 컨벤션](#아키텍처--코딩-컨벤션)
- [시스템 아키텍처](#시스템-아키텍처)
- [프로젝트 구조](#프로젝트-구조)
- [메뉴 구조](#메뉴-구조)
- [ERD](#erd)
- [이메일 인증 플로우](#이메일-인증-플로우)
- [공지사항 기능](#공지사항-기능)
- [실행 방법](#실행-방법)
- [개발 로드맵](#개발-로드맵)

---

## 프로젝트 개요

### 주요 기능

| 기능 | 설명 | 상태 |
|------|------|------|
| **테스트베드 소개** | 마이데이터 서비스 및 테스트베드 소개 | 예정 |
| **API 가이드** | 데이터 표준 API 규격, 인증/지원/정보제공 API 규격 문서 | 예정 |
| **테스트베드** | 마이데이터 서비스 테스트, API 서버 테스트 | 예정 |
| **적합성 심사** | 기능적합성 심사, 보안취약점 결과 점검 | 예정 |
| **고객지원** | 공지사항, FAQ, 문의하기, 자료실, 자유게시판 | 공지사항 ✅ FAQ ✅ |
| **회원관리** | 회원가입(4단계), 로그인/로그아웃, 이메일 인증 | ✅ 완료 |

---

## 기술 스택

### Backend
| 기술 | 버전 | 설명 |
|------|------|------|
| Java | 21 | 프로그래밍 언어 |
| Spring Boot | 3.4.1 | 웹 프레임워크 |
| Spring Security | 6.x | 인증/인가 (Form Login 방식) |
| Spring Data JPA | - | ORM |
| Spring Validation | - | Bean Validation (커스텀 어노테이션 포함) |
| Spring Mail | - | 이메일 발송 (Gmail SMTP) |
| Thymeleaf | - | 템플릿 엔진 |
| Thymeleaf Layout Dialect | - | 레이아웃 템플릿 |
| Lombok | - | 보일러플레이트 코드 제거 |

### Database
| 기술 | 용도 | 비고 |
|------|------|------|
| H2 Database (파일 모드) | 개발/테스트 환경 | 서버 재시작해도 데이터 유지 |
| MySQL | 운영 환경 | - |

### Frontend
| 기술 | 설명 |
|------|------|
| HTML5 / CSS3 | 마크업 & 스타일링 |
| JavaScript | 클라이언트 스크립트 |
| Thymeleaf | 서버사이드 템플릿 |

### Build & Tools
| 도구 | 설명 |
|------|------|
| Gradle | 빌드 도구 |
| IntelliJ IDEA | IDE |
| Git | 버전 관리 |

---

## 아키텍처 & 코딩 컨벤션

### 클린 아키텍처 원칙

| 규칙 | 설명 |
|------|------|
| **No Factory Method** | DTO, Entity, VO에 `of()`, `from()` 등 정적 팩토리 메서드 사용 금지 |
| **No Setter** | 모든 클래스에서 Setter 사용 금지, `@Builder` 패턴 사용 |
| **Use Mapper** | DTO ↔ Entity 변환은 별도 Mapper 클래스 사용 |
| **Use VO** | 핵심 값 객체(Email, Password, Phone)는 VO로 래핑하여 타입 안전성 확보 |
| **Domain Logic in Entity/VO** | 도메인 규칙은 Entity와 VO에 캡슐화 (예: 이메일 형식 검증은 VO에서) |
| **Use Case in Service** | Service는 유스케이스 조합만 담당, 인프라 의존성 조율 |

### 파일 네이밍 규칙

| 타입 | 접미사 | 예시 |
|------|--------|------|
| Entity | 도메인명만 | `Member.java`, `Notice.java` |
| VO | `Vo` | `EmailVo.java` |
| DTO (Request) | `RequestDto` | `MemberSignupRequestDto.java` |
| DTO (Response) | `ResponseDto` | `NoticeListResponseDto.java` |
| Mapper | `Mapper` | `NoticeMapper.java` |
| Service Interface | `Service` | `NoticeService.java` |
| Service 구현체 | `ServiceImpl` | `NoticeServiceImpl.java` |

### Lombok 어노테이션 패턴

| 클래스 종류 | 어노테이션 | 이유 |
|------------|-----------|------|
| **Entity** | `@Getter @NoArgsConstructor(access = PROTECTED)` + 생성자에 `@Builder` | id, 시간 필드 제외 |
| **VO** | `@Getter @NoArgsConstructor(access = PROTECTED)` + 생성자에 `@Builder` | 검증 로직 포함 |
| **ResponseDto** | `@Getter @Builder` | Mapper에서 Builder로만 생성 |
| **RequestDto** | `@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor` | Spring MVC 바인딩 필요 |

---

## 시스템 아키텍처
```
┌─────────────────────────────────────────────────────────────────┐
│                          Client (Browser)                        │
└─────────────────────────────┬───────────────────────────────────┘
                              │ HTTP Request
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Spring Boot Application                     │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                    Presentation Layer                      │  │
│  │              Controller, Thymeleaf Templates               │  │
│  └───────────────────────────────────────────────────────────┘  │
│                              │                                   │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                     Validation Layer                       │  │
│  │      DTO (@Valid), Custom Annotation (@PasswordMatching)   │  │
│  └───────────────────────────────────────────────────────────┘  │
│                              │                                   │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                     Security Layer                         │  │
│  │     Spring Security, CustomUserDetails, Form Login         │  │
│  └───────────────────────────────────────────────────────────┘  │
│                              │                                   │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                   Application Layer                        │  │
│  │              Service (Use Case), Mapper                    │  │
│  └───────────────────────────────────────────────────────────┘  │
│                              │                                   │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                      Domain Layer                          │  │
│  │            Entity, VO (핵심 비즈니스 규칙 포함)              │  │
│  └───────────────────────────────────────────────────────────┘  │
│                              │                                   │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                  Infrastructure Layer                      │  │
│  │                 Repository, EmailSender                    │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────┬───────────────────────────────────┘
                              │ JDBC / SMTP
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              Database (H2 / MySQL) + Gmail SMTP                  │
└─────────────────────────────────────────────────────────────────┘
```

### 레이어별 책임

| Layer | 책임 | 주요 컴포넌트 |
|-------|------|--------------|
| **Presentation** | HTTP 요청/응답 처리, 뷰 렌더링 | Controller, Thymeleaf |
| **Validation** | 입력값 검증 | DTO, Custom Annotation |
| **Security** | 인증/인가, 세션 관리 | Spring Security, UserDetails |
| **Application** | 유스케이스 구현, 객체 변환 | Service, Mapper |
| **Domain** | 핵심 비즈니스 규칙 | Entity, VO |
| **Infrastructure** | 데이터 영속성, 외부 서비스 연동 | Repository, JavaMailSender |

---

## 프로젝트 구조
```
src/main/java/com/mydata/mydatatestbed/
├── MydataTestbedApplication.java
│
├── config/                              # 설정
│   ├── AuditConfig.java                 # JPA Auditing 설정
│   └── SecurityConfig.java              # Spring Security 설정
│
├── controller/                          # 컨트롤러
│   ├── MainController.java              # 메인 페이지 + 공지사항 연동
│   ├── MemberController.java            # 회원가입/로그인/이메일인증
│   └── SupportController.java           # 고객지원 (공지사항, FAQ 등)
│
├── entity/                              # Entity
│   ├── BaseTimeEntity.java              # 공통 시간 필드 (createdAt, updatedAt)
│   ├── Member.java                      # 회원 Entity
│   ├── Notice.java                      # 공지사항 Entity
│   ├── Faq.java                         # FAQ Entity
│   ├── EmailVerificationToken.java      # 이메일 인증 토큰 Entity
│   └── Enum/
│       ├── MemberRole.java              # 회원 권한 Enum
│       └── FaqCategory.java             # FAQ 카테고리 Enum
│
├── vo/                                  # Value Objects
│   ├── EmailVo.java                     # 이메일 VO (형식 검증 포함)
│   ├── PasswordVo.java                  # 비밀번호 VO
│   └── PhoneVo.java                     # 전화번호 VO (형식 검증 포함)
│
├── repository/                          # Repository
│   ├── MemberRepository.java
│   ├── NoticeRepository.java            # 공지사항 Repository
│   ├── FaqRepository.java               # FAQ Repository
│   └── EmailVerificationTokenRepository.java
│
├── dto/                                 # DTO
│   ├── member/
│   │   ├── MemberSignupRequestDto.java
│   │   └── MemberResponseDto.java
│   ├── notice/
│   │   ├── NoticeListResponseDto.java   # 공지사항 목록 응답 DTO
│   │   └── NoticeDetailResponseDto.java # 공지사항 상세 응답 DTO
│   └── faq/
│       └── FaqResponseDto.java          # FAQ 응답 DTO
│
├── mapper/                              # Mapper (DTO ↔ Entity 변환)
│   ├── MemberMapper.java
│   ├── NoticeMapper.java                # 공지사항 Mapper
│   └── FaqMapper.java                   # FAQ Mapper
│
├── service/                             # Service
│   ├── MemberService.java
│   ├── EmailService.java
│   ├── NoticeService.java               # 공지사항 서비스 인터페이스
│   ├── FaqService.java                  # FAQ 서비스 인터페이스
│   └── impl/
│       ├── MemberServiceImpl.java
│       ├── EmailServiceImpl.java
│       ├── NoticeServiceImpl.java       # 공지사항 서비스 구현체
│       └── FaqServiceImpl.java          # FAQ 서비스 구현체
│
├── security/                            # Spring Security
│   ├── CustomUserDetails.java
│   └── CustomUserDetailsService.java
│
└── validation/                          # 커스텀 Validation
    ├── PasswordMatching.java
    └── PasswordMatchingValidator.java
```

### 프론트엔드 구조
```
src/main/resources/
├── templates/
│   ├── layout/
│   │   ├── default-layout.html          # 기본 레이아웃
│   │   ├── header.html                  # 헤더
│   │   ├── footer.html                  # 푸터
│   │   └── sidebar.html                 # 사이드바
│   ├── fragments/
│   │   ├── breadcrumb.html              # 브레드크럼
│   │   ├── pagination.html              # 페이지네이션
│   │   └── page-banner.html             # 페이지 배너
│   ├── main/
│   │   └── index.html                   # 메인 페이지
│   ├── member/
│   │   ├── login.html
│   │   ├── signup-step1-terms.html
│   │   ├── signup-step2-phone.html
│   │   ├── signup-step3-info.html
│   │   ├── signup-step4-email.html
│   │   ├── verify-email-success.html
│   │   └── verify-email-failed.html
│   ├── support/                         # 고객지원 템플릿
│   │   ├── notice-list.html             # 공지사항 목록
│   │   └── notice-detail.html           # 공지사항 상세
│   └── error/
│
└── static/
    ├── css/
    │   ├── common.css
    │   ├── header.css
    │   ├── footer.css
    │   ├── sidebar.css
    │   ├── main.css
    │   ├── sub-page.css
    │   └── form.css
    └── js/
        ├── common.js
        └── main.js
```

---

## 메뉴 구조
```
📁 마이데이터 테스트베드
│
├── 🏠 메인 (최신 공지사항 3개 표시)
│
├── 📖 테스트베드 소개
│   ├── 마이데이터 서비스 소개
│   └── 마이데이터 테스트베드 소개
│
├── 📚 API 가이드
│   ├── API 가이드 (기본규격/인증규격/처리절차)
│   ├── 마이데이터 인증 API 규격
│   ├── 마이데이터 지원 API 규격
│   └── 마이데이터 정보제공 API 규격
│
├── 🧪 테스트베드
│   ├── 마이데이터 서비스 테스트
│   └── API 서버 테스트
│
├── ✅ 적합성 심사
│   ├── 기능적합성 심사
│   └── 보안취약점 결과 점검
│
├── 💬 고객지원
│   ├── 공지사항 ✅ (목록/상세/검색/페이징)
│   ├── FAQ ✅ (카테고리 필터링, 아코디언 UI)
│   ├── 문의하기
│   ├── 자료실
│   └── 자유게시판
│
└── 👤 회원
    ├── 로그인 ✅
    └── 회원가입 (4단계) ✅
        ├── Step 1: 약관동의
        ├── Step 2: 휴대폰 인증
        ├── Step 3: 회원정보 입력
        └── Step 4: 이메일 인증
```

---

## ERD
```
┌──────────────────┐       ┌──────────────────────────┐
│     members      │       │ email_verification_tokens│
├──────────────────┤       ├──────────────────────────┤
│ id (PK)          │       │ id (PK)                  │
│ email (UK)       │◄──────│ email                    │
│ password         │       │ token (UK)               │
│ name             │       │ expires_at               │
│ phone            │       │ created_at               │
│ company          │       └──────────────────────────┘
│ department       │
│ role             │       ┌──────────────────┐
│ email_verified   │       │     notices      │
│ phone_verified   │       ├──────────────────┤
│ last_login_at    │       │ id (PK)          │
│ created_at       │       │ title            │
│ updated_at       │       │ content          │
└──────────────────┘       │ pinned           │
         │                 │ view_count       │
         │                 │ attachment_path  │
         │                 │ attachment_name  │
         ├────────────────►│ author_id (FK)   │
         │                 │ created_at       │
         │                 │ updated_at       │
         │                 └──────────────────┘
         │
         │                 ┌──────────────────┐
         │                 │      faqs        │  ✅ 완료
         │                 ├──────────────────┤
         │                 │ id (PK)          │
         │                 │ category         │
         │                 │ question         │
         │                 │ answer           │
         │                 │ order_num        │
         │                 │ active           │
         │                 │ created_at       │
         │                 │ updated_at       │
         │                 └──────────────────┘
         │
         │                 ┌──────────────────┐
         │                 │    inquiries     │  (예정)
         │                 └──────────────────┘
         │
         │                 ┌──────────────────┐
         │                 │    resources     │  (예정)
         │                 └──────────────────┘
         │
         │                 ┌──────────────────┐
         │                 │      boards      │  (예정)
         └────────────────►└──────────────────┘
```

---

## 이메일 인증 플로우

회원가입 시 이메일 인증을 통해 실제 사용자임을 확인합니다.

### 전체 플로우
```
[Step 3 완료] 회원정보 입력 후 "다음" 버튼 클릭
      │
      ▼
┌─────────────────────────────────────────────────────────────────┐
│  MemberController.signupStep3Process()                           │
│    ├─→ MemberService.signup() - 회원 생성 (emailVerified=false)  │
│    └─→ EmailService.sendVerificationEmail() - 인증 메일 발송      │
└─────────────────────────────────────────────────────────────────┘
      │
      ▼
[Step 4] 이메일 인증 대기 화면 (재발송 버튼)
      │
      │  사용자가 메일함 확인 후 인증 링크 클릭
      ▼
┌─────────────────────────────────────────────────────────────────┐
│  MemberController.verifyEmail(token)                             │
│    ├─→ EmailService.verifyToken(token) - 토큰 검증               │
│    └─→ MemberService.verifyEmail(email) - emailVerified = true   │
└─────────────────────────────────────────────────────────────────┘
      │
      ▼
[인증 완료] → [로그인 가능]
```

### 왜 이메일 인증이 필요한가?

```java
// CustomUserDetails.java
@Override
public boolean isEnabled() {
    return member.isEmailVerified();  // false면 로그인 불가
}
```

---

## 공지사항 기능

### 기능 목록

| 기능 | URL | 설명 |
|------|-----|------|
| 목록 조회 | `GET /support/notice` | 페이징, 중요 공지 상단 고정 |
| 검색 | `GET /support/notice?keyword=검색어` | 제목/내용 검색 |
| 상세 조회 | `GET /support/notice/{id}` | 조회수 자동 증가 |
| 메인 페이지 | `GET /` | 최신 공지사항 3개 표시 |

### 조회 흐름
```
[헤더] 공지사항 클릭
      │
      │ GET /support/notice
      ▼
┌─────────────────────────────────────────────────────────────────┐
│  SupportController.noticeList()                                  │
│    └─→ NoticeService.getNoticeList(pageable)                    │
│          └─→ NoticeRepository.findAllOrderByPinnedAndCreatedAt()│
└─────────────────────────────────────────────────────────────────┘
      │
      ▼
[notice-list.html] 목록 표시
      │
      │ 특정 글 클릭: GET /support/notice/5
      ▼
┌─────────────────────────────────────────────────────────────────┐
│  SupportController.noticeDetail(id=5)                            │
│    └─→ NoticeService.getNoticeDetailWithViewCount(5)            │
│          ├─→ NoticeRepository.findByIdWithAuthor(5)             │
│          └─→ notice.incrementViewCount() (조회수 증가)           │
└─────────────────────────────────────────────────────────────────┘
      │
      ▼
[notice-detail.html] 상세 표시
```

### Notice Entity 주요 코드

```java
@Entity
@Table(name = "notices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean pinned;  // 중요 공지 (상단 고정)

    @Column(nullable = false)
    private int viewCount;

    /**
     * 작성자
     * - @JoinColumn(name = "author_id"): notices 테이블에 author_id FK 컬럼 생성
     * - FetchType.LAZY: 실제 사용 시점에 Member 조회 (@ManyToOne 기본값 EAGER 대신)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Member author;

    // === 비즈니스 메서드 === //

    public void incrementViewCount() {
        this.viewCount++;
    }
}
```

### NoticeRepository 주요 쿼리

```java
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 목록 조회 (중요 공지 먼저, 그 다음 최신순)
    @Query("SELECT n FROM Notice n ORDER BY n.pinned DESC, n.createdAt DESC")
    Page<Notice> findAllOrderByPinnedAndCreatedAt(Pageable pageable);

    // 상세 조회 (작성자 함께 조회 - N+1 방지)
    @Query("SELECT n FROM Notice n LEFT JOIN FETCH n.author WHERE n.id = :id")
    Optional<Notice> findByIdWithAuthor(@Param("id") Long id);
}
```

---

## FAQ 기능

### 기능 목록

| 기능 | URL | 설명 |
|------|-----|------|
| 전체 조회 | `GET /support/faq` | 모든 활성화된 FAQ 목록 |
| 카테고리 필터 | `GET /support/faq?category=GENERAL` | 특정 카테고리 FAQ만 조회 |

### 카테고리 종류

| Enum 값 | 한글 표시명 | 설명 |
|---------|-----------|------|
| `GENERAL` | 일반 | 일반적인 질문 |
| `SIGNUP` | 회원가입 | 회원가입 관련 질문 |
| `API` | API | API 사용 관련 질문 |
| `TEST` | 테스트 | 테스트베드 사용 관련 질문 |
| `CONFORMANCE` | 적합성심사 | 적합성 심사 관련 질문 |

### 조회 흐름
```
[헤더] FAQ 클릭
      │
      │ GET /support/faq
      ▼
┌─────────────────────────────────────────────────────────────────┐
│  SupportController.faq()                                         │
│    └─→ FaqService.getAllFaqs()                                  │
│          └─→ FaqRepository.findAllActiveOrderByOrderNum()       │
└─────────────────────────────────────────────────────────────────┘
      │
      ▼
[faq.html] 전체 FAQ 표시 (아코디언 UI)
      │
      │ [일반] 탭 클릭: GET /support/faq?category=GENERAL
      ▼
┌─────────────────────────────────────────────────────────────────┐
│  SupportController.faq(category=GENERAL)                         │
│    └─→ FaqService.getFaqsByCategory(GENERAL)                    │
│          └─→ FaqRepository.findByCategoryAndActiveOrderByOrderNum()│
└─────────────────────────────────────────────────────────────────┘
      │
      ▼
[faq.html] 일반 카테고리 FAQ만 표시
```

### Faq Entity 주요 코드

```java
@Entity
@Table(name = "faqs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Faq extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FaqCategory category;

    @Column(nullable = false, length = 500)
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(nullable = false)
    private int orderNum;  // 정렬 순서

    @Column(nullable = false)
    private boolean active = true;  // 활성화 여부

    // === 비즈니스 메서드 === //
    public void update(FaqCategory category, String question, String answer, int orderNum) { ... }
    public void toggleActive() { ... }
    public void deactivate() { ... }
    public void activate() { ... }
}
```

### FaqRepository 주요 쿼리

```java
public interface FaqRepository extends JpaRepository<Faq, Long> {

    // 활성화된 전체 FAQ 조회 (정렬순)
    @Query("SELECT f FROM Faq f WHERE f.active = true ORDER BY f.orderNum ASC")
    List<Faq> findAllActiveOrderByOrderNum();

    // 특정 카테고리의 활성화된 FAQ 조회
    @Query("SELECT f FROM Faq f WHERE f.active = true AND f.category = :category ORDER BY f.orderNum ASC")
    List<Faq> findByCategoryAndActiveOrderByOrderNum(@Param("category") FaqCategory category);

    // 카테고리별 FAQ 개수
    long countByCategoryAndActiveTrue(FaqCategory category);
}
```

---

## 실행 방법

### 요구사항
- Java 21+
- Gradle 8.x
- Gmail 계정 (이메일 발송용, 앱 비밀번호 필요)

### 개발 환경 실행
```bash
# 1. 프로젝트 클론
git clone https://github.com/YangGyunShin/mydata-testbed.git
cd mydata-testbed

# 2. 애플리케이션 실행
./gradlew bootRun

# 3. 브라우저에서 접속
http://localhost:8080
```

### H2 콘솔 접속 (개발용)
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:file:./data/testdb
Username: sa
Password: (비워두기)
```

### 데이터베이스 설정

```yaml
# application.yml
spring:
  datasource:
    # H2 파일 DB - 서버 재시작해도 데이터 유지
    url: jdbc:h2:file:./data/testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    
  jpa:
    hibernate:
      ddl-auto: update  # 기존 데이터 유지, 변경사항만 반영
```

| 설정 | 설명 |
|------|------|
| `file:./data/testdb` | 프로젝트 루트의 `data` 폴더에 DB 파일 저장 |
| `DB_CLOSE_DELAY=-1` | 연결 유지 (자동 닫힘 방지) |
| `ddl-auto: update` | 테이블/데이터 유지, 스키마 변경만 반영 |

> **Note:** `data/` 폴더는 `.gitignore`에 추가되어 Git에 커밋되지 않습니다.

---

## 개발 로드맵

### Phase 1: 기본 구조 ✅ 완료
- [x] 프로젝트 생성 및 의존성 설정
- [x] 공통 레이아웃 (Header, Footer, Sidebar)
- [x] CSS (common, header, footer, sidebar, main, sub-page, form)
- [x] JS (common.js, main.js)
- [x] 메인 페이지 템플릿 (index.html)
- [x] Config 설정 (SecurityConfig, AuditConfig)
- [x] MainController

### Phase 2: 회원 기능 ✅ 완료
- [x] VO (EmailVo, PasswordVo, PhoneVo)
- [x] Entity (BaseTimeEntity, Member, EmailVerificationToken)
- [x] Enum (MemberRole)
- [x] Repository (MemberRepository, EmailVerificationTokenRepository)
- [x] DTO (MemberSignupRequestDto, MemberResponseDto)
- [x] Custom Validation (@PasswordMatching)
- [x] Mapper (MemberMapper)
- [x] Service (MemberService, EmailService + 구현체)
- [x] Security (CustomUserDetails, CustomUserDetailsService)
- [x] MemberController
- [x] 회원가입/로그인 템플릿
- [x] 이메일 인증 기능

### Phase 3: 게시판 기능 🔄 진행중
- [x] Notice Entity
- [x] NoticeRepository
- [x] Notice DTO (List, Detail)
- [x] NoticeMapper
- [x] NoticeService / NoticeServiceImpl
- [x] SupportController (공지사항 부분)
- [x] notice-list.html, notice-detail.html
- [x] MainController에 공지사항 연동
- [x] H2 파일 DB로 변경 (데이터 유지)
- [x] Faq Entity, FaqCategory Enum
- [x] FaqRepository
- [x] FaqResponseDto
- [x] FaqMapper
- [x] FaqService / FaqServiceImpl
- [x] SupportController (FAQ 부분)
- [x] faq.html (카테고리 필터링, 아코디언 UI)
- [ ] Inquiry Entity, Repository, Service, Controller, 템플릿
- [ ] Resource Entity, Repository, Service, Controller, 템플릿
- [ ] Board Entity, Repository, Service, Controller, 템플릿

### Phase 4: 핵심 기능 (예정)
- [ ] API 가이드 페이지
- [ ] 테스트베드 기능
- [ ] 적합성 심사 기능

### Phase 5: 완성도 높이기 (예정)
- [ ] 파일 첨부/다운로드
- [ ] 반응형 디자인 점검
- [ ] 에러 페이지 (404, 500)
- [ ] 미인증 회원 정리 배치 작업

---

## 라이선스

This project is for educational purposes only.

---

## 참고 자료

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [금융분야 마이데이터 테스트베드](https://developers.mydatakorea.org/mdtb/)

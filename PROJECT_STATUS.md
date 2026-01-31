# 📊 프로젝트 진행 상황

> **마지막 업데이트**: 2025-01-31  
> **현재 Phase**: Phase 3 완료 ✅ → Phase 4 예정

---

## 🎯 전체 진행률

```
Phase 1: 기본 구조      [██████████] 100% ✅
Phase 2: 회원 기능      [██████████] 100% ✅
Phase 3: 게시판 기능    [██████████] 100% ✅
Phase 4: 핵심 기능      [░░░░░░░░░░]   0% ⏳
Phase 5: 완성도         [░░░░░░░░░░]   0% ⏳
```

---

## ✅ 완료된 작업

### Phase 1: 기본 구조 ✅

| 분류 | 항목 | 파일 |
|------|------|------|
| **Config** | 보안, 웹, Auditing 설정 | `SecurityConfig`, `WebConfig`, `AuditConfig` |
| **Layout** | 레이아웃 템플릿 | `default-layout`, `header`, `footer`, `sidebar` |
| **Fragments** | 공통 UI 조각 | `breadcrumb`, `pagination`, `page-banner` |
| **CSS** | 스타일시트 | `common`, `header`, `footer`, `sidebar`, `main`, `sub-page`, `form` |
| **JS** | 스크립트 | `common.js`, `main.js` |
| **Page** | 메인 페이지 | `index.html`, `MainController` |

### Phase 2: 회원 기능 ✅

| 분류 | 항목 | 파일 |
|------|------|------|
| **Entity** | 회원, 공통 시간 | `Member`, `BaseTimeEntity` |
| **VO** | 값 객체 | `Email`, `Password`, `Phone` |
| **Enum** | 회원 역할 | `MemberRole` |
| **Repository** | 데이터 접근 | `MemberRepository`, `EmailVerificationTokenRepository` |
| **DTO** | 데이터 전송 | `MemberSignupRequestDto`, `MemberResponseDto` |
| **Mapper** | 변환 | `MemberMapper` |
| **Service** | 비즈니스 로직 | `MemberService`, `EmailService` |
| **Security** | 인증/인가 | `CustomUserDetails`, `CustomUserDetailsService` |
| **Controller** | 요청 처리 | `MemberController` |
| **Templates** | 화면 | `login`, `signup-step1~4`, `verify-email-*` |

### Phase 3: 게시판 기능 ✅

#### 3-1. 공지사항 (Notice) ✅

| 항목 | 파일 |
|------|------|
| Entity | `Notice.java` |
| Repository | `NoticeRepository.java` |
| DTO | `NoticeListResponseDto`, `NoticeDetailResponseDto` |
| Mapper | `NoticeMapper.java` |
| Service | `NoticeService`, `NoticeServiceImpl` |
| Controller | `SupportController` (Notice 부분) |
| Templates | `notice-list.html`, `notice-detail.html` |

#### 3-2. FAQ ✅

| 항목 | 파일 |
|------|------|
| Entity | `Faq.java` |
| Enum | `FaqCategory.java` |
| Repository | `FaqRepository.java` |
| DTO | `FaqResponseDto.java` |
| Mapper | `FaqMapper.java` |
| Service | `FaqService`, `FaqServiceImpl` |
| Controller | `SupportController` (FAQ 부분) |
| Templates | `faq.html` |
| 초기 데이터 | `data.sql` |

#### 3-3. 문의하기 (Inquiry) ✅

| 항목 | 파일 |
|------|------|
| Entity | `Inquiry.java` |
| Enum | `InquiryStatus.java` (WAITING, COMPLETED) |
| Repository | `InquiryRepository.java` |
| DTO | `InquiryRequestDto`, `InquiryResponseDto`, `InquiryListResponseDto` |
| Mapper | `InquiryMapper.java` |
| Service | `InquiryService`, `InquiryServiceImpl` |
| Controller | `SupportController` (Inquiry 부분) |
| Templates | `inquiry-form.html`, `inquiry-list.html`, `inquiry-detail.html` |

#### 3-4. 자료실 (Resource) ✅

| 항목 | 파일 |
|------|------|
| Entity | `Resource.java` |
| Repository | `ResourceRepository.java` |
| DTO | `ResourceListResponseDto`, `ResourceDetailResponseDto`, `ResourceNavDto` |
| Mapper | `ResourceMapper.java` |
| Service | `ResourceService`, `ResourceServiceImpl` |
| Controller | `SupportController` (Resource 부분) |
| Templates | `resource-list.html`, `resource-detail.html` |
| 초기 데이터 | `data.sql` (7건의 테스트 데이터) |

#### 3-5. 자유게시판 (Board) ✅

| 항목 | 파일 |
|------|------|
| Entity | `Board.java` |
| Repository | `BoardRepository.java` |
| DTO | `BoardRequestDto`, `BoardListResponseDto`, `BoardDetailResponseDto` |
| Mapper | `BoardMapper.java` |
| Service | `BoardService`, `BoardServiceImpl`, `FileService`, `FileServiceImpl` |
| Util | `FileSizeFormatter.java` |
| Controller | `SupportController` (Board 부분) |
| Templates | `board-list.html`, `board-detail.html`, `board-write.html` |

**주요 기능:**
- 게시글 CRUD (작성/조회/수정/삭제)
- 파일 업로드/다운로드 (최대 10MB)
- 검색 (전체/제목/작성자)
- 페이징
- 권한 체크 (작성자 본인 또는 관리자만 수정/삭제)
- 조회수 자동 증가

---

## 📝 남은 작업

### Phase 4: 핵심 기능 (예정) ⬜

| 항목 | 상태 | 비고 |
|------|------|------|
| API 가이드 페이지 | ⬜ | 정적 컨텐츠 |
| 테스트베드 기능 | ⬜ | 서비스/API 테스트 |
| 적합성 심사 기능 | ⬜ | 기능적합성/보안취약점 |

### Phase 5: 완성도 (예정) ⬜

| 항목 | 상태 |
|------|------|
| 검색 기능 고도화 | ⬜ |
| 반응형 디자인 개선 | ⬜ |
| 에러 페이지 (404, 500) | ⬜ |
| 전역 예외 처리 | ⬜ |

---

## 📁 현재 파일 구조

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
│   ├── MemberController.java
│   └── SupportController.java
│
├── entity/
│   ├── BaseTimeEntity.java
│   ├── Member.java
│   ├── Notice.java
│   ├── Faq.java
│   ├── Inquiry.java
│   ├── Resource.java
│   ├── Board.java                       # ✅ Phase 3-5 추가
│   └── Enum/
│       ├── MemberRole.java
│       ├── FaqCategory.java
│       └── InquiryStatus.java
│
├── vo/
│   ├── EmailVo.java
│   ├── PasswordVo.java
│   └── PhoneVo.java
│
├── repository/
│   ├── MemberRepository.java
│   ├── EmailVerificationTokenRepository.java
│   ├── NoticeRepository.java
│   ├── FaqRepository.java
│   ├── InquiryRepository.java
│   ├── ResourceRepository.java
│   └── BoardRepository.java             # ✅ Phase 3-5 추가
│
├── dto/
│   ├── member/
│   │   ├── MemberSignupRequestDto.java
│   │   └── MemberResponseDto.java
│   ├── notice/
│   │   ├── NoticeListResponseDto.java
│   │   └── NoticeDetailResponseDto.java
│   ├── faq/
│   │   └── FaqResponseDto.java
│   ├── inquiry/
│   │   ├── InquiryRequestDto.java
│   │   ├── InquiryResponseDto.java
│   │   └── InquiryListResponseDto.java
│   ├── resource/
│   │   ├── ResourceListResponseDto.java
│   │   ├── ResourceDetailResponseDto.java
│   │   └── ResourceNavDto.java
│   └── board/                            # ✅ Phase 3-5 추가
│       ├── BoardRequestDto.java
│       ├── BoardListResponseDto.java
│       └── BoardDetailResponseDto.java
│
├── mapper/
│   ├── MemberMapper.java
│   ├── NoticeMapper.java
│   ├── FaqMapper.java
│   ├── InquiryMapper.java
│   ├── ResourceMapper.java
│   └── BoardMapper.java                  # ✅ Phase 3-5 추가
│
├── service/
│   ├── MemberService.java
│   ├── EmailService.java
│   ├── NoticeService.java
│   ├── FaqService.java
│   ├── InquiryService.java
│   ├── ResourceService.java
│   ├── BoardService.java                 # ✅ Phase 3-5 추가
│   ├── FileService.java                  # ✅ Phase 3-5 추가
│   └── impl/
│       ├── MemberServiceImpl.java
│       ├── EmailServiceImpl.java
│       ├── NoticeServiceImpl.java
│       ├── FaqServiceImpl.java
│       ├── InquiryServiceImpl.java
│       ├── ResourceServiceImpl.java
│       ├── BoardServiceImpl.java         # ✅ Phase 3-5 추가
│       └── FileServiceImpl.java          # ✅ Phase 3-5 추가
│
├── util/                                  # ✅ Phase 3-5 추가
│   └── FileSizeFormatter.java
│
├── security/
│   ├── CustomUserDetails.java
│   └── CustomUserDetailsService.java
│
└── validation/
    ├── PasswordMatching.java
    └── PasswordMatchingValidator.java

src/main/resources/
├── application.yml
├── data.sql
├── templates/
│   ├── layout/
│   │   ├── default-layout.html
│   │   ├── header.html
│   │   ├── footer.html
│   │   └── sidebar.html
│   ├── fragments/
│   │   ├── breadcrumb.html
│   │   ├── pagination.html
│   │   └── page-banner.html
│   ├── main/
│   │   └── index.html
│   ├── member/
│   │   ├── login.html
│   │   ├── signup-step1-terms.html
│   │   ├── signup-step2-phone.html
│   │   ├── signup-step3-info.html
│   │   ├── signup-step4-email.html
│   │   ├── verify-email-success.html
│   │   └── verify-email-failed.html
│   ├── support/
│   │   ├── notice-list.html
│   │   ├── notice-detail.html
│   │   ├── faq.html
│   │   ├── inquiry-form.html
│   │   ├── inquiry-list.html
│   │   ├── inquiry-detail.html
│   │   ├── resource-list.html
│   │   ├── resource-detail.html
│   │   ├── board-list.html              # ✅ Phase 3-5 추가
│   │   ├── board-detail.html            # ✅ Phase 3-5 추가
│   │   └── board-write.html             # ✅ Phase 3-5 추가
│   └── error/
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

## 🔗 구현된 URL 매핑

### 공개 URL (인증 불필요)

| URL | Method | 설명 |
|-----|--------|------|
| `/` | GET | 메인 페이지 |
| `/member/login` | GET/POST | 로그인 |
| `/member/signup/step1~4` | GET/POST | 회원가입 |
| `/member/verify-email` | GET | 이메일 인증 |
| `/member/resend-verification` | POST | 인증 메일 재발송 |
| `/support/notice` | GET | 공지사항 목록 |
| `/support/notice/{id}` | GET | 공지사항 상세 |
| `/support/faq` | GET | FAQ |
| `/support/resource` | GET | 자료실 목록 |
| `/support/resource/{id}` | GET | 자료실 상세 |
| `/support/resource/{id}/download` | GET | 자료 다운로드 |
| `/support/board` | GET | 자유게시판 목록 ✅ |
| `/support/board/{id}` | GET | 자유게시판 상세 ✅ |
| `/support/board/{id}/download` | GET | 첨부파일 다운로드 ✅ |

### 인증 필요 URL

| URL | Method | 설명 |
|-----|--------|------|
| `/support/inquiry` | GET/POST | 문의 작성 폼/등록 |
| `/support/inquiry/list` | GET | 내 문의 목록 |
| `/support/inquiry/{id}` | GET | 문의 상세 |
| `/support/board/write` | GET/POST | 게시글 작성 ✅ |
| `/support/board/{id}/edit` | GET/POST | 게시글 수정 (작성자/관리자) ✅ |
| `/support/board/{id}/delete` | POST | 게시글 삭제 (작성자/관리자) ✅ |

---

## 📚 관련 문서

| 문서 | 설명 |
|------|------|
| [README.md](README.md) | 프로젝트 소개, 빠른 시작 |
| [API_SPEC.md](API_SPEC.md) | API 엔드포인트 상세 명세 |
| [TROUBLESHOOTING.md](TROUBLESHOOTING.md) | 트러블슈팅 가이드 |
| [NEXT_SESSION_TEMPLATE.md](NEXT_SESSION_TEMPLATE.md) | 코딩 컨벤션, 다음 작업 |

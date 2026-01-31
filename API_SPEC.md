# 📚 API 명세서

> **마지막 업데이트**: 2025-01-31  
> 마이데이터 테스트베드 프로젝트의 URL 엔드포인트 명세입니다.

---

## 목차

- [메인](#메인)
- [회원 (Member)](#회원-member)
- [고객지원 - 공지사항 (Notice)](#고객지원---공지사항-notice)
- [고객지원 - FAQ](#고객지원---faq)
- [고객지원 - 문의하기 (Inquiry)](#고객지원---문의하기-inquiry)
- [고객지원 - 자료실 (Resource)](#고객지원---자료실-resource)
- [고객지원 - 자유게시판 (Board)](#고객지원---자유게시판-board) ✅
- [인증 필요 여부 요약](#인증-필요-여부-요약)

---

## 메인

### 메인 페이지

| 항목 | 내용 |
|------|------|
| **URL** | `/` |
| **Method** | `GET` |
| **인증** | 불필요 |
| **설명** | 메인 페이지 (최신 공지사항 3개 표시) |

**Model 데이터**:

| 속성 | 타입 | 설명 |
|------|------|------|
| `notices` | `List<NoticeListResponseDto>` | 최신 공지사항 3개 |

---

## 회원 (Member)

### 로그인

| 항목 | 내용 |
|------|------|
| **URL** | `/member/login` |
| **Method** | `GET` / `POST` |
| **인증** | 불필요 |
| **설명** | 로그인 페이지 및 처리 (Spring Security) |

**POST 파라미터** (Spring Security 자동 처리):

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `email` | String | ✅ | 사용자 이메일 |
| `password` | String | ✅ | 비밀번호 |

**응답**:
- 성공: `/` (메인 페이지)
- 실패: `/member/login?error=true`

---

### 로그아웃

| 항목 | 내용 |
|------|------|
| **URL** | `/member/logout` |
| **Method** | `POST` |
| **인증** | 필요 |
| **설명** | 로그아웃 처리 (Spring Security) |

**응답**: `/` (메인 페이지)

---

### 회원가입 Step 1 - 약관동의

| 항목 | 내용 |
|------|------|
| **URL** | `/member/signup/step1` |
| **Method** | `GET` |
| **인증** | 불필요 |

---

### 회원가입 Step 2 - 휴대폰 인증

| 항목 | 내용 |
|------|------|
| **URL** | `/member/signup/step2` |
| **Method** | `GET` / `POST` |
| **인증** | 불필요 |

**POST 파라미터**:

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `termsAgreed` | boolean | ✅ | 이용약관 동의 |
| `privacyAgreed` | boolean | ✅ | 개인정보처리방침 동의 |

---

### 회원가입 Step 3 - 회원정보 입력

| 항목 | 내용 |
|------|------|
| **URL** | `/member/signup/step3` |
| **Method** | `GET` / `POST` |
| **인증** | 불필요 |
| **설명** | 회원 생성 + 인증 메일 발송 |

**POST 파라미터** (`MemberSignupRequestDto`):

| 파라미터 | 타입 | 필수 | 검증 규칙 | 설명 |
|----------|------|------|-----------|------|
| `email` | String | ✅ | 이메일 형식, 중복 불가 | 사용자 이메일 |
| `password` | String | ✅ | 8~20자, 영문+숫자+특수문자 | 비밀번호 |
| `passwordConfirm` | String | ✅ | password와 일치 | 비밀번호 확인 |
| `name` | String | ✅ | 2~50자 | 이름 |
| `phone` | String | ✅ | 010-XXXX-XXXX 형식 | 휴대폰 번호 |
| `company` | String | ❌ | 최대 100자 | 소속 회사/기관 |
| `department` | String | ❌ | 최대 50자 | 부서 |

---

### 회원가입 Step 4 - 이메일 인증 대기

| 항목 | 내용 |
|------|------|
| **URL** | `/member/signup/step4` |
| **Method** | `GET` |
| **인증** | 불필요 |

---

### 이메일 인증 처리

| 항목 | 내용 |
|------|------|
| **URL** | `/member/verify-email` |
| **Method** | `GET` |
| **인증** | 불필요 |

**Query 파라미터**:

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `token` | String | ✅ | 이메일 인증 토큰 (UUID) |

**응답**:
- 성공: `member/verify-email-success`
- 실패: `member/verify-email-failed`

---

### 인증 메일 재발송

| 항목 | 내용 |
|------|------|
| **URL** | `/member/resend-verification` |
| **Method** | `POST` |
| **인증** | 불필요 |
| **Content-Type** | `application/x-www-form-urlencoded` |

**요청 파라미터**:

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `email` | String | ✅ | 재발송 대상 이메일 |

**응답** (JSON):
```json
{ "success": true, "expiresAt": "2025-01-23T12:00:00" }
{ "success": false, "message": "에러 메시지" }
```

---

## 고객지원 - 공지사항 (Notice)

### 공지사항 목록 조회

| 항목 | 내용 |
|------|------|
| **URL** | `/support/notice` |
| **Method** | `GET` |
| **인증** | 불필요 |

**Query 파라미터**:

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| `page` | int | ❌ | 0 | 페이지 번호 |
| `keyword` | String | ❌ | "" | 검색어 (제목/내용) |

**NoticeListResponseDto**:

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 공지사항 ID |
| `title` | String | 제목 |
| `pinned` | boolean | 중요 공지 여부 |
| `viewCount` | int | 조회수 |
| `hasAttachment` | boolean | 첨부파일 유무 |
| `authorName` | String | 작성자 이름 |
| `createdAt` | LocalDateTime | 등록일 |

---

### 공지사항 상세 조회

| 항목 | 내용 |
|------|------|
| **URL** | `/support/notice/{id}` |
| **Method** | `GET` |
| **인증** | 불필요 |
| **설명** | 조회 시 조회수 증가 |

**NoticeDetailResponseDto**:

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 공지사항 ID |
| `title` | String | 제목 |
| `content` | String | 본문 (HTML) |
| `pinned` | boolean | 중요 공지 여부 |
| `viewCount` | int | 조회수 |
| `attachmentPath` | String | 첨부파일 경로 |
| `attachmentName` | String | 첨부파일 원본명 |
| `authorName` | String | 작성자 이름 |
| `createdAt` | LocalDateTime | 등록일 |
| `updatedAt` | LocalDateTime | 수정일 |

---

## 고객지원 - FAQ

### FAQ 목록 조회

| 항목 | 내용 |
|------|------|
| **URL** | `/support/faq` |
| **Method** | `GET` |
| **인증** | 불필요 |
| **설명** | 카테고리별 필터링, 아코디언 UI |

**Query 파라미터**:

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `category` | FaqCategory | ❌ | 카테고리 필터 (null이면 전체) |

**FaqCategory Enum**:

| 값 | 표시명 |
|------|------|
| `GENERAL` | 일반 |
| `SIGNUP` | 회원가입 |
| `API` | API |
| `TEST` | 테스트 |
| `CONFORMANCE` | 적합성심사 |

**FaqResponseDto**:

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | FAQ ID |
| `category` | FaqCategory | 카테고리 |
| `categoryDisplayName` | String | 카테고리 표시명 |
| `question` | String | 질문 |
| `answer` | String | 답변 |
| `orderNum` | int | 정렬 순서 |
| `createdAt` | LocalDateTime | 등록일 |

---

## 고객지원 - 문의하기 (Inquiry)

### 문의 작성 폼

| 항목 | 내용 |
|------|------|
| **URL** | `/support/inquiry` |
| **Method** | `GET` |
| **인증** | ✅ 필요 |

---

### 문의 등록

| 항목 | 내용 |
|------|------|
| **URL** | `/support/inquiry` |
| **Method** | `POST` |
| **인증** | ✅ 필요 |

**요청 파라미터** (`InquiryRequestDto`):

| 파라미터 | 타입 | 필수 | 검증 규칙 | 설명 |
|----------|------|------|-----------|------|
| `title` | String | ✅ | @NotBlank, 최대 200자 | 문의 제목 |
| `content` | String | ✅ | @NotBlank | 문의 내용 |

**응답**: `/support/inquiry/list`로 리다이렉트 + Flash 메시지

---

### 내 문의 목록

| 항목 | 내용 |
|------|------|
| **URL** | `/support/inquiry/list` |
| **Method** | `GET` |
| **인증** | ✅ 필요 |
| **설명** | 본인 문의만 조회 |

**Query 파라미터**:

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| `page` | int | ❌ | 0 | 페이지 번호 |

**Model 데이터**:

| 속성 | 타입 | 설명 |
|------|------|------|
| `inquiries` | `Page<InquiryListResponseDto>` | 문의 목록 |
| `totalCount` | long | 총 문의 개수 |

**InquiryListResponseDto**:

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 문의 ID |
| `title` | String | 제목 |
| `status` | InquiryStatus | 상태 |
| `statusDisplayName` | String | 상태 표시명 |
| `createdAt` | LocalDateTime | 등록일 |
| `answeredAt` | LocalDateTime | 답변일 (null 가능) |

---

### 문의 상세 조회

| 항목 | 내용 |
|------|------|
| **URL** | `/support/inquiry/{id}` |
| **Method** | `GET` |
| **인증** | ✅ 필요 |
| **설명** | 본인 문의만 조회 가능 |

**InquiryResponseDto**:

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 문의 ID |
| `title` | String | 제목 |
| `content` | String | 문의 내용 |
| `memberName` | String | 작성자 이름 |
| `memberEmail` | String | 작성자 이메일 |
| `status` | InquiryStatus | 상태 |
| `statusDisplayName` | String | 상태 표시명 |
| `answer` | String | 답변 내용 (null 가능) |
| `answererName` | String | 답변자 이름 (null 가능) |
| `answeredAt` | LocalDateTime | 답변일 (null 가능) |
| `createdAt` | LocalDateTime | 등록일 |
| `updatedAt` | LocalDateTime | 수정일 |

**InquiryStatus Enum**:

| 값 | 표시명 |
|------|------|
| `WAITING` | 대기 |
| `COMPLETED` | 완료 |

---

## 고객지원 - 자료실 (Resource)

### 자료 목록 조회

| 항목 | 내용 |
|------|------|
| **URL** | `/support/resource` |
| **Method** | `GET` |
| **인증** | 불필요 |
| **설명** | 목록형 UI, 제목 클릭 시 상세 페이지 이동 |

**Query 파라미터**:

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| `page` | int | ❌ | 0 | 페이지 번호 |
| `keyword` | String | ❌ | "" | 검색어 (제목/설명) |

**Model 데이터**:

| 속성 | 타입 | 설명 |
|------|------|------|
| `resources` | `Page<ResourceListResponseDto>` | 자료 목록 |
| `keyword` | String | 검색어 (폼 유지용) |

**ResourceListResponseDto**:

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 자료 ID |
| `title` | String | 제목 |
| `createdAt` | LocalDateTime | 등록일 |

---

### 자료 상세 조회

| 항목 | 내용 |
|------|------|
| **URL** | `/support/resource/{id}` |
| **Method** | `GET` |
| **인증** | 불필요 |
| **설명** | 조회 시 조회수 증가, 이전글/다음글 네비게이션 |

**Model 데이터**:

| 속성 | 타입 | 설명 |
|------|------|------|
| `resource` | `ResourceDetailResponseDto` | 자료 상세 정보 |
| `nextResource` | `ResourceNavDto` | 다음 글 (null 가능) |
| `prevResource` | `ResourceNavDto` | 이전 글 (null 가능) |

**ResourceDetailResponseDto**:

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 자료 ID |
| `title` | String | 제목 |
| `content` | String | 본문 (description 필드 매핑) |
| `fileName` | String | 파일명 |
| `formattedFileSize` | String | 파일 크기 (예: "5.0 MB") |
| `viewCount` | int | 조회수 |
| `downloadCount` | int | 다운로드 수 |
| `authorName` | String | 작성자 이름 |
| `createdAt` | LocalDateTime | 등록일 |

**ResourceNavDto** (이전글/다음글):

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 자료 ID |
| `title` | String | 제목 |

---

### 자료 다운로드

| 항목 | 내용 |
|------|------|
| **URL** | `/support/resource/{id}/download` |
| **Method** | `GET` |
| **인증** | 불필요 |
| **설명** | 파일 다운로드 + 다운로드 수 증가 |

**응답**:
- 파일 존재 시: 파일 다운로드 (Content-Disposition: attachment)
- 파일 미존재 시: 상세 페이지로 리다이렉트

---

## 고객지원 - 자유게시판 (Board)

### 게시글 목록 조회

| 항목 | 내용 |
|------|------|
| **URL** | `/support/board` |
| **Method** | `GET` |
| **인증** | 불필요 |
| **설명** | 목록형 UI, 검색 타입별 필터링 |

**Query 파라미터**:

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| `page` | int | ❌ | 0 | 페이지 번호 |
| `keyword` | String | ❌ | "" | 검색어 |
| `searchType` | String | ❌ | "all" | 검색 타입 (all/title/author) |

**Model 데이터**:

| 속성 | 타입 | 설명 |
|------|------|------|
| `boards` | `Page<BoardListResponseDto>` | 게시글 목록 |
| `keyword` | String | 검색어 (폼 유지용) |
| `searchType` | String | 검색 타입 (폼 유지용) |
| `totalCount` | long | 총 게시글 수 |

**BoardListResponseDto**:

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 게시글 ID |
| `title` | String | 제목 |
| `authorName` | String | 작성자 이름 |
| `viewCount` | int | 조회수 |
| `hasAttachment` | boolean | 첨부파일 유무 |
| `createdAt` | LocalDateTime | 등록일 |

---

### 게시글 상세 조회

| 항목 | 내용 |
|------|------|
| **URL** | `/support/board/{id}` |
| **Method** | `GET` |
| **인증** | 불필요 |
| **설명** | 조회 시 조회수 증가 |

**Model 데이터**:

| 속성 | 타입 | 설명 |
|------|------|------|
| `board` | `BoardDetailResponseDto` | 게시글 상세 정보 |
| `isAuthor` | boolean | 로그인 사용자가 작성자인지 여부 |

**BoardDetailResponseDto**:

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 게시글 ID |
| `title` | String | 제목 |
| `content` | String | 본문 |
| `authorId` | Long | 작성자 ID |
| `authorName` | String | 작성자 이름 |
| `viewCount` | int | 조회수 |
| `attachmentPath` | String | 첨부파일 경로 (null 가능) |
| `attachmentName` | String | 첨부파일 원본명 (null 가능) |
| `formattedFileSize` | String | 파일 크기 (예: "5.0 MB", null 가능) |
| `createdAt` | LocalDateTime | 등록일 |
| `updatedAt` | LocalDateTime | 수정일 |

---

### 게시글 작성 폼

| 항목 | 내용 |
|------|------|
| **URL** | `/support/board/write` |
| **Method** | `GET` |
| **인증** | ✅ 필요 |

**Model 데이터**:

| 속성 | 타입 | 설명 |
|------|------|------|
| `boardRequest` | `BoardRequestDto` | 빈 폼 객체 |

---

### 게시글 등록

| 항목 | 내용 |
|------|------|
| **URL** | `/support/board/write` |
| **Method** | `POST` |
| **인증** | ✅ 필요 |
| **Content-Type** | `multipart/form-data` |

**요청 파라미터** (`BoardRequestDto`):

| 파라미터 | 타입 | 필수 | 검증 규칙 | 설명 |
|----------|------|------|-----------|------|
| `title` | String | ✅ | @NotBlank, 최대 200자 | 제목 |
| `content` | String | ✅ | @NotBlank | 내용 |
| `file` | MultipartFile | ❌ | 최대 10MB | 첨부파일 |

**응답**: `/support/board/{id}`로 리다이렉트

---

### 게시글 수정 폼

| 항목 | 내용 |
|------|------|
| **URL** | `/support/board/{id}/edit` |
| **Method** | `GET` |
| **인증** | ✅ 필요 |
| **권한** | 작성자 본인 또는 관리자 |

**Model 데이터**:

| 속성 | 타입 | 설명 |
|------|------|------|
| `boardRequest` | `BoardRequestDto` | 기존 데이터가 채워진 폼 |
| `board` | `BoardDetailResponseDto` | 기존 게시글 정보 (첨부파일 표시용) |

---

### 게시글 수정

| 항목 | 내용 |
|------|------|
| **URL** | `/support/board/{id}/edit` |
| **Method** | `POST` |
| **인증** | ✅ 필요 |
| **권한** | 작성자 본인 또는 관리자 |
| **Content-Type** | `multipart/form-data` |

**요청 파라미터**:

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `title` | String | ✅ | 제목 |
| `content` | String | ✅ | 내용 |
| `file` | MultipartFile | ❌ | 새 첨부파일 (기존 파일 교체) |
| `deleteAttachment` | boolean | ❌ | 기존 첨부파일 삭제 여부 |

**응답**: `/support/board/{id}`로 리다이렉트

---

### 게시글 삭제

| 항목 | 내용 |
|------|------|
| **URL** | `/support/board/{id}/delete` |
| **Method** | `POST` |
| **인증** | ✅ 필요 |
| **권한** | 작성자 본인 또는 관리자 |

**응답**: `/support/board`로 리다이렉트 + Flash 메시지

---

### 첨부파일 다운로드

| 항목 | 내용 |
|------|------|
| **URL** | `/support/board/{id}/download` |
| **Method** | `GET` |
| **인증** | 불필요 |
| **설명** | 첨부파일 다운로드 |

**응답**:
- 파일 존재 시: 파일 다운로드 (Content-Disposition: attachment)
- 파일 미존재 시: 상세 페이지로 리다이렉트

**다운로드 흐름**:
1. 게시글 조회 (조회수 증가 없이)
2. 첨부파일 존재 여부 확인
3. 파일 리소스 생성 (절대 경로)
4. 파일명 URL 인코딩 (한글 깨짐 방지)
5. Content-Disposition 헤더 설정 후 다운로드

---

## 인증 필요 여부 요약

### 공개 URL (인증 불필요)

| URL 패턴 | 설명 |
|----------|------|
| `/css/**`, `/js/**`, `/images/**` | 정적 리소스 |
| `/` | 메인 페이지 |
| `/intro/**`, `/api-guide/**` | 소개, API 가이드 |
| `/member/login` | 로그인 |
| `/member/signup/**` | 회원가입 |
| `/member/verify-email` | 이메일 인증 |
| `/member/resend-verification` | 인증 메일 재발송 |
| `/support/notice/**` | 공지사항 |
| `/support/faq` | FAQ |
| `/support/resource/**` | 자료실 |
| `/support/board` | 자유게시판 목록 |
| `/support/board/{id}` | 자유게시판 상세 |
| `/support/board/{id}/download` | 자유게시판 첨부파일 다운로드 |
| `/h2-console/**` | H2 콘솔 (개발용) |

### 인증 필요 URL

| URL 패턴 | 설명 |
|----------|------|
| `/support/inquiry/**` | 문의하기 (작성, 목록, 상세) |
| `/support/board/write` | 게시글 작성 |
| `/support/board/*/edit` | 게시글 수정 (작성자/관리자) |
| `/support/board/*/delete` | 게시글 삭제 (작성자/관리자) |
| `/testbed/**` | 테스트베드 (예정) |
| `/conformance/**` | 적합성 심사 (예정) |
| `/admin/**` | 관리자 (ADMIN 권한, 예정) |

---

## SecurityConfig 참고

```java
http.authorizeHttpRequests(auth -> auth
    // 정적 리소스
    .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
    
    // 공개 페이지
    .requestMatchers("/", "/intro/**", "/api-guide/**").permitAll()
    .requestMatchers("/member/login", "/member/signup/**").permitAll()
    .requestMatchers("/member/verify-email", "/member/resend-verification").permitAll()
    
    // 고객지원 - 인증 필요 (순서 중요! 구체적인 것 먼저)
    .requestMatchers("/support/inquiry/**").authenticated()
    .requestMatchers("/support/board/write").authenticated()
    .requestMatchers("/support/board/*/edit").authenticated()
    .requestMatchers("/support/board/*/delete").authenticated()
    
    // 고객지원 - 공개
    .requestMatchers("/support/**").permitAll()
    
    // H2 콘솔 (개발용)
    .requestMatchers("/h2-console/**").permitAll()
    
    // 나머지는 인증 필요
    .anyRequest().authenticated()
);
```

> ⚠️ **순서 주의**: 구체적인 URL 패턴(`/support/board/write`)이 일반적인 패턴(`/support/**`)보다 먼저 와야 합니다.

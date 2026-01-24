# 📋 Phase 3 계속 작업 요청서 - Inquiry/Resource/Board

## 🎯 요청 사항

Phase 3의 남은 기능들(문의하기, 자료실, 자유게시판)을 구현해주세요.

---

## 📁 프로젝트 정보

- **프로젝트명**: 금융분야 마이데이터 테스트베드 클론
- **프로젝트 경로**: `~/Library/Mobile Documents/com~apple~CloudDocs/Spring/study/mydata-testbed`
- **기술 스택**: Spring Boot 3.4.1, Java 21, Thymeleaf, Spring Security 6.x, JPA, H2 Database

---

## ✅ 완료된 작업

| Phase | 기능 | 상태 |
|-------|------|------|
| Phase 1 | 레이아웃, CSS/JS, Config | ✅ 완료 |
| Phase 2 | 회원가입, 로그인, 이메일 인증 | ✅ 완료 |
| Phase 3-1 | 공지사항 (Notice) | ✅ 완료 |
| Phase 3-2 | FAQ | ✅ 완료 |

---

## 📝 남은 작업

### 1. 문의하기 (Inquiry) - 우선 구현
| 항목 | 상태 |
|------|------|
| Inquiry Entity | ⬜ |
| InquiryStatus Enum (WAITING, COMPLETED) | ⬜ |
| InquiryRepository | ⬜ |
| InquiryRequestDto, InquiryResponseDto, InquiryListResponseDto | ⬜ |
| InquiryMapper | ⬜ |
| InquiryService / InquiryServiceImpl | ⬜ |
| SupportController (Inquiry 부분) | ⬜ |
| inquiry-form.html, inquiry-list.html, inquiry-detail.html | ⬜ |

### 2. 자료실 (Resource)
| 항목 | 상태 |
|------|------|
| Resource Entity | ⬜ |
| ResourceRepository | ⬜ |
| ResourceListResponseDto, ResourceDetailResponseDto | ⬜ |
| ResourceMapper | ⬜ |
| ResourceService / ResourceServiceImpl | ⬜ |
| SupportController (Resource 부분) | ⬜ |
| resource-list.html | ⬜ |
| 파일 다운로드 기능 | ⬜ |

### 3. 자유게시판 (Board)
| 항목 | 상태 |
|------|------|
| Board Entity | ⬜ |
| BoardRepository | ⬜ |
| BoardRequestDto, BoardListResponseDto, BoardDetailResponseDto | ⬜ |
| BoardMapper | ⬜ |
| BoardService / BoardServiceImpl | ⬜ |
| SupportController (Board 부분) | ⬜ |
| board-list.html, board-detail.html, board-write.html | ⬜ |

---

## 📐 코딩 컨벤션 (필수 준수)

### 어노테이션 패턴

| 클래스 | 어노테이션 |
|--------|-----------|
| **Entity** | `@Getter @NoArgsConstructor(access = PROTECTED)` + 생성자에 `@Builder` |
| **ResponseDto** | `@Getter @Builder` |
| **RequestDto** | `@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor` |

### 필수 규칙
- ❌ **No Factory Method**: `of()`, `from()` 정적 팩토리 메서드 사용 금지
- ❌ **No Setter**: Entity, ResponseDto에는 Setter 금지 (RequestDto만 예외)
- ✅ **Use Mapper**: DTO ↔ Entity 변환은 별도 Mapper 클래스 사용
- ✅ **@ManyToOne**: 반드시 `fetch = FetchType.LAZY` 지정
- ✅ **N+1 방지**: JOIN FETCH 쿼리 사용

---

## 🔗 URL 매핑 (예정)

### 문의하기
| URL | Method | 설명 |
|-----|--------|------|
| `/support/inquiry` | GET | 문의 작성 폼 (로그인 필요) |
| `/support/inquiry` | POST | 문의 등록 |
| `/support/inquiry/list` | GET | 내 문의 목록 |
| `/support/inquiry/{id}` | GET | 문의 상세 + 답변 확인 |

### 자료실
| URL | Method | 설명 |
|-----|--------|------|
| `/support/resource` | GET | 자료 목록 |
| `/support/resource/{id}/download` | GET | 자료 다운로드 |

### 자유게시판
| URL | Method | 설명 |
|-----|--------|------|
| `/support/board` | GET | 게시글 목록 |
| `/support/board/{id}` | GET | 게시글 상세 |
| `/support/board/write` | GET | 글쓰기 폼 (로그인 필요) |
| `/support/board/write` | POST | 글 등록 |

---

## 📂 파일 경로

```
src/main/java/com/mydata/mydatatestbed/
├── entity/
│   └── Inquiry.java, Resource.java, Board.java
├── entity/Enum/
│   └── InquiryStatus.java
├── repository/
│   └── InquiryRepository.java, ResourceRepository.java, BoardRepository.java
├── dto/
│   ├── inquiry/
│   ├── resource/
│   └── board/
├── mapper/
│   └── InquiryMapper.java, ResourceMapper.java, BoardMapper.java
├── service/
│   └── InquiryService.java, ResourceService.java, BoardService.java
└── service/impl/
    └── InquiryServiceImpl.java, ResourceServiceImpl.java, BoardServiceImpl.java

src/main/resources/templates/support/
├── inquiry-form.html
├── inquiry-list.html
├── inquiry-detail.html
├── resource-list.html
├── board-list.html
├── board-detail.html
└── board-write.html
```

---

## 🔄 작업 방식

### 역할 분담
- **사용자**: 백엔드 Java 코드 직접 생성
- **Claude**: 프론트엔드 HTML 템플릿 생성, 필요 시 CSS 수정

### 작업 순서
1. Claude가 백엔드 코드(Entity, Repository, DTO, Mapper, Service, Controller 수정분) 제공
2. 사용자가 해당 Java 파일들을 수동으로 생성
3. Claude가 프론트엔드 템플릿 직접 생성
4. 테스트 및 디버깅

---

## 📚 참고 문서

| 문서 | 내용 |
|------|------|
| `README.md` | 프로젝트 개요, 아키텍처, 코딩 컨벤션 |
| `TROUBLESHOOTING.md` | 트러블슈팅 가이드 |
| `PHASE3_TEMPLATE.md` | Phase 3 작업 상세 내용, 참고 코드 예시 |
| `API_SPEC.md` | API 명세 |

---

## 💬 시작하기

위 내용을 확인하시고, **문의하기(Inquiry) 기능부터** 구현을 시작해주세요.

기존 FAQ 패턴을 참고하여:
1. 먼저 백엔드 코드(Entity, Enum, Repository, DTO, Mapper, Service)를 제공해주세요
2. 그 다음 SupportController에 추가할 엔드포인트 코드를 제공해주세요
3. 마지막으로 HTML 템플릿을 생성해주세요

감사합니다! 🙏

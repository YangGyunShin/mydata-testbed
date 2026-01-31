# 📋 다음 세션 작업 가이드

> **마지막 업데이트**: 2025-01-31  
> **다음 작업**: Phase 4 핵심 기능 (API 가이드, 테스트베드, 적합성 심사)

---

## 📁 프로젝트 정보

| 항목 | 내용 |
|------|------|
| **프로젝트명** | 금융분야 마이데이터 테스트베드 클론 |
| **프로젝트 경로** | `~/Library/Mobile Documents/com~apple~CloudDocs/Spring/study/mydata-testbed` |
| **기술 스택** | Spring Boot 3.4.1, Java 21, Thymeleaf, Spring Security 6.x, JPA, H2 |

---

## ✅ 완료된 Phase 3 작업

| 기능 | 상태 | 비고 |
|------|------|------|
| 3-1. 공지사항 (Notice) | ✅ | 목록/상세, 검색, 페이징 |
| 3-2. FAQ | ✅ | 카테고리 필터, 아코디언 UI |
| 3-3. 문의하기 (Inquiry) | ✅ | 작성, 목록, 상세 (인증 필요) |
| 3-4. 자료실 (Resource) | ✅ | 목록/상세, 이전글/다음글, 다운로드 |
| 3-5. 자유게시판 (Board) | ✅ | CRUD, 파일 업로드/다운로드, 권한 체크 |

---

## 📐 코딩 컨벤션 (필수 준수)

### 어노테이션 패턴

| 클래스 유형 | 어노테이션 |
|------------|-----------|
| **Entity** | `@Getter @NoArgsConstructor(access = PROTECTED)` + 생성자에 `@Builder` |
| **ResponseDto** | `@Getter @Builder` |
| **RequestDto** | `@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor` |
| **Mapper** | `@Component` |
| **Service** | `@Service @RequiredArgsConstructor @Transactional(readOnly = true)` |

### 필수 규칙

| 규칙 | 설명 |
|------|------|
| ❌ **No Factory Method** | `of()`, `from()` 정적 팩토리 메서드 사용 금지 |
| ❌ **No Setter (Entity/ResponseDto)** | Entity, ResponseDto에 Setter 금지 |
| ✅ **Setter (RequestDto)** | RequestDto에는 `@Setter` 필수 (폼 바인딩용) |
| ✅ **Use Mapper** | DTO ↔ Entity 변환은 별도 Mapper 클래스 사용 |
| ✅ **LAZY Loading** | `@ManyToOne`에 `fetch = FetchType.LAZY` 필수 |
| ✅ **N+1 방지** | JOIN FETCH 쿼리 사용 |

---

## 🔗 완료된 Board 기능 URL 매핑

| URL | Method | 인증 | 설명 |
|-----|--------|------|------|
| `/support/board` | GET | ❌ | 게시글 목록 |
| `/support/board/{id}` | GET | ❌ | 게시글 상세 (조회수 증가) |
| `/support/board/write` | GET | ✅ | 글쓰기 폼 |
| `/support/board/write` | POST | ✅ | 글 등록 |
| `/support/board/{id}/edit` | GET | ✅ | 글 수정 폼 (작성자/관리자) |
| `/support/board/{id}/edit` | POST | ✅ | 글 수정 (작성자/관리자) |
| `/support/board/{id}/delete` | POST | ✅ | 글 삭제 (작성자/관리자) |
| `/support/board/{id}/download` | GET | ❌ | 첨부파일 다운로드 |

---

## 📂 Board 파일 구조 (완료)

```
src/main/java/com/mydata/mydatatestbed/
├── entity/
│   └── Board.java
├── repository/
│   └── BoardRepository.java
├── dto/board/
│   ├── BoardRequestDto.java          # @Setter 포함
│   ├── BoardListResponseDto.java
│   └── BoardDetailResponseDto.java
├── mapper/
│   └── BoardMapper.java
├── service/
│   ├── BoardService.java
│   ├── FileService.java
│   └── impl/
│       ├── BoardServiceImpl.java     # hasFile() 메서드 포함
│       └── FileServiceImpl.java      # 절대 경로 사용
└── util/
    └── FileSizeFormatter.java

src/main/resources/templates/support/
├── board-list.html
├── board-detail.html
└── board-write.html                  # 작성/수정 공용
```

---

## 🐛 해결된 트러블슈팅 요약

| 문제 | 원인 | 해결 |
|------|------|------|
| 사이드바 미표시 | 템플릿 변수명 불일치 (`menuItems` vs `sidebarMenus`) | 하드코딩 `'고객지원'` + `${sidebarMenus}` 사용 |
| 폼 바인딩 실패 | BoardRequestDto에 `@Setter` 누락 | `@Setter` 추가 |
| 파일 저장 실패 | 상대 경로 사용 | `.toAbsolutePath().normalize()` 추가 |
| 빈 파일 체크 실패 | `isEmpty()` 체크 불충분 | `hasFile()` 메서드로 강화 |

---

## 🔄 작업 방식

| 담당 | 작업 |
|------|------|
| **사용자** | 백엔드 Java 코드 직접 생성 |
| **Claude** | 프론트엔드 HTML 템플릿 생성, CSS 수정, 코드 제공 |

### 작업 순서

1. Claude가 백엔드 코드 (Entity, Repository, DTO, Mapper, Service, Controller) 제공
2. 사용자가 해당 Java 파일들을 수동으로 생성
3. Claude가 프론트엔드 템플릿 직접 생성
4. 테스트 및 디버깅

---

## 📚 주요 코드 패턴 (Board 구현 참고용)

### Entity - Board.java

```java
@Entity
@Table(name = "boards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private int viewCount = 0;

    private String attachmentPath;
    private String attachmentName;
    private Long attachmentSize;

    @Builder
    public Board(Member member, String title, String content,
                 String attachmentPath, String attachmentName, Long attachmentSize) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.attachmentPath = attachmentPath;
        this.attachmentName = attachmentName;
        this.attachmentSize = attachmentSize;
    }

    // 비즈니스 메서드
    public void incrementViewCount() {
        this.viewCount++;
    }

    public void update(String title, String content,
                       String attachmentPath, String attachmentName, Long attachmentSize) {
        this.title = title;
        this.content = content;
        if (attachmentPath != null) {
            this.attachmentPath = attachmentPath;
            this.attachmentName = attachmentName;
            this.attachmentSize = attachmentSize;
        }
    }

    public void removeAttachment() {
        this.attachmentPath = null;
        this.attachmentName = null;
        this.attachmentSize = null;
    }

    public boolean isAuthor(Long memberId) {
        return this.member.getId().equals(memberId);
    }
}
```

### Service - 파일 체크 패턴

```java
/**
 * 파일 존재 여부를 안전하게 확인
 * 
 * 단순히 file != null && !file.isEmpty()만으로는 부족함
 * - 브라우저에 따라 빈 파일도 isEmpty() = false일 수 있음
 */
private boolean hasFile(MultipartFile file) {
    return file != null 
            && !file.isEmpty() 
            && file.getSize() > 0
            && file.getOriginalFilename() != null 
            && !file.getOriginalFilename().trim().isEmpty();
}
```

### FileService - 절대 경로 사용

```java
@Override
public FileInfo saveFile(MultipartFile file, String subDir) {
    try {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String savedFilename = UUID.randomUUID() + extension;

        // 핵심: 절대 경로로 변환
        Path uploadPath = Paths.get(uploadDir, subDir).toAbsolutePath().normalize();

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(savedFilename);
        file.transferTo(filePath.toFile());  // 절대 경로면 정상 동작

        return new FileInfo(filePath.toString(), originalFilename, file.getSize());
    } catch (IOException e) {
        throw new RuntimeException("파일 저장에 실패했습니다: " + e.getMessage(), e);
    }
}
```

### Sidebar 템플릿 패턴

```html
<!-- 올바른 사이드바 호출 방식 -->
<th:block th:replace="~{layout/sidebar :: sidebar('고객지원', ${sidebarMenus}, ${currentMenu})}"></th:block>
```

---

## 📝 다음 작업: Phase 4 핵심 기능

### Phase 4-1: API 가이드 페이지 ⬜

| URL | 설명 |
|-----|------|
| `/api-guide` | API 가이드 메인 |
| `/api-guide/auth` | 인증규격 |
| `/api-guide/process` | 처리절차 |
| `/api-guide/auth-api` | 마이데이터 인증 API 규격 |
| `/api-guide/support-api` | 마이데이터 지원 API 규격 |
| `/api-guide/info-api` | 마이데이터 정보제공 API 규격 |

### Phase 4-2: 테스트베드 기능 ⬜

| URL | 설명 |
|-----|------|
| `/testbed/service` | 마이데이터 서비스 테스트 |
| `/testbed/api` | API 서버 테스트 |

### Phase 4-3: 적합성 심사 ⬜

| URL | 설명 |
|-----|------|
| `/conformance/functional` | 기능적합성 심사 |
| `/conformance/security` | 보안취약점 결과 점검 |

---

## 📚 관련 문서

| 문서 | 설명 |
|------|------|
| [README.md](README.md) | 프로젝트 소개, 빠른 시작 |
| [PROJECT_STATUS.md](PROJECT_STATUS.md) | 진행 상황, 파일 구조 |
| [API_SPEC.md](API_SPEC.md) | API 엔드포인트 상세 명세 |
| [TROUBLESHOOTING.md](TROUBLESHOOTING.md) | 트러블슈팅 가이드 |

---

## 💬 다음 세션 시작하기

**Phase 4 핵심 기능** 구현을 시작합니다.

Phase 4는 주로 정적 컨텐츠 페이지가 많으며, 실제 API 테스트 기능은 원본 사이트의 복잡한 로직을 단순화하여 구현할 예정입니다.

감사합니다! 🙏

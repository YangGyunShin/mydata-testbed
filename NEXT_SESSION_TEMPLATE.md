# 📋 다음 세션 작업 가이드

> **마지막 업데이트**: 2025-01-26  
> **다음 작업**: Phase 3-5 자유게시판 (Board)

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
| **3-5. 자유게시판 (Board)** | ⬜ | **다음 작업** |

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
| ❌ **No Setter** | Entity, ResponseDto에 Setter 금지 (RequestDto만 예외) |
| ✅ **Use Mapper** | DTO ↔ Entity 변환은 별도 Mapper 클래스 사용 |
| ✅ **LAZY Loading** | `@ManyToOne`에 `fetch = FetchType.LAZY` 필수 |
| ✅ **N+1 방지** | JOIN FETCH 쿼리 사용 |

---

## 🔗 다음 작업: 자유게시판 (Board) URL 매핑

| URL | Method | 인증 | 설명 |
|-----|--------|------|------|
| `/support/board` | GET | ❌ | 게시글 목록 |
| `/support/board/{id}` | GET | ❌ | 게시글 상세 (조회수 증가) |
| `/support/board/write` | GET | ✅ | 글쓰기 폼 |
| `/support/board/write` | POST | ✅ | 글 등록 |
| `/support/board/{id}/edit` | GET | ✅ | 글 수정 폼 (작성자만) |
| `/support/board/{id}/edit` | POST | ✅ | 글 수정 (작성자만) |
| `/support/board/{id}/delete` | POST | ✅ | 글 삭제 (작성자만) |

---

## 📂 생성할 파일 경로

### 자유게시판 (Board)

```
src/main/java/com/mydata/mydatatestbed/
├── entity/
│   └── Board.java
├── repository/
│   └── BoardRepository.java
├── dto/board/
│   ├── BoardRequestDto.java
│   ├── BoardListResponseDto.java
│   ├── BoardDetailResponseDto.java
│   └── BoardNavDto.java
├── mapper/
│   └── BoardMapper.java
└── service/
    ├── BoardService.java
    └── impl/BoardServiceImpl.java

src/main/resources/templates/support/
├── board-list.html
├── board-detail.html
└── board-write.html
```

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

## 📚 코드 예시 (자료실 Resource - 최신 참고용)

### Entity 예시 (Resource.java)

```java
@Entity
@Table(name = "resources")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Resource extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String fileName;

    private Long fileSize;

    @Column(nullable = false)
    private int downloadCount = 0;

    @Column(nullable = false)
    private int viewCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Member author;

    @Builder
    public Resource(String title, String description, String filePath, 
                    String fileName, Long fileSize, Member author) {
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.author = author;
    }

    public void incrementDownloadCount() {
        this.downloadCount++;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }
}
```

### Repository 예시 (ResourceRepository.java)

```java
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @Query("SELECT r FROM Resource r LEFT JOIN FETCH r.author WHERE r.id = :id")
    Optional<Resource> findByIdWithAuthor(@Param("id") Long id);

    @Query("SELECT r FROM Resource r WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY r.createdAt DESC")
    Page<Resource> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 다음 글 조회 (현재 글보다 최신)
    @Query("SELECT r FROM Resource r WHERE r.id > :id ORDER BY r.id ASC LIMIT 1")
    Optional<Resource> findNextResource(@Param("id") Long id);

    // 이전 글 조회 (현재 글보다 이전)
    @Query("SELECT r FROM Resource r WHERE r.id < :id ORDER BY r.id DESC LIMIT 1")
    Optional<Resource> findPrevResource(@Param("id") Long id);
}
```

### Mapper 예시 (ResourceMapper.java)

```java
@Component
public class ResourceMapper {

    public ResourceListResponseDto toListResponseDto(Resource resource) {
        return ResourceListResponseDto.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .createdAt(resource.getCreatedAt())
                .build();
    }

    public ResourceDetailResponseDto toDetailResponseDto(Resource resource) {
        return ResourceDetailResponseDto.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .content(resource.getDescription())
                .fileName(resource.getFileName())
                .formattedFileSize(formatFileSize(resource.getFileSize()))
                .viewCount(resource.getViewCount())
                .downloadCount(resource.getDownloadCount())
                .authorName(resource.getAuthor() != null ? resource.getAuthor().getName() : "관리자")
                .createdAt(resource.getCreatedAt())
                .build();
    }

    public ResourceNavDto toNavDto(Resource resource) {
        return ResourceNavDto.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .build();
    }

    private String formatFileSize(Long bytes) {
        if (bytes == null || bytes == 0) return "0 B";
        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        double size = bytes;
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        return String.format("%.1f %s", size, units[unitIndex]);
    }
}
```

### Controller 패턴 (SupportController - Resource 부분)

```java
// ========================================
// 자료실 (Resource)
// ========================================

@GetMapping("/resource")
public String resourceList(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "") String keyword,
        Model model) {

    Page<ResourceListResponseDto> resources = 
            resourceService.getResourceList(keyword, PageRequest.of(page, 10));

    model.addAttribute("resources", resources);
    model.addAttribute("keyword", keyword);
    model.addAttribute("menuTitle", "고객지원");
    model.addAttribute("menuItems", createSupportSidebarMenus());
    model.addAttribute("currentMenu", "/support/resource");
    model.addAttribute("breadcrumbItems", createResourceBreadcrumb());

    return "support/resource-list";
}

@GetMapping("/resource/{id}")
public String resourceDetail(@PathVariable Long id, Model model) {
    ResourceDetailResponseDto resource = resourceService.getResourceDetail(id);
    ResourceNavDto nextResource = resourceService.getNextResource(id);
    ResourceNavDto prevResource = resourceService.getPrevResource(id);

    model.addAttribute("resource", resource);
    model.addAttribute("nextResource", nextResource);
    model.addAttribute("prevResource", prevResource);
    model.addAttribute("menuTitle", "고객지원");
    model.addAttribute("menuItems", createSupportSidebarMenus());
    model.addAttribute("currentMenu", "/support/resource");
    model.addAttribute("breadcrumbItems", createResourceBreadcrumb());

    return "support/resource-detail";
}

private List<Map<String, String>> createResourceBreadcrumb() {
    return List.of(
            Map.of("name", "고객지원", "url", "#"),
            Map.of("name", "자료실", "url", "/support/resource")
    );
}
```

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

**자유게시판(Board)** 구현을 시작합니다.

자료실(Resource) 패턴을 참고하여:
1. 먼저 백엔드 코드 (Entity, Repository, DTO, Mapper, Service)를 제공해드립니다
2. 그 다음 SupportController에 추가할 엔드포인트 코드를 제공해드립니다
3. 마지막으로 HTML 템플릿을 생성합니다

### Board 특징 (Resource와 차이점)

| 항목 | Resource | Board |
|------|----------|-------|
| 글 작성 | 관리자만 | 로그인 사용자 |
| 글 수정/삭제 | 관리자만 | 작성자 본인만 |
| 첨부파일 | 필수 | 선택 |
| 이전글/다음글 | ✅ | ✅ |

감사합니다! 🙏

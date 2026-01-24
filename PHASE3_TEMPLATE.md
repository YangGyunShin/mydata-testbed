# 📋 Phase 3 작업 요청서 - 게시판 기능 (남은 작업)

## 프로젝트 정보

- **프로젝트명**: 금융분야 마이데이터 테스트베드 클론
- **프로젝트 경로**: `~/Library/Mobile Documents/com~apple~CloudDocs/Spring/study/mydata-testbed`
- **기술 스택**: Spring Boot 3.4.1, Java 21, Thymeleaf, Spring Security 6.x, JPA, H2 Database

---

## 완료된 작업

### Phase 1: 기본 구조 ✅
- 레이아웃 (Header, Footer, Sidebar)
- CSS/JS 파일
- MainController, SecurityConfig, AuditConfig

### Phase 2: 회원 기능 ✅
- Member Entity, VO (Email, Password, Phone)
- 회원가입 (4단계), 로그인/로그아웃
- 이메일 인증 기능

### Phase 3-1: 공지사항 ✅
- Notice Entity, Repository, DTO, Mapper, Service
- SupportController (공지사항 부분)
- notice-list.html, notice-detail.html
- 목록/상세/검색/페이징 기능

### Phase 3-2: FAQ ✅
- Faq Entity, FaqCategory Enum
- FaqRepository (카테고리별 조회, 활성화 필터)
- FaqResponseDto, FaqMapper
- FaqService / FaqServiceImpl
- SupportController (FAQ 부분)
- faq.html (카테고리 탭 필터링, 아코디언 UI)
- data.sql 초기 데이터

---

## 남은 Phase 3 작업

### 1. 문의하기 (Inquiry) 기능
- [ ] Inquiry Entity
- [ ] InquiryStatus Enum (WAITING, COMPLETED)
- [ ] InquiryRepository
- [ ] InquiryRequestDto, InquiryResponseDto, InquiryListResponseDto
- [ ] InquiryMapper
- [ ] InquiryService / InquiryServiceImpl
- [ ] SupportController에 문의하기 엔드포인트 추가
- [ ] inquiry-form.html (문의 작성 폼, 로그인 필요)
- [ ] inquiry-list.html (내 문의 목록)
- [ ] inquiry-detail.html (문의 상세 + 답변)

### 2. 자료실 (Resource) 기능
- [ ] Resource Entity
- [ ] ResourceRepository
- [ ] ResourceListResponseDto, ResourceDetailResponseDto
- [ ] ResourceMapper
- [ ] ResourceService / ResourceServiceImpl
- [ ] SupportController에 자료실 엔드포인트 추가
- [ ] resource-list.html 템플릿
- [ ] 파일 다운로드 기능

### 3. 자유게시판 (Board) 기능
- [ ] Board Entity
- [ ] BoardRepository
- [ ] BoardListResponseDto, BoardDetailResponseDto, BoardRequestDto
- [ ] BoardMapper
- [ ] BoardService / BoardServiceImpl
- [ ] SupportController에 자유게시판 엔드포인트 추가
- [ ] board-list.html, board-detail.html, board-write.html 템플릿

---

## 코딩 컨벤션 (필수 준수)

### 클래스별 어노테이션

| 클래스 | 어노테이션 |
|--------|-----------|
| **Entity** | `@Getter @NoArgsConstructor(access = PROTECTED)` + 생성자에 `@Builder` |
| **ResponseDto** | `@Getter @Builder` |
| **RequestDto** | `@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor` |

### 규칙
- **No Factory Method**: `of()`, `from()` 등 정적 팩토리 메서드 사용 금지
- **No Setter**: Entity, ResponseDto에는 Setter 금지 (RequestDto만 예외)
- **Use Mapper**: DTO ↔ Entity 변환은 별도 Mapper 클래스 사용
- **@ManyToOne**: 반드시 `fetch = FetchType.LAZY` 지정
- **N+1 방지**: JOIN FETCH 쿼리 사용

---

## 참고할 기존 코드 패턴

### Entity 예시 (Faq.java)
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
    private int orderNum;

    @Column(nullable = false)
    private boolean active = true;

    @Builder
    public Faq(FaqCategory category, String question, String answer, int orderNum) {
        this.category = category;
        this.question = question;
        this.answer = answer;
        this.orderNum = orderNum;
        this.active = true;
    }

    // 비즈니스 메서드
    public void update(FaqCategory category, String question, String answer, int orderNum) { ... }
    public void toggleActive() { ... }
}
```

### Repository 예시 (FaqRepository.java)
```java
public interface FaqRepository extends JpaRepository<Faq, Long> {
    
    @Query("SELECT f FROM Faq f WHERE f.active = true ORDER BY f.orderNum ASC")
    List<Faq> findAllActiveOrderByOrderNum();

    @Query("SELECT f FROM Faq f WHERE f.active = true AND f.category = :category ORDER BY f.orderNum ASC")
    List<Faq> findByCategoryAndActiveOrderByOrderNum(@Param("category") FaqCategory category);
}
```

### Mapper 예시 (FaqMapper.java)
```java
@Component
public class FaqMapper {
    
    public FaqResponseDto toResponseDto(Faq faq) {
        return FaqResponseDto.builder()
                .id(faq.getId())
                .category(faq.getCategory())
                .categoryDisplayName(faq.getCategory().getDisplayName())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .orderNum(faq.getOrderNum())
                .createdAt(faq.getCreatedAt())
                .build();
    }
}
```

### Service 예시 (FaqServiceImpl.java)
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaqServiceImpl implements FaqService {

    private final FaqRepository faqRepository;
    private final FaqMapper faqMapper;

    @Override
    public List<FaqResponseDto> getAllFaqs() {
        return faqRepository.findAllActiveOrderByOrderNum()
                .stream()
                .map(faqMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<FaqResponseDto> getFaqsByCategory(FaqCategory category) {
        return faqRepository.findByCategoryAndActiveOrderByOrderNum(category)
                .stream()
                .map(faqMapper::toResponseDto)
                .toList();
    }
}
```

### Controller 예시 (SupportController - FAQ 부분)
```java
@GetMapping("/faq")
public String faq(@RequestParam(required = false) FaqCategory category, Model model) {
    List<FaqResponseDto> faqs;
    if (category != null) {
        faqs = faqService.getFaqsByCategory(category);
    } else {
        faqs = faqService.getAllFaqs();
    }

    model.addAttribute("faqs", faqs);
    model.addAttribute("categories", FaqCategory.values());
    model.addAttribute("selectedCategory", category);
    model.addAttribute("breadcrumbItems", createFaqBreadcrumb());
    model.addAttribute("menuTitle", "고객지원");
    model.addAttribute("menuItems", getSupportMenuItems());
    model.addAttribute("currentMenu", "/support/faq");

    return "support/faq";
}
```

---

## 파일 경로 참고

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
│   │   ├── InquiryRequestDto.java
│   │   ├── InquiryResponseDto.java
│   │   └── InquiryListResponseDto.java
│   ├── resource/
│   │   ├── ResourceListResponseDto.java
│   │   └── ResourceDetailResponseDto.java
│   └── board/
│       ├── BoardRequestDto.java
│       ├── BoardListResponseDto.java
│       └── BoardDetailResponseDto.java
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

## 작업 방식 안내

### 역할 분담
- **사용자**: 백엔드 Java 코드 직접 생성 (Entity, Repository, DTO, Mapper, Service, Controller)
- **Claude**: 프론트엔드 HTML 템플릿 생성, CSS 수정, 코드 제공

### 작업 순서
1. Claude가 백엔드 코드를 제공
2. 사용자가 해당 Java 파일들을 수동으로 생성
3. Claude가 프론트엔드 템플릿 직접 생성
4. 테스트 및 디버깅

---

## 요청사항

1. **Inquiry (문의하기) 부터 시작**해주세요.
2. 각 기능 구현 후 **README.md 개발 로드맵 업데이트** 부탁드립니다.
3. 새로운 트러블슈팅이 발생하면 **TROUBLESHOOTING.md에 추가**해주세요.
4. **기존 FAQ 코드 패턴을 참고**하여 일관성 있게 구현해주세요.

---

## 참고 문서

| 문서 | 경로 |
|------|------|
| README | `README.md` |
| API 명세 | `API_SPEC.md` |
| 트러블슈팅 | `TROUBLESHOOTING.md` |
| 시스템 구조 | `마이데이터_테스트베드_시스템_구조` (프로젝트 파일) |

# 📋 다음 세션 작업 가이드

> **마지막 업데이트**: 2026-02-05  
> **다음 작업**: Phase 4-4 계속 - 마이데이터 정보제공 API 규격 (나머지 업권)

---

## 📁 프로젝트 정보

| 항목 | 내용 |
|------|------|
| **프로젝트명** | 금융분야 마이데이터 테스트베드 클론 |
| **프로젝트 경로** | `~/Library/Mobile Documents/com~apple~CloudDocs/Spring/study/mydata-testbed` |
| **기술 스택** | Spring Boot 3.4.1, Java 21, Thymeleaf, Spring Security 6.x, JPA, H2 |

---

## ✅ 완료된 작업 요약

| Phase | 상태 | 비고 |
|-------|------|------|
| Phase 1: 기본 구조 | ✅ | 레이아웃, CSS, Security |
| Phase 2: 회원 기능 | ✅ | 4단계 회원가입, 이메일 인증 |
| Phase 3: 게시판 기능 | ✅ | 공지사항, FAQ, 문의, 자료실, 자유게시판 |
| Phase 4-1: 데이터 표준 API | ✅ | 기본규격, 인증규격, 참여자별 처리절차 |
| Phase 4-2: 마이데이터 인증 API 규격 | ✅ | 개별인증 API (4개), 통합인증 API (9개) |
| Phase 4-3: 마이데이터 지원 API 규격 | ✅ | 종합포털 제공 (14개), 사업자/정보제공자 제공 (4개) |
| Phase 4-4: 마이데이터 정보제공 API 규격 - 은행 | ✅ | 은행 업권 (31개 API) |
| Phase 4-4: 마이데이터 정보제공 API 규격 - 나머지 | ⬜ | **다음 작업** (11개 업권) |

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

## 🔄 작업 방식

| 담당 | 작업 |
|------|------|
| **사용자** | 백엔드 Java 코드 직접 생성 |
| **Claude** | 프론트엔드 HTML 템플릿 생성, CSS 수정, 코드 제공 |

---

## 🎯 다음 작업: 마이데이터 정보제공 API 규격 (나머지 11개 업권)

### 아코디언 사이드바 구조 (이미 구현 완료)

`sidebar-api-spec.html`에 12개 업권 모두 메뉴 등록 완료:
```
마이데이터 정보제공 API 규격        (activeGroup='info')
├── 은행 업권 정보제공 API 규격      ✅ 완료 (/info-api/bank)
├── 카드 업권 정보제공 API 규격      ⬜ 다음 (/info-api/card)
├── 보험 업권 정보제공 API 규격      ⬜ (/info-api/insurance)
├── 금융투자 업권 정보제공 API 규격   ⬜ (/info-api/invest)
├── 전자금융 업권 정보제공 API 규격   ⬜ (/info-api/efin)
├── 할부금융 업권 정보제공 API 규격   ⬜ (/info-api/installment)
├── 보증보험 업권 정보제공 API 규격   ⬜ (/info-api/guarantee)
├── 통신 업권 정보제공 API 규격      ⬜ (/info-api/telecom)
├── P2P 업권 정보제공 API 규격       ⬜ (/info-api/p2p)
├── 인수채권 업권 정보제공 API 규격   ⬜ (/info-api/bond)
├── 대부 업권 정보제공 API 규격      ⬜ (/info-api/loan)
└── 서민금융진흥원 API 규격          ⬜ (/info-api/kinfa)
```

### 원본 사이트 업권별 URL 매핑

| 업권 | 원본 URL | 프로젝트 URL |
|------|---------|-------------|
| 은행 | `/mdtb/apg/mac/bas/FSAG0404?id=1` | `/info-api/bank` ✅ |
| 카드 | `/mdtb/apg/mac/bas/FSAG0406?id=2` | `/info-api/card` |
| 보험 | `/mdtb/apg/mac/bas/FSAG0403?id=3` | `/info-api/insurance` |
| 금융투자 | `/mdtb/apg/mac/bas/FSAG0402?id=4` | `/info-api/invest` |
| 전자금융 | `/mdtb/apg/mac/bas/FSAG0405?id=5` | `/info-api/efin` |
| 할부금융 | `/mdtb/apg/mac/bas/FSAG0407?id=6` | `/info-api/installment` |
| 보증보험 | `/mdtb/apg/mac/bas/FSAG0408?id=10` | `/info-api/guarantee` |
| 통신 | `/mdtb/apg/mac/bas/FSAG0409?id=11` | `/info-api/telecom` |
| P2P | `/mdtb/apg/mac/bas/FSAG0410?id=13` | `/info-api/p2p` |
| 인수채권 | `/mdtb/apg/mac/bas/FSAG0411?id=14` | `/info-api/bond` |
| 대부 | `/mdtb/apg/mac/bas/FSAG0412?id=15` | `/info-api/loan` |
| 서민금융진흥원 | (스캔 필요) | `/info-api/kinfa` |

### 업권 추가 작업 절차 (반복)

각 업권별로 아래 절차를 반복:

1. **원본 사이트 스캔**: 해당 업권 URL에서 API 스펙 데이터 추출 (JavaScript로 JSON + HTML 생성)
2. **HTML 템플릿 다운로드**: 브라우저에서 생성된 `{업권}-api.html` 다운로드
3. **파일 배치**: `templates/info-api/` 에 복사
4. **InfoApiController.java**: 해당 업권 메서드 주석 해제

### InfoApiController.java 업권 추가 패턴

```java
// 주석 해제하여 업권 추가
@GetMapping("/card")
public String cardApi(Model model) {
    model.addAttribute("activeGroup", "info");
    model.addAttribute("currentMenu", "/info-api/card");
    model.addAttribute("breadcrumbItems", getBreadcrumbItems("카드 업권 정보제공 API 규격"));
    return "info-api/card-api";
}
```

### 생성할 파일 목록 (남은 업권)

```
src/main/resources/templates/info-api/
├── bank-api.html           # ✅ 완료 (31개 API, ~197KB)
├── card-api.html           # ⬜ Claude가 생성
├── insurance-api.html      # ⬜ Claude가 생성
├── invest-api.html         # ⬜ Claude가 생성
├── efin-api.html           # ⬜ Claude가 생성
├── installment-api.html    # ⬜ Claude가 생성
├── guarantee-api.html      # ⬜ Claude가 생성
├── telecom-api.html        # ⬜ Claude가 생성
├── p2p-api.html            # ⬜ Claude가 생성
├── bond-api.html           # ⬜ Claude가 생성
├── loan-api.html           # ⬜ Claude가 생성
└── kinfa-api.html          # ⬜ Claude가 생성
```

---

## 📂 현재 완료된 파일 구조

```
src/main/java/com/mydata/mydatatestbed/controller/
├── MainController.java
├── MemberController.java
├── SupportController.java
├── ApiGuideController.java          # activeGroup="guide"
├── CertApiController.java          # activeGroup="cert"
├── SupportApiController.java       # activeGroup="support"
└── InfoApiController.java          # ✅ NEW activeGroup="info"

src/main/resources/templates/
├── layout/
│   ├── default-layout.html
│   ├── header.html
│   ├── footer.html
│   ├── sidebar.html                 # 일반 사이드바 (고객지원용)
│   └── sidebar-api-spec.html        # 아코디언 사이드바 (12개 업권 메뉴 포함)
├── api-guide/
│   ├── basic-spec.html              # /api-guide/base
│   ├── auth-spec.html               # /api-guide/auth
│   └── process-spec.html            # /api-guide/process
├── cert-api/
│   ├── individual-api.html          # /cert-api/individual
│   └── integrated-api.html          # /cert-api/integrated
├── support-api/
│   ├── portal-api.html              # /support-api/portal
│   └── provider-api.html            # /support-api/provider
├── info-api/                        # ✅ NEW
│   └── bank-api.html               # /info-api/bank (31개 API 스펙)
└── ...

src/main/resources/static/css/
├── sidebar.css                      # 아코디언 사이드바 스타일 포함
└── api-guide.css                    # API 스펙 카드, 테이블 스타일
```

---

## 🐛 해결된 트러블슈팅 요약

| 문제 | 원인 | 해결 |
|------|------|------|
| API 가이드 사이드바에 세부항목 미표시 | 아코디언 사이드바에 API가이드 그룹 누락 | `sidebar-api-spec.html`에 guide 그룹 추가 |
| 사이드바 템플릿-컨트롤러 불일치 | HTML은 `activeGroup` 기대, 컨트롤러는 `sidebarMenus` 전달 | 컨트롤러를 `activeGroup` 방식으로 통일 |
| 상위/세부 항목 구분 어려움 | 동일한 배경색, 글자 크기 | 세부항목 배경색/보더 추가, 글자 크기 차별화 |
| 원본 사이트 데이터 추출 시 콘텐츠 차단 | 브라우저 보안 정책으로 대용량 텍스트 반환 차단 | JSON/HTML 파일로 다운로드 후 수동 복사 방식 채택 |

---

## 💬 다음 세션 시작하기

1. **카드 업권부터 순차적으로 진행** (원본 사이트 스캔 → HTML 생성 → 다운로드 → 배치)
2. 각 업권 완료 시 **InfoApiController.java** 해당 메서드 주석 해제
3. 모든 업권 완료 후 **문서 업데이트** (README, PROJECT_STATUS, API_SPEC)

---

## 📚 관련 문서

| 문서 | 설명 |
|------|------|
| [README.md](README.md) | 프로젝트 소개, 빠른 시작 |
| [PROJECT_STATUS.md](PROJECT_STATUS.md) | 진행 상황, 파일 구조 |
| [API_SPEC.md](API_SPEC.md) | API 엔드포인트 상세 명세 |
| [TROUBLESHOOTING.md](TROUBLESHOOTING.md) | 트러블슈팅 가이드 |

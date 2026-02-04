# 📋 다음 세션 작업 가이드

> **마지막 업데이트**: 2026-02-03  
> **다음 작업**: Phase 4-1 계속 - 마이데이터 정보제공 API 규격

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
| Phase 4-1: 마이데이터 인증 API 규격 | ✅ | 개별인증 API (4개), 통합인증 API (9개) |
| Phase 4-1: 마이데이터 지원 API 규격 | ✅ | 종합포털 제공 (14개), 사업자/정보제공자 제공 (4개) |
| Phase 4-1: 마이데이터 정보제공 API 규격 | ⬜ | **다음 작업** |

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

## 🎯 다음 작업: 마이데이터 정보제공 API 규격

### 핵심 구조

원본 사이트의 사이드바 하위 메뉴 구조:
```
마이데이터 정보제공 API 규격 (activeGroup='info')
├── 은행
├── 보험
├── 금융투자
├── 전자금융
├── 카드
├── 통신
├── 보증보험
├── P2P
├── 공공
└── ... (스캔 후 확인 필요)
```

> ⚠️ **원본 사이트 스캔 필요**: 하위 메뉴 구성과 각 업종별 API 스펙 내용은 원본 사이트에서 확인해야 합니다.

### 아코디언 사이드바 구조 (이미 구현됨)

모든 API 규격 페이지는 **동일한 아코디언 사이드바**(`sidebar-api-spec.html`)를 공유합니다:
```
API가이드                          (activeGroup='guide')  ✅ 완료
├── 데이터 표준 API 기본규격
├── 데이터 표준 API 인증규격
└── 마이데이터 참여자별 API 처리 절차

마이데이터 인증 API 규격            (activeGroup='cert')   ✅ 완료
├── 개별인증 API
└── 통합인증 API

마이데이터 지원 API 규격            (activeGroup='support') ✅ 완료
├── 지원 API(종합포털 제공)
└── 지원 API(마이데이터사업자/정보제공자 제공)

마이데이터 정보제공 API 규격        (activeGroup='info')   ⬜ 다음 작업
└── (스캔 필요)
```

### 작업 절차

#### 1단계: 원본 사이트 스캔
- 원본 URL에서 "마이데이터 정보제공 API 규격" 섹션 내용 스캔
- 사이드바 하위 메뉴 구조 파악 (업종별 분류)
- 각 하위 페이지의 API 스펙 데이터 추출

#### 2단계: sidebar-api-spec.html 수정
정보제공 API 하위 메뉴 추가 (현재 주석 처리 상태):

```html
<!-- 마이데이터 정보제공 API 규격 -->
<div class="sidebar-group" th:classappend="${activeGroup == 'info'} ? ' active' : ''">
    <a th:href="@{/info-api}" class="sidebar-group-title">마이데이터 정보제공 API 규격</a>
    <ul class="sidebar-group-menu" th:if="${activeGroup == 'info'}">
        <!-- 스캔 후 하위 메뉴 추가 -->
        <li th:classappend="${currentMenu == '/info-api/bank'} ? ' active' : ''">
            <a th:href="@{/info-api/bank}">은행</a>
        </li>
        <!-- ... 업종별 메뉴 -->
    </ul>
</div>
```

#### 3단계: InfoApiController.java 생성 (사용자가 생성)

```java
package com.mydata.mydatatestbed.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/info-api")
public class InfoApiController {

    @GetMapping
    public String redirectToDefault() {
        return "redirect:/info-api/bank";  // 첫 번째 업종으로 리다이렉트
    }

    @GetMapping("/bank")
    public String bankApi(Model model) {
        model.addAttribute("activeGroup", "info");
        model.addAttribute("currentMenu", "/info-api/bank");
        model.addAttribute("breadcrumbItems", getBreadcrumbItems("은행"));
        return "info-api/bank-api";
    }

    // ... 업종별 메서드 추가 (스캔 후 확정)

    private List<Map<String, String>> getBreadcrumbItems(String current) {
        return List.of(
                Map.of("name", "홈", "url", "/"),
                Map.of("name", "API가이드", "url", "/api-guide"),
                Map.of("name", "마이데이터 정보제공 API 규격", "url", "/info-api"),
                Map.of("name", current, "url", "")
        );
    }
}
```

#### 4단계: SecurityConfig.java 확인
`/info-api/**`는 이미 permitAll 예약되어 있음 (API_SPEC.md 참고):
```java
.requestMatchers("/info-api/**").permitAll()
```

#### 5단계: HTML 템플릿 생성 (Claude가 생성)

```
src/main/resources/templates/info-api/
├── bank-api.html        # /info-api/bank    (은행)
├── insurance-api.html   # /info-api/insurance (보험)
├── invest-api.html      # /info-api/invest   (금융투자)
├── efin-api.html        # /info-api/efin     (전자금융)
├── card-api.html        # /info-api/card     (카드)
└── ... (스캔 후 확정)
```

### 생성할 파일 목록

```
src/main/java/.../controller/
└── InfoApiController.java          # 사용자가 생성 (위 템플릿 참고)

src/main/resources/templates/info-api/
├── bank-api.html                   # Claude가 생성
├── insurance-api.html              # Claude가 생성
└── ... (업종별)                     # Claude가 생성
```

### 참고: 기존 패턴

인증 API, 지원 API 규격에서 사용된 HTML 구조를 동일하게 적용:
- `.api-spec-card` 컨테이너
- `.api-version-badge` 버전 배지
- `.method-badge` HTTP Method 배지
- `.api-toc` Table of Contents
- `.api-msg-table` 메시지 명세 테이블

CSS는 이미 `api-guide.css`에 모두 정의되어 있으므로 추가 CSS 불필요.

---

## 📂 현재 완료된 파일 구조

```
src/main/java/com/mydata/mydatatestbed/controller/
├── MainController.java
├── MemberController.java
├── SupportController.java
├── ApiGuideController.java          # activeGroup="guide"
├── CertApiController.java          # activeGroup="cert"
└── SupportApiController.java       # activeGroup="support"

src/main/resources/templates/
├── layout/
│   ├── default-layout.html
│   ├── header.html
│   ├── footer.html
│   ├── sidebar.html                 # 일반 사이드바 (고객지원용)
│   └── sidebar-api-spec.html        # 아코디언 사이드바 (API 규격용)
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

---

## 💬 다음 세션 시작하기

1. 원본 사이트에서 **마이데이터 정보제공 API 규격** 스캔 요청
2. 사이드바 하위 메뉴 구조 확인
3. **sidebar-api-spec.html** 하위 메뉴 추가
4. **InfoApiController.java** 생성 (위 템플릿 참고)
5. **SecurityConfig**에 `/info-api/**` permitAll 추가
6. 업종별 HTML 템플릿 생성

---

## 📚 관련 문서

| 문서 | 설명 |
|------|------|
| [README.md](README.md) | 프로젝트 소개, 빠른 시작 |
| [PROJECT_STATUS.md](PROJECT_STATUS.md) | 진행 상황, 파일 구조 |
| [API_SPEC.md](API_SPEC.md) | API 엔드포인트 상세 명세 |
| [TROUBLESHOOTING.md](TROUBLESHOOTING.md) | 트러블슈팅 가이드 |

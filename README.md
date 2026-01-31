# 🏦 금융분야 마이데이터 테스트베드

> 금융보안원 마이데이터 테스트베드 클론 프로젝트  
> 원본 사이트: https://developers.mydatakorea.org/mdtb/

마이데이터 서비스 개발자를 위한 API 테스트 환경을 제공하는 웹 애플리케이션입니다.

---

## 🚀 빠른 시작

### 요구사항
- Java 21+
- Gradle 8.x

### 실행

```bash
# 프로젝트 클론
git clone https://github.com/YangGyunShin/mydata-testbed.git
cd mydata-testbed

# 애플리케이션 실행
./gradlew bootRun

# 브라우저에서 접속
http://localhost:8080
```

### H2 콘솔 (개발용)
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:file:./data/testdb
Username: sa
Password: (비워두기)
```

---

## 📋 주요 기능

| 기능 | 설명 | 상태 |
|------|------|------|
| **회원관리** | 회원가입(4단계), 로그인/로그아웃, 이메일 인증 | ✅ 완료 |
| **공지사항** | 목록/상세, 검색, 페이징 | ✅ 완료 |
| **FAQ** | 카테고리별 필터, 아코디언 UI | ✅ 완료 |
| **문의하기** | 1:1 문의 작성, 내 문의 목록/상세 | ✅ 완료 |
| **자료실** | 목록/상세, 이전글/다음글, 파일 다운로드 | ✅ 완료 |
| **자유게시판** | CRUD, 파일 업로드/다운로드, 권한 체크 | ✅ 완료 |
| **테스트베드** | API 테스트 환경 | ⏳ 예정 |
| **적합성 심사** | 기능적합성/보안취약점 점검 | ⏳ 예정 |

---

## 🛠 기술 스택

| 분류 | 기술 |
|------|------|
| **Backend** | Java 21, Spring Boot 3.4.1, Spring Security 6.x, Spring Data JPA |
| **Frontend** | Thymeleaf, HTML5/CSS3, JavaScript |
| **Database** | H2 (개발), MySQL (운영 예정) |
| **Build** | Gradle |

---

## 📚 문서

| 문서 | 설명 |
|------|------|
| [PROJECT_STATUS.md](PROJECT_STATUS.md) | 📊 진행 상황 및 파일 구조 |
| [API_SPEC.md](API_SPEC.md) | 📚 API 엔드포인트 명세 |
| [TROUBLESHOOTING.md](TROUBLESHOOTING.md) | 🐛 트러블슈팅 가이드 |
| [NEXT_SESSION_TEMPLATE.md](NEXT_SESSION_TEMPLATE.md) | 📋 코딩 컨벤션 및 다음 작업 |

---

## 📁 프로젝트 구조 (요약)

```
src/main/java/com/mydata/mydatatestbed/
├── config/          # SecurityConfig, WebConfig, AuditConfig
├── controller/      # MemberController, SupportController, MainController
├── entity/          # Member, Notice, Faq, Inquiry, Resource, Board + enums
├── vo/              # Email, Password, Phone (Value Objects)
├── repository/      # JPA Repositories
├── dto/             # Request/Response DTOs
├── mapper/          # Entity ↔ DTO 변환
├── service/         # 비즈니스 로직 (인터페이스 + impl)
├── util/            # FileSizeFormatter 등 유틸리티
├── security/        # CustomUserDetails, CustomUserDetailsService
└── validation/      # 커스텀 검증 어노테이션

src/main/resources/
├── templates/       # Thymeleaf 템플릿
│   ├── layout/      # 레이아웃 (header, footer, sidebar)
│   ├── fragments/   # 공통 조각 (breadcrumb, pagination)
│   ├── member/      # 회원 (로그인, 회원가입)
│   └── support/     # 고객지원 (notice, faq, inquiry, resource, board)
├── static/          # CSS, JS, Images
├── application.yml  # 설정
└── data.sql         # 초기 데이터 (FAQ, Resource)
```

---

## 🔗 구현된 URL

### 공개 URL

| URL | 설명 |
|-----|------|
| `/` | 메인 페이지 |
| `/member/login` | 로그인 |
| `/member/signup/step1~4` | 회원가입 |
| `/support/notice` | 공지사항 목록 |
| `/support/notice/{id}` | 공지사항 상세 |
| `/support/faq` | FAQ |
| `/support/resource` | 자료실 목록 |
| `/support/resource/{id}` | 자료실 상세 |
| `/support/board` | 자유게시판 목록 |
| `/support/board/{id}` | 자유게시판 상세 |
| `/support/board/{id}/download` | 첨부파일 다운로드 |

### 인증 필요 URL

| URL | 설명 |
|-----|------|
| `/support/inquiry` | 문의 작성/목록/상세 |
| `/support/board/write` | 게시글 작성 |
| `/support/board/{id}/edit` | 게시글 수정 (작성자/관리자) |
| `/support/board/{id}/delete` | 게시글 삭제 (작성자/관리자) |

---

## 🎯 개발 진행률

```
Phase 1: 기본 구조      [██████████] 100% ✅
Phase 2: 회원 기능      [██████████] 100% ✅
Phase 3: 게시판 기능    [██████████] 100% ✅
Phase 4: 핵심 기능      [░░░░░░░░░░]   0% ⏳
Phase 5: 완성도         [░░░░░░░░░░]   0% ⏳
```

---

## 📞 참고 자료

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [금융분야 마이데이터 테스트베드](https://developers.mydatakorea.org/mdtb/)

---

## 📝 라이선스

This project is for educational purposes only.

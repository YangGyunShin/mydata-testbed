# 🐛 트러블슈팅 가이드

프로젝트 개발 중 발생한 버그와 해결 방법을 정리한 문서입니다.

---

## 목차

- [Spring Security 관련](#spring-security-관련)
- [Thymeleaf 관련](#thymeleaf-관련)
- [Spring MVC 관련](#spring-mvc-관련)
- [JPA 관련](#jpa-관련)
- [이메일 인증 관련](#이메일-인증-관련)
- [데이터베이스 관련](#데이터베이스-관련)
- [Controller 관련](#controller-관련)
- [파일 업로드 관련](#파일-업로드-관련)

---

## Spring Security 관련

### 1. 이메일 인증 링크 클릭 시 로그인 페이지로 리다이렉트됨

**증상**
- 이메일의 "이메일 인증하기" 버튼 클릭
- `/member/verify-email?token=xxx` URL로 이동해야 하는데
- `/member/login` 페이지로 자동 리다이렉트됨

**원인**
- Spring Security가 `/member/verify-email` URL을 인증이 필요한 페이지로 인식
- 로그인하지 않은 상태에서 접근하면 자동으로 로그인 페이지로 리다이렉트

**해결 방법**
`SecurityConfig.java`에서 해당 URL을 `permitAll()`에 추가:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
            .requestMatchers("/", "/intro/**", "/api-guide/**").permitAll()
            .requestMatchers(
                "/member/login",
                "/member/signup/**",
                "/member/verify-email",       // ✅ 추가
                "/member/resend-verification" // ✅ 추가
            ).permitAll()
            .anyRequest().authenticated()
        );
    return http.build();
}
```

**교훈**
- 로그인 없이 접근해야 하는 URL은 반드시 `permitAll()`에 추가할 것
- 이메일 인증, 비밀번호 재설정 등 비로그인 상태에서 접근하는 기능은 특히 주의

---

### 2. 로그인 시 "Bad credentials" 또는 "User is disabled" 오류

**증상**
- 올바른 이메일/비밀번호를 입력해도 로그인 실패
- 에러 메시지: "Bad credentials" 또는 "User is disabled"

**원인**
- `CustomUserDetails.isEnabled()` 메서드가 `member.isEmailVerified()`를 반환
- 이메일 인증을 완료하지 않은 회원은 `emailVerified = false`
- Spring Security는 `isEnabled() = false`인 계정의 로그인을 거부

```java
// CustomUserDetails.java
@Override
public boolean isEnabled() {
    return member.isEmailVerified();  // false면 로그인 불가!
}
```

**해결 방법**
1. **(권장)** 이메일 인증 기능을 완전히 구현하여 인증 완료 후 로그인 가능하도록 함
2. **(테스트용)** 임시로 `isEnabled()`가 항상 `true`를 반환하도록 수정

```java
// 테스트용 임시 해결 (운영에서는 사용 금지!)
@Override
public boolean isEnabled() {
    return true;  // 또는 member.isEmailVerified();
}
```

**교훈**
- Spring Security의 `UserDetails` 인터페이스는 여러 계정 상태를 체크함
- `isEnabled()`, `isAccountNonLocked()`, `isAccountNonExpired()`, `isCredentialsNonExpired()` 중 하나라도 `false`면 로그인 실패
- 이메일 인증 기능을 구현할 때는 이 연결고리를 먼저 이해해야 함

---

### 3. Spring Security URL 패턴 순서 문제

**증상**
- `/support/board/write`가 인증 필요로 설정했는데 인증 없이 접근됨
- 또는 `/support/board/{id}`가 인증 없이 접근해야 하는데 로그인 페이지로 리다이렉트됨

**원인**
- Spring Security는 URL 패턴을 **순서대로** 매칭
- 일반적인 패턴(`/support/**`)이 구체적인 패턴(`/support/board/write`)보다 먼저 오면 일반 패턴에 매칭됨

```java
// ❌ 잘못된 순서
.requestMatchers("/support/**").permitAll()              // 이게 먼저 매칭!
.requestMatchers("/support/board/write").authenticated() // 무시됨
```

**해결 방법**
**구체적인 패턴을 먼저, 일반적인 패턴을 나중에** 배치:

```java
// ✅ 올바른 순서
.requestMatchers("/support/inquiry/**").authenticated()
.requestMatchers("/support/board/write").authenticated()
.requestMatchers("/support/board/*/edit").authenticated()
.requestMatchers("/support/board/*/delete").authenticated()
.requestMatchers("/support/**").permitAll()  // 일반 패턴은 마지막에
```

**교훈**
- Spring Security URL 매칭은 **first-match-wins** 방식
- 구체적인 패턴 → 일반적인 패턴 순서로 배치할 것
- 새 URL 추가 시 기존 패턴과의 순서 관계 확인 필수

---

## Thymeleaf 관련

### 4. 브레드크럼에서 SpEL 오류 발생

**증상**
- 페이지 로딩 시 Thymeleaf 오류 발생
- 에러 메시지: `SpelEvaluationException` 또는 `Property or field 'name' cannot be found`

**원인**
- `breadcrumb.html` fragment에서 `${items}` 변수를 사용하는데
- Controller에서 해당 데이터를 Model에 담지 않음
- 또는 데이터 구조가 맞지 않음 (예: `name`, `url` 필드가 없는 객체 전달)

**해결 방법**
Controller에서 브레드크럼 데이터를 올바른 형식으로 전달:

```java
@GetMapping("/signup/step1")
public String signupStep1(Model model) {
    List<Map<String, String>> breadcrumbItems = List.of(
        Map.of("name", "로그인", "url", "/member/login"),
        Map.of("name", "회원가입", "url", "")  // 현재 페이지는 url 비움
    );
    model.addAttribute("breadcrumbItems", breadcrumbItems);
    return "member/signup-step1-terms";
}
```

**교훈**
- Thymeleaf fragment에서 사용하는 변수는 반드시 Controller에서 전달해야 함
- fragment의 파라미터 이름과 Model에 담는 이름이 일치해야 함

---

### 5. 사이드바가 표시되지 않는 문제

**증상**
- 자유게시판 페이지에서 사이드바가 나타나지 않음
- 다른 페이지 (공지사항, FAQ 등)에서는 정상 표시

**원인**
- 템플릿에서 사이드바 호출 시 변수명이 다른 페이지와 불일치
- 기존 페이지들: 하드코딩된 문자열 `'고객지원'` 사용
- 문제 페이지: `${menuTitle}` 변수 사용 (Controller에서 전달 필요)

```html
<!-- ❌ 잘못된 코드 -->
<th:block th:replace="~{layout/sidebar :: sidebar(${menuTitle}, ${menuItems}, ${currentMenu})}"></th:block>

<!-- ✅ 올바른 코드 (기존 패턴) -->
<th:block th:replace="~{layout/sidebar :: sidebar('고객지원', ${sidebarMenus}, ${currentMenu})}"></th:block>
```

**해결 방법**
모든 고객지원 페이지에서 동일한 패턴 사용:

```html
<th:block th:replace="~{layout/sidebar :: sidebar('고객지원', ${sidebarMenus}, ${currentMenu})}"></th:block>
```

**교훈**
- 여러 페이지에서 동일한 레이아웃을 사용할 때는 **기존 패턴을 따를 것**
- 새 페이지 작성 시 기존 비슷한 페이지의 코드를 참고하여 일관성 유지

---

### 6. Thymeleaf에서 정적 클래스 접근 금지 오류

**증상**
- FAQ 페이지 로딩 시 Thymeleaf 템플릿 파싱 오류 발생
- 에러 메시지: `Instantiation of new objects and access to static classes is forbidden in this context`

**원인**
Thymeleaf에서 `T(System).lineSeparator()` 같은 정적 클래스 접근 시도:

```html
<!-- ❌ 잘못된 코드 -->
<p th:utext="${#strings.replace(faq.answer, T(System).lineSeparator(), '&lt;br/&gt;')}">
```

Thymeleaf의 보안 정책상 `T()` 연산자를 통한 정적 클래스 접근이 기본적으로 차단됨.

**해결 방법**

방법 1: 단순히 `th:text` 사용 (HTML 이스케이프 자동 처리)
```html
<!-- ✅ 권장 -->
<p th:text="${faq.answer}"></p>
```

방법 2: CSS로 줄바꿈 처리
```css
.faq-answer p {
    white-space: pre-line;  /* \n을 줄바꿈으로 표시 */
}
```

방법 3: Controller/Service에서 미리 변환
```java
// Service에서 HTML 변환
public String convertNewlinesToBr(String text) {
    return text.replace("\n", "<br/>");
}
```

**교훈**
- Thymeleaf는 보안상 정적 클래스 접근을 차단함
- `T()`, `new` 등의 SpEL 표현식은 제한될 수 있음
- 텍스트 포맷팅은 CSS나 백엔드에서 처리하는 것이 안전

---

## Spring MVC 관련

### 7. 폼 데이터가 DTO에 바인딩되지 않음

**증상**
- 회원가입 폼 제출 시 `MemberSignupRequestDto`의 모든 필드가 `null`
- `@Valid` 검증에서 모든 필드가 실패

**원인**
- `MemberSignupRequestDto`에 `@Setter`가 없음
- Spring MVC는 폼 데이터 바인딩 시 기본 생성자로 객체를 만들고 Setter로 값을 주입
- `@Builder`만 있으면 Setter가 없어서 값이 주입되지 않음

**해결 방법**
RequestDto에는 `@Setter` 추가:

```java
@Getter
@Setter  // ✅ 추가!
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignupRequestDto {
    private String email;
    private String password;
    // ...
}
```

**교훈**
- **Entity, ResponseDto**: `@Setter` 사용 금지, `@Builder`만 사용
- **RequestDto**: `@Setter` 필수 (Spring MVC 폼 바인딩을 위해)
- 프로젝트 컨벤션에서 "No Setter"는 Entity와 ResponseDto에만 적용

---

### 8. 자유게시판 글 작성 시 제목/내용이 null로 바인딩됨

**증상**
- 자유게시판 글 작성 폼 제출
- 제목과 내용을 입력했는데 "제목을 입력해주세요", "내용을 입력해주세요" 검증 오류 발생
- `BoardRequestDto`의 `title`, `content`가 null

**원인**
`BoardRequestDto`에 `@Setter`가 누락됨:

```java
// ❌ 잘못된 코드
@Getter
@NoArgsConstructor
public class BoardRequestDto {
    @NotBlank
    private String title;
    
    @NotBlank
    private String content;
}
```

Spring MVC 폼 바인딩 흐름:
1. 사용자 폼 제출
2. Spring이 `BoardRequestDto` 인스턴스 생성 (`@NoArgsConstructor`)
3. **Setter로 폼 데이터 주입** ← 여기서 실패 (Setter 없음)
4. `@Valid`로 검증 수행 → 모든 필드 null → 검증 실패

**해결 방법**

```java
// ✅ 올바른 코드
@Getter
@Setter  // 추가!
@NoArgsConstructor
public class BoardRequestDto {
    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 200, message = "제목은 200자 이내로 입력해주세요.")
    private String title;
    
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
```

**Request DTO에 @Setter가 안전한 이유**

| 구분 | Setter 사용 | 이유 |
|------|-------------|------|
| Entity | ❌ 금지 | 비즈니스 메서드로만 상태 변경, 불변성 보장 |
| Request DTO | ✅ 허용 | 폼 바인딩 필요, Validation 적용, 짧은 생명주기 |
| Response DTO | ❌ 불필요 | 읽기 전용, Builder로 생성 |

**교훈**
- RequestDto는 **폼 바인딩용**이므로 `@Setter` 필수
- Setter가 있어도 `@Valid` + Validation 어노테이션으로 검증됨
- Entity와 달리 RequestDto는 요청 처리 후 바로 버려지므로 보안 위험 없음

---

## JPA 관련

### 9. 공지사항 목록에서 작성자 조회 시 N+1 문제

**증상**
- 공지사항 목록 조회 시 쿼리가 N+1번 실행됨
- 10개 목록 조회 시 11번의 쿼리 발생 (목록 1번 + 작성자 조회 10번)
- 로그에 SELECT 쿼리가 여러 번 찍힘

**원인**
- `@ManyToOne`의 기본 FetchType이 `EAGER`
- LAZY로 바꿔도 Thymeleaf에서 `notice.author.name` 접근 시 각각 쿼리 발생
- 연관 엔티티를 개별적으로 조회하면서 쿼리 폭발

**해결 방법**
**방법 1: Fetch Join 사용 (권장)**
```java
// NoticeRepository.java
@Query("SELECT n FROM Notice n LEFT JOIN FETCH n.author WHERE n.id = :id")
Optional<Notice> findByIdWithAuthor(@Param("id") Long id);
```

**방법 2: EntityGraph 사용**
```java
@EntityGraph(attributePaths = {"author"})
@Query("SELECT n FROM Notice n WHERE n.id = :id")
Optional<Notice> findByIdWithAuthor(@Param("id") Long id);
```

**방법 3: DTO Projection**
```java
@Query("SELECT new com.mydata.dto.NoticeListDto(n.id, n.title, m.name) " +
       "FROM Notice n LEFT JOIN n.author m")
List<NoticeListDto> findAllAsDto();
```

**교훈**
- `@ManyToOne`은 기본이 `EAGER`이므로 명시적으로 `LAZY` 지정 필요
- 목록 조회 시 연관 엔티티가 필요하면 Fetch Join 사용
- 상세 조회와 목록 조회에서 다른 쿼리 메서드 사용 권장

---

### 10. LazyInitializationException

**증상**
- 연관 엔티티 접근 시 `LazyInitializationException` 발생
- 에러 메시지: `could not initialize proxy - no Session`

**원인**
- `@ManyToOne(fetch = FetchType.LAZY)` 등으로 지연 로딩 설정된 연관 엔티티
- 트랜잭션 밖에서 해당 엔티티에 접근하려고 함

**해결 방법**
1. Service 메서드에 `@Transactional(readOnly = true)` 추가
2. 또는 Fetch Join 사용
3. 또는 DTO로 필요한 데이터만 조회

```java
// 방법 1: 트랜잭션 범위 확장
@Transactional(readOnly = true)
public MemberResponseDto getMember(Long id) {
    Member member = memberRepository.findById(id).orElseThrow();
    return memberMapper.toResponseDto(member);
}

// 방법 2: Fetch Join
@Query("SELECT m FROM Member m JOIN FETCH m.company WHERE m.id = :id")
Optional<Member> findByIdWithCompany(@Param("id") Long id);
```

---

## 이메일 인증 관련

### 11. Gmail SMTP 인증 실패

**증상**
- 이메일 발송 시 `AuthenticationFailedException` 발생
- 에러 메시지: `535-5.7.8 Username and Password not accepted`

**원인**
- Gmail 일반 비밀번호 사용 (앱 비밀번호가 아님)
- 또는 2단계 인증이 활성화되지 않음

**해결 방법**
1. Google 계정에서 2단계 인증 활성화
2. 앱 비밀번호 생성: Google 계정 → 보안 → 2단계 인증 → 앱 비밀번호
3. "메일" 선택 후 생성된 16자리 비밀번호를 환경변수에 설정

```bash
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=abcd efgh ijkl mnop  # 16자리 앱 비밀번호
```

**교훈**
- Gmail SMTP는 일반 비밀번호로는 인증 불가 (보안 정책)
- 반드시 앱 비밀번호를 생성해서 사용해야 함

---

### 12. 이메일 인증 토큰이 만료됨

**증상**
- 이메일 링크 클릭 시 "만료된 토큰입니다" 오류
- 24시간이 지나지 않았는데도 발생

**원인**
- 서버 시간과 DB 시간이 다름
- 또는 토큰 생성 시 시간 계산 오류

**해결 방법**
1. 서버와 DB의 타임존 설정 확인
2. `EmailVerificationToken` 엔티티의 시간 처리 로직 확인

```yaml
# application.yml에 타임존 설정
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          time_zone: Asia/Seoul
```

---

## 데이터베이스 관련

### 13. 회원가입 후 로그인 실패 - 서버 재시작 시 데이터 사라짐

**증상**
- 회원가입 완료 후 로그인 시 "없는 계정" 오류 발생
- 서버를 재시작하면 가입한 계정이 사라짐
- 회원가입을 다시 해도 동일한 문제 반복

**원인**
H2 메모리 DB를 사용하면 서버 종료 시 모든 데이터가 삭제됩니다.

```yaml
# 문제가 되는 설정
spring:
  datasource:
    url: jdbc:h2:mem:testdb  # 메모리 DB - 서버 종료 시 데이터 삭제!
```

**해결 방법**
H2 파일 DB로 변경하여 데이터를 영구 저장:

```yaml
# application.yml
spring:
  datasource:
    # 파일 DB로 변경
    url: jdbc:h2:file:./data/testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:

  jpa:
    hibernate:
      ddl-auto: update  # create → update로 변경 (기존 데이터 유지)
```

**.gitignore에 data 폴더 추가:**
```gitignore
### H2 Database ###
data/
```

| 설정 | 설명 |
|------|------|
| `file:./data/testdb` | 프로젝트 루트의 `data` 폴더에 `testdb.mv.db` 파일로 저장 |
| `DB_CLOSE_DELAY=-1` | 마지막 연결이 닫혀도 DB 유지 |
| `DB_CLOSE_ON_EXIT=FALSE` | JVM 종료 시에도 DB 파일 유지 |
| `ddl-auto: update` | 기존 테이블/데이터 유지, 스키마 변경만 반영 |

**H2 콘솔 접속 정보:**
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:file:./data/testdb
Username: sa
Password: (비워두기)
```

**교훈**
- 개발 환경에서도 데이터 유지가 필요하면 파일 DB 사용
- `mem:` = 메모리 DB (휘발성), `file:` = 파일 DB (영구)
- 운영 환경에서는 MySQL/PostgreSQL 등 실제 DBMS 사용

---

### 14. ddl-auto: create로 인한 데이터 손실

**증상**
- 서버 재시작 후 기존 데이터가 모두 삭제됨
- 테이블이 매번 새로 생성됨

**원인**
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create  # 서버 시작 시 테이블 DROP 후 CREATE
```

**해결 방법**
개발 환경에서는 `update`, 운영 환경에서는 `validate` 또는 `none` 사용:

```yaml
# 개발 환경
ddl-auto: update  # 기존 테이블 유지, 변경사항만 반영

# 운영 환경
ddl-auto: validate  # 스키마 검증만, 변경하지 않음
```

| 옵션 | 동작 | 권장 환경 |
|------|------|----------|
| `create` | DROP + CREATE | 테스트용 |
| `create-drop` | CREATE + 종료 시 DROP | 테스트용 |
| `update` | 변경사항만 반영 | 개발 |
| `validate` | 검증만, 변경 안 함 | 운영 |
| `none` | 아무것도 안 함 | 운영 |

---

### 15. data.sql이 실행되지 않는 문제

**증상**
- `src/main/resources/data.sql`에 INSERT 문 작성
- 서버 시작 시 데이터가 삽입되지 않음
- H2 콘솔에서 테이블 확인 시 비어있음

**원인**
Spring Boot 2.5 이상에서는 `data.sql`이 Hibernate가 테이블을 생성하기 **전에** 실행됨:

```
기존 순서: schema.sql → Hibernate ddl-auto → data.sql
Spring Boot 2.5+: data.sql → Hibernate ddl-auto (테이블 없음!) → 실패!
```

**해결 방법**
`application.yml`에 아래 설정 추가:

```yaml
spring:
  jpa:
    defer-datasource-initialization: true  # Hibernate 후에 data.sql 실행
  
  sql:
    init:
      mode: always  # 항상 실행 (embedded DB만이면 embedded)
```

| 설정 | 설명 |
|------|------|
| `defer-datasource-initialization: true` | Hibernate가 테이블 생성 후 data.sql 실행 |
| `sql.init.mode: always` | 항상 SQL 초기화 스크립트 실행 |
| `sql.init.mode: embedded` | 내장 DB(H2)일 때만 실행 |
| `sql.init.mode: never` | 절대 실행 안 함 |

**주의사항**
`mode: always`는 서버 재시작마다 data.sql을 실행하므로 중복 데이터 삽입 발생 가능:

```sql
-- data.sql 상단에 삭제문 추가
DELETE FROM faqs;

-- 이후 INSERT문 작성
INSERT INTO faqs (category, question, answer, order_num, active, created_at, updated_at) VALUES ...;
```

또는 초기 데이터 로드 후 `mode: never`로 변경.

**교훈**
- Spring Boot 2.5+ 업그레이드 시 SQL 초기화 순서 변경 주의
- 파일 DB 사용 시 중복 데이터 문제 고려 필요
- 운영 환경에서는 Flyway/Liquibase 등 마이그레이션 도구 사용 권장

---

## Controller 관련

### 16. 헬퍼 메서드 시그니처 불일치 오류

**증상**
- 컴파일 오류 발생
- 에러 메시지: `method createResourceBreadcrumb in class SupportController cannot be applied to given types`

**원인**
- 헬퍼 메서드(예: `createResourceBreadcrumb`)의 파라미터 개수와 호출부가 일치하지 않음
- 코드 수정 과정에서 메서드 시그니처가 변경되었으나 호출부는 업데이트되지 않음

```java
// ❌ 메서드 정의와 호출부 불일치

// 메서드 정의 (파라미터 없음)
private List<Map<String, String>> createResourceBreadcrumb() {
    return List.of(...);
}

// 호출부 (파라미터 2개 전달)
model.addAttribute("breadcrumbItems", createResourceBreadcrumb("자료실", "/support/resource"));
```

**해결 방법**

**방법 1: 파라미터가 필요한 경우**
```java
// 메서드 정의
private List<Map<String, String>> createResourceBreadcrumb(String pageName, String pageUrl) {
    return List.of(
            Map.of("name", "고객지원", "url", "#"),
            Map.of("name", pageName, "url", pageUrl)
    );
}

// 호출부
model.addAttribute("breadcrumbItems", createResourceBreadcrumb("자료실", "/support/resource"));
```

**방법 2: 파라미터 없는 버전 (고정값)**
```java
// 메서드 정의
private List<Map<String, String>> createResourceBreadcrumb() {
    return List.of(
            Map.of("name", "고객지원", "url", "#"),
            Map.of("name", "자료실", "url", "/support/resource")
    );
}

// 호출부
model.addAttribute("breadcrumbItems", createResourceBreadcrumb());
```

**교훈**
- 헬퍼 메서드 수정 시 모든 호출부도 함께 확인할 것
- IDE의 "Find Usages" 기능 활용 권장
- 비슷한 기능의 메서드들은 패턴을 통일 (예: 모든 브레드크럼 메서드는 파라미터 2개 또는 0개로 통일)

---

### 17. 중복 회원 생성 버그

**증상**
- 회원가입 완료 버튼을 여러 번 누르면 중복 회원이 생성됨
- 또는 Step 4 POST에서 회원이 또 생성됨

**원인**
Step 3 POST와 Step 4 POST 양쪽에서 `memberService.signup()`을 호출

```java
// ❌ 잘못된 구조
@PostMapping("/signup/step3")
public String step3(...) {
    memberService.signup(requestDto);  // 여기서 생성
    return "redirect:/member/signup/step4";
}

@PostMapping("/signup/step4")
public String step4(...) {
    memberService.signup(requestDto);  // 또 생성! (중복)
    return "redirect:/member/signup/complete";
}
```

**해결 방법**
Step 3에서만 회원 생성, Step 4는 안내 페이지로만 사용:

```java
// ✅ 올바른 구조
@PostMapping("/signup/step3")
public String step3Process(...) {
    // 1. 회원 생성 (emailVerified = false)
    memberService.signup(requestDto);
    
    // 2. 인증 메일 발송
    emailService.sendVerificationEmail(requestDto.getEmail());
    
    return "member/signup-step4-email";  // Step 4 안내 페이지로 이동
}

// Step 4 POST는 제거하거나 주석 처리
// 이메일 링크 클릭 → /member/verify-email → 인증 완료
```

**올바른 회원가입 흐름:**
```
Step 3 POST → 회원 생성 + 메일 발송 → Step 4 (안내 페이지)
    ↓
사용자가 메일의 링크 클릭
    ↓
GET /member/verify-email?token=xxx → 인증 완료 → 로그인 가능
```

---

## 파일 업로드 관련

### 18. 파일 없이 수정해도 파일 저장 시도 오류

**증상**
- 자유게시판 글 수정 시 파일을 첨부하지 않았는데 "파일 저장에 실패했습니다" 오류 발생
- `FileServiceImpl.saveFile()` 호출됨

**원인**
`file.isEmpty()` 체크가 불충분함. 브라우저에 따라 파일을 선택하지 않아도 빈 `MultipartFile` 객체가 전송되어 `isEmpty()`가 `false`를 반환할 수 있음.

```java
// ❌ 불충분한 체크
if (file != null && !file.isEmpty()) {
    fileService.saveFile(file, subDir);  // 빈 파일이어도 호출될 수 있음
}
```

**해결 방법**
더 강력한 파일 체크 메서드 사용:

```java
/**
 * 파일 존재 여부를 안전하게 확인
 */
private boolean hasFile(MultipartFile file) {
    return file != null 
            && !file.isEmpty() 
            && file.getSize() > 0                              // 크기가 0보다 커야 함
            && file.getOriginalFilename() != null 
            && !file.getOriginalFilename().trim().isEmpty();   // 파일명이 있어야 함
}

// 사용
if (hasFile(file)) {
    fileService.saveFile(file, subDir);
}
```

**교훈**
- `MultipartFile.isEmpty()`만으로는 브라우저 호환성 문제가 있을 수 있음
- 파일 크기와 파일명까지 추가로 체크하면 안전

---

### 19. 파일 저장 시 FileNotFoundException 발생

**증상**
- PDF 파일 첨부 후 글 작성/수정 시 `FileNotFoundException` 발생
- 에러 메시지: `No such file or directory`
- 경로가 Tomcat 임시 디렉토리 안으로 잘못 설정됨:
  ```
  /private/var/folders/.../tomcat.../ROOT/./uploads/board/xxx.pdf
  ```

**원인**
`file.transferTo()`가 **상대 경로**를 Tomcat 임시 디렉토리 기준으로 해석함:

```java
// ❌ 문제가 되는 코드
Path uploadPath = Paths.get(uploadDir, subDir);  // 상대 경로 "./uploads"
file.transferTo(filePath.toFile());              // Tomcat 기준으로 해석
```

**해결 방법**
**절대 경로로 변환**하면 `transferTo()`도 정상 동작:

```java
// ✅ 올바른 코드
Path uploadPath = Paths.get(uploadDir, subDir)
        .toAbsolutePath()   // 절대 경로로 변환
        .normalize();       // "..", "." 같은 경로 정규화

if (!Files.exists(uploadPath)) {
    Files.createDirectories(uploadPath);
}

Path filePath = uploadPath.resolve(savedFilename);
file.transferTo(filePath.toFile());  // 절대 경로면 정상 동작
```

**`transferTo()` vs `Files.copy()` 비교**

| 구분 | transferTo() | Files.copy() |
|------|--------------|--------------|
| 동작 | 이동 시도 → 실패 시 복사 | 항상 복사 |
| 성능 | 더 빠를 수 있음 (이동 시) | 일반적 |
| 경로 처리 | 절대 경로 필요 | 절대 경로 필요 |
| 결론 | **절대 경로면 둘 다 OK** | **절대 경로면 둘 다 OK** |

**교훈**
- 파일 저장 시 **항상 절대 경로** 사용
- `.toAbsolutePath().normalize()` 패턴 적용
- 상대 경로(`./uploads`)는 서블릿 컨테이너에 따라 해석이 달라질 수 있음

---

## 버그 보고 템플릿

새로운 버그를 발견하면 아래 형식으로 추가해주세요:

```markdown
### N. 버그 제목

**증상**
- 어떤 현상이 발생했는지

**원인**
- 왜 이런 현상이 발생했는지

**해결 방법**
코드 또는 설정 변경 내용

**교훈**
- 이 버그를 통해 배운 점
```

---

## 참고 자료

- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Spring MVC Data Binding](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/modelattrib-method-args.html)
- [H2 Database Connection Modes](https://h2database.com/html/features.html#connection_modes)

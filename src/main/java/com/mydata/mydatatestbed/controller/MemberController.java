package com.mydata.mydatatestbed.controller;

import com.mydata.mydatatestbed.dto.member.MemberSignupRequestDto;
import com.mydata.mydatatestbed.service.EmailService;
import com.mydata.mydatatestbed.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 회원 관련 컨트롤러
 * <p>
 * 로그인/로그아웃 처리:
 * - Spring Security의 Form Login 방식 사용
 * - 로그인: SecurityConfig에서 설정한 /member/login으로 POST 요청 시 자동 처리
 * - 로그아웃: SecurityConfig에서 설정한 /member/logout으로 POST 요청 시 자동 처리
 * - 따라서 이 컨트롤러에는 로그인 "페이지"만 제공하고, 로그인 "처리"는 없음
 * <p>
 * 회원가입 처리:
 * - 4단계 프로세스: 약관동의 → 휴대폰인증 → 회원정보입력 → 이메일인증
 * - 각 단계별 세션 검증으로 단계 건너뛰기 방지
 */
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;

    // ==================== 로그인 ====================

    /**
     * 로그인 페이지
     *
     * @param error  로그인 실패 시 SecurityConfig에서 ?error=true 파라미터 전달
     * @param logout 로그아웃 성공 시 ?logout=true 파라미터 전달 (선택적)
     */
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "로그아웃되었습니다.");
        }

        // 브레드크럼 데이터 설정
        model.addAttribute("breadcrumbItems", createBreadcrumb("로그인", "/member/login"));

        return "member/login";
    }

    // ==================== 회원가입 Step 1: 약관동의 ====================

    /**
     * 회원가입 1단계 - 약관동의 페이지
     */
    @GetMapping("/signup/step1")
    public String signupStep1(HttpSession session, Model model) {
        // 새 회원가입 시작 시 이전 세션 데이터 초기화
        clearSignupSession(session);
        
        // 브레드크럼 데이터 설정
        model.addAttribute("breadcrumbItems", createSignupBreadcrumb());
        
        return "member/signup-step1-terms";
    }

    /**
     * 회원가입 1단계 - 약관동의 처리
     *
     * @param termsAgreed   이용약관 동의 여부
     * @param privacyAgreed 개인정보처리방침 동의 여부
     */
    @PostMapping("/signup/step1")
    public String signupStep1Process(
            @RequestParam(defaultValue = "false") boolean termsAgreed,
            @RequestParam(defaultValue = "false") boolean privacyAgreed,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!termsAgreed || !privacyAgreed) {
            redirectAttributes.addFlashAttribute("errorMessage", "필수 약관에 모두 동의해주세요.");
            return "redirect:/member/signup/step1";
        }

        // 세션에 약관동의 완료 표시
        session.setAttribute("signupStep1Complete", true);
        return "redirect:/member/signup/step2";
    }

    // ==================== 회원가입 Step 2: 휴대폰 인증 ====================

    /**
     * 회원가입 2단계 - 휴대폰 인증 페이지
     */
    @GetMapping("/signup/step2")
    public String signupStep2(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // 이전 단계 완료 확인
        if (session.getAttribute("signupStep1Complete") == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "약관동의를 먼저 완료해주세요.");
            return "redirect:/member/signup/step1";
        }
        
        // 브레드크럼 데이터 설정
        model.addAttribute("breadcrumbItems", createSignupBreadcrumb());
        
        return "member/signup-step2-phone";
    }

    /**
     * 회원가입 2단계 - 휴대폰 인증 처리
     * <p>
     * 참고: 실제 서비스에서는 SMS 인증 API 연동 필요
     * 여기서는 단순화하여 인증번호 확인 없이 통과 처리
     */
    @PostMapping("/signup/step2")
    public String signupStep2Process(
            @RequestParam String phone,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 이전 단계 완료 확인
        if (session.getAttribute("signupStep1Complete") == null) {
            return "redirect:/member/signup/step1";
        }

        // TODO: 실제 SMS 인증 로직 구현
        // 현재는 전화번호만 세션에 저장하고 통과
        session.setAttribute("signupPhone", phone);
        session.setAttribute("signupStep2Complete", true);
        return "redirect:/member/signup/step3";
    }

    // ==================== 회원가입 Step 3: 회원정보 입력 ====================

    /**
     * 회원가입 3단계 - 회원정보 입력 페이지
     */
    @GetMapping("/signup/step3")
    public String signupStep3(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // 이전 단계 완료 확인
        if (session.getAttribute("signupStep2Complete") == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "휴대폰 인증을 먼저 완료해주세요.");
            return "redirect:/member/signup/step2";
        }

        // 세션에서 전화번호 가져와서 DTO에 미리 설정
        String phone = (String) session.getAttribute("signupPhone");
        MemberSignupRequestDto dto = MemberSignupRequestDto.builder()
                .phone(phone)
                .build();
        model.addAttribute("signupRequest", dto);
        
        // 브레드크럼 데이터 설정
        model.addAttribute("breadcrumbItems", createSignupBreadcrumb());

        return "member/signup-step3-info";
    }

    /**
     * 회원가입 3단계 - 회원정보 입력 처리
     *
     * @Valid: DTO의 Bean Validation + @PasswordMatching 어노테이션 실행
     * BindingResult: 검증 실패 시 에러 정보 담김
     */
    @PostMapping("/signup/step3")
    public String signupStep3Process(
            @Valid @ModelAttribute("signupRequest") MemberSignupRequestDto requestDto,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        // 이전 단계 완료 확인
        if (session.getAttribute("signupStep2Complete") == null) {
            return "redirect:/member/signup/step2";
        }

        // 검증 실패 시 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            // 브레드크럼 데이터 설정 (검증 실패 시에도 필요)
            model.addAttribute("breadcrumbItems", createSignupBreadcrumb());
            return "member/signup-step3-info";
        }

        // 이메일 중복 확인
        if (memberService.isEmailDuplicate(requestDto.getEmail())) {
            bindingResult.rejectValue("email", "duplicate", "이미 사용 중인 이메일입니다.");
            // 브레드크럼 데이터 설정 (검증 실패 시에도 필요)
            model.addAttribute("breadcrumbItems", createSignupBreadcrumb());
            return "member/signup-step3-info";
        }

        // 회원정보를 세션에 저장 (최종 가입은 이메일 인증 후)
        session.setAttribute("signupRequest", requestDto);
        session.setAttribute("signupStep3Complete", true);

        // 회원 먼저 생성 (이메일 미인증 상태로)
        try {
            memberService.signup(requestDto);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("email", "duplicate", e.getMessage());
            model.addAttribute("breadcrumbItems", createSignupBreadcrumb());
            return "member/signup-step3-info";
        }

        // 이메일 인증 메일 발송
        LocalDateTime expiresAt = emailService.sendVerificationEmail(requestDto.getEmail());
        redirectAttributes.addFlashAttribute("expiresAt", expiresAt);

        return "redirect:/member/signup/step4";
    }

    // ==================== 회원가입 Step 4: 이메일 인증 ====================

    /**
     * 회원가입 4단계 - 이메일 인증 페이지
     */
    @GetMapping("/signup/step4")
    public String signupStep4(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // 이전 단계 완료 확인
        if (session.getAttribute("signupStep3Complete") == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "회원정보를 먼저 입력해주세요.");
            return "redirect:/member/signup/step3";
        }

        MemberSignupRequestDto requestDto = (MemberSignupRequestDto) session.getAttribute("signupRequest");
        model.addAttribute("email", requestDto.getEmail());
        
        // 브레드크럼 데이터 설정
        model.addAttribute("breadcrumbItems", createSignupBreadcrumb());

        return "member/signup-step4-email";
    }

    /**
     * 회원가입 4단계 - 이메일 인증 처리 (인증 완료 버튼 클릭 시)
     * <p>
     * 참고: 실제 서비스에서는 이메일 링크 클릭으로 인증 처리
     * 여기서는 단순화하여 버튼 클릭으로 가입 완료 처리
     */
    @PostMapping("/signup/step4")
    public String signupStep4Process(
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 이전 단계 완료 확인
        if (session.getAttribute("signupStep3Complete") == null) {
            return "redirect:/member/signup/step3";
        }

        MemberSignupRequestDto requestDto = (MemberSignupRequestDto) session.getAttribute("signupRequest");
        if (requestDto == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "세션이 만료되었습니다. 다시 시도해주세요.");
            return "redirect:/member/signup/step1";
        }

        // 회원가입 처리
        try {
            memberService.signup(requestDto);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/member/signup/step1";
        }

        // 세션 정리
        clearSignupSession(session);

        return "redirect:/member/signup/complete";
    }

    // ==================== 회원가입 완료 ====================

    /**
     * 회원가입 완료 페이지
     */
    @GetMapping("/signup/complete")
    public String signupComplete() {
        return "member/signup-complete";
    }

    // ==================== 이메일 인증 처리 ====================

    /**
     * 이메일 인증 링크 클릭 처리
     * 사용자가 메일의 인증 링크를 클릭하면 이 엔드포인트로 요청됨
     */
    @GetMapping("/verify-email")
    public String verifyEmail(
            @RequestParam String token,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // 토큰 검증
            String email = emailService.verifyToken(token);

            // 2. 회원 이메일 인증 처리
            memberService.verifyEmail(email);

            // 브레드크럼 설정
            model.addAttribute("breadcrumbItems", createBreadcrumb("이메일 인증", "/member/verify-email"));
            model.addAttribute("email", email);

            return "member/verify-email-success";

        } catch (IllegalArgumentException e) {
            model.addAttribute("breadcrumbItems", createBreadcrumb("이메일 인증", "/member/verify-email"));
            model.addAttribute("errorMessage", e.getMessage());
            return "member/verify-email-failed";
        }
    }

    /**
     * 인증 메일 재발송
     */
    @PostMapping("/signup/resend-email")
    public String resendVerificationEmail(
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        MemberSignupRequestDto requestDto = (MemberSignupRequestDto) session.getAttribute("signupRequest");
        if (requestDto == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "세션이 만료되었습니다.");
            return "redirect:/member/signup/step1";
        }

        try {
            LocalDateTime expiresAt = emailService.resendVerificationEmail(requestDto.getEmail());
            redirectAttributes.addFlashAttribute("successMessage", "인증 메일이 재발송되었습니다.");
            redirectAttributes.addFlashAttribute("expiresAt", expiresAt);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "메일 발송에 실패했습니다: " + e.getMessage());
        }

        return "redirect:/member/signup/step4";
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 회원가입 관련 세션 데이터 정리
     */
    private void clearSignupSession(HttpSession session) {
        session.removeAttribute("signupStep1Complete");
        session.removeAttribute("signupStep2Complete");
        session.removeAttribute("signupStep3Complete");
        session.removeAttribute("signupPhone");
        session.removeAttribute("signupRequest");
    }
    
    /**
     * 브레드크럼 데이터 생성 (로그인 등 일반 페이지용)
     * 
     * @param currentPageName 현재 페이지명
     * @param currentPageUrl 현재 페이지 URL
     * @return 브레드크럼 아이템 리스트
     */
    private List<Map<String, String>> createBreadcrumb(String currentPageName, String currentPageUrl) {
        return List.of(
                Map.of("name", "회원", "url", "#"),
                Map.of("name", currentPageName, "url", currentPageUrl)
        );
    }
    
    /**
     * 회원가입 페이지용 브레드크럼 데이터 생성
     * 
     * @return 브레드크럼 아이템 리스트
     */
    private List<Map<String, String>> createSignupBreadcrumb() {
        return List.of(
                Map.of("name", "회원", "url", "#"),
                Map.of("name", "회원가입", "url", "/member/signup/step1")
        );
    }
}

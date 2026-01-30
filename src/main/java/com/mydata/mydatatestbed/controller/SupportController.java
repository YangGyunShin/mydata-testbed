package com.mydata.mydatatestbed.controller;

import com.mydata.mydatatestbed.dto.board.BoardDetailResponseDto;
import com.mydata.mydatatestbed.dto.board.BoardListResponseDto;
import com.mydata.mydatatestbed.dto.board.BoardRequestDto;
import com.mydata.mydatatestbed.dto.inquiry.InquiryListResponseDto;
import com.mydata.mydatatestbed.dto.inquiry.InquiryRequestDto;
import com.mydata.mydatatestbed.dto.inquiry.InquiryResponseDto;
import com.mydata.mydatatestbed.dto.notice.NoticeDetailResponseDto;
import com.mydata.mydatatestbed.dto.notice.NoticeListResponseDto;
import com.mydata.mydatatestbed.dto.resource.ResourceDetailResponseDto;
import com.mydata.mydatatestbed.dto.resource.ResourceListResponseDto;
import com.mydata.mydatatestbed.dto.resource.ResourceNavDto;
import com.mydata.mydatatestbed.security.CustomUserDetails;
import com.mydata.mydatatestbed.service.*;
import com.mydata.mydatatestbed.dto.faq.FaqResponseDto;
import com.mydata.mydatatestbed.entity.Enum.FaqCategory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * 고객지원 관련 컨트롤러
 * <p>
 * 담당 기능:
 * - 공지사항 (Notice) - 구현 완료
 * - FAQ (추후 구현)
 * - 문의하기 (추후 구현)
 * - 자료실 (추후 구현)
 * - 자유게시판 (추후 구현)
 * <p>
 * URL 구조:
 * - /support/notice     : 공지사항 목록
 * - /support/notice/{id}: 공지사항 상세
 * - /support/faq        : FAQ
 * - /support/inquiry    : 문의하기
 * - /support/resource   : 자료실
 * - /support/board      : 자유게시판
 */
@Controller
@RequestMapping("/support")
@RequiredArgsConstructor
public class SupportController {

    private final NoticeService noticeService;
    private final FaqService faqService;
    private final InquiryService inquiryService;
    private final ResourceService resourceService;
    private final BoardService boardService;

    /**
     * 페이지당 게시글 수
     * - 목록 조회 시 한 페이지에 보여줄 게시글 개수
     * - 상수로 관리하여 변경 용이
     */
    private static final int DEFAULT_PAGE_SIZE = 10;

    // ==================== 공지사항 ====================

    /**
     * 공지사항 목록 페이지
     *
     * @param page    페이지 번호 (0부터 시작, 기본값 0)
     * @param keyword 검색 키워드 (제목/내용 검색, 기본값 빈 문자열)
     * @param model   뷰에 전달할 데이터
     * @return 공지사항 목록 뷰
     *
     * 페이징 처리:
     * - Spring Data JPA의 Pageable 사용
     * - PageRequest.of(page, size)로 Pageable 객체 생성
     * - 반환된 Page 객체에 전체 페이지 수, 총 게시글 수 등 포함
     *
     * 검색 처리:
     * - keyword가 비어있으면 전체 목록 조회
     * - keyword가 있으면 제목/내용에서 검색
     */
    @GetMapping("/notice")
    public String noticeList(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "") String keyword,
                             Model model) {

        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE);

        Page<NoticeListResponseDto> notices;
        if (keyword.isBlank()) {
            notices = noticeService.getNoticeList(pageable);
        } else {
            notices = noticeService.searchNotices(keyword, pageable);
        }

        model.addAttribute("notices", notices);
        model.addAttribute("keyword", keyword);  // 검색어 유지 (폼에 다시 표시)
        model.addAttribute("breadcrumbItems", createNoticeBreadcrumb("공지사항", "/support/notice"));
        model.addAttribute("sidebarMenus", createSupportSidebarMenus());
        model.addAttribute("currentMenu", "/support/notice");  // 사이드바 활성 메뉴 표시용

        return "support/notice-list";
    }

    /**
     * 공지사항 상세 페이지
     *
     * @param id    공지사항 ID (URL 경로 변수)
     * @param model 뷰에 전달할 데이터
     * @return 공지사항 상세 뷰
     *
     * @PathVariable: URL 경로의 {id} 부분을 파라미터로 매핑
     * - /support/notice/1 → id = 1
     * - /support/notice/100 → id = 100
     *
     * 조회수 처리:
     * - getNoticeDetailWithViewCount() 메서드가 조회와 동시에 조회수 증가
     * - @Transactional로 묶여 있어 조회수 증가가 DB에 반영됨
     */
    @GetMapping("/notice/{id}")
    public String noticeDetail(@PathVariable Long id,
                               Model model) {

        // 조회수 증가 포함 조회
        NoticeDetailResponseDto notice = noticeService.getNoticeDetailWithViewCount(id);

        model.addAttribute("notice", notice);
        model.addAttribute("breadcrumbItems", createNoticeBreadcrumb("공지사항 상세", "/support/notice/" + id));
        model.addAttribute("sidebarMenus", createSupportSidebarMenus());
        model.addAttribute("currentMenu", "/support/notice");

        return "support/notice-detail";
    }

    // ==================== FAQ ====================

    /**
     * FAQ 페이지
     *
     * @param category 카테고리 필터 (null이면 전체 조회)
     * @param model    뷰에 전달할 데이터
     * @return FAQ 뷰
     * <p>
     * 카테고리 필터링:
     * - category 파라미터가 없으면: 전체 FAQ 표시
     * - category 파라미터가 있으면: 해당 카테고리만 표시
     * <p>
     * 아코디언 UI:
     * - 질문 클릭 시 답변 토글 (JavaScript로 처리)
     */
    @GetMapping("/faq")
    public String faq(@RequestParam(required = false) FaqCategory category,
                      Model model) {

        List<FaqResponseDto> faqs;
        if (category != null) {
            faqs = faqService.getFaqsByCategory(category);
        } else {
            faqs = faqService.getAllFaqs();
        }

        model.addAttribute("faqs", faqs);
        model.addAttribute("categories", FaqCategory.values());  // 카테고리 탭 표시용
        model.addAttribute("selectedCategory", category);        // 현재 선택된 카테고리
        model.addAttribute("breadcrumbItems", createFaqBreadcrumb());
        model.addAttribute("sidebarMenus", createSupportSidebarMenus());
        model.addAttribute("currentMenu", "/support/faq");

        return "support/faq";
    }

    // ==================== 문의하기 ====================

    // 문의 작성 폼
    @GetMapping("/inquiry")
    public String inquiryForm(Model model) {
        model.addAttribute("inquiryRequest", new InquiryRequestDto());
        model.addAttribute("breadcrumbItems", createInquiryBreadcrumb("문의하기", "/support/inquiry"));
        model.addAttribute("sidebarMenus", createSupportSidebarMenus());
        model.addAttribute("currentMenu", "/support/inquiry");
        return "support/inquiry-form";
    }

    // 문의 등록
    @PostMapping("/inquiry")
    public String createInquiry(@Valid @ModelAttribute("inquiryRequest") InquiryRequestDto requestDto,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("breadcrumbItems", createInquiryBreadcrumb("문의하기", "/support/inquiry"));
            model.addAttribute("sidebarMenus", createSupportSidebarMenus());
            model.addAttribute("currentMenu", "/support/inquiry");
            return "support/inquiry-form";
        }

        inquiryService.createInquiry(userDetails.getMember(), requestDto);
        redirectAttributes.addFlashAttribute("message", "문의가 등록되었습니다.");
        return "redirect:/support/inquiry/list";
    }

    // 내 문의 목록
    @GetMapping("/inquiry/list")
    public String inquiryList(@RequestParam(defaultValue = "0") int page,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              Model model) {

        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE);
        Page<InquiryListResponseDto> inquiries = inquiryService.getMyInquiries(userDetails.getMember(), pageable);

        model.addAttribute("inquiries", inquiries);
        model.addAttribute("totalCount", inquiryService.countMyInquiries(userDetails.getMember()));
        model.addAttribute("breadcrumbItems", createInquiryBreadcrumb("내 문의 목록", "/support/inquiry/list"));
        model.addAttribute("sidebarMenus", createSupportSidebarMenus());
        model.addAttribute("currentMenu", "/support/inquiry");
        return "support/inquiry-list";
    }

    // 문의 상세
    @GetMapping("/inquiry/{id}")
    public String inquiryDetail(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                Model model) {

        InquiryResponseDto inquiry = inquiryService.getInquiryDetail(id, userDetails.getMember());
        model.addAttribute("inquiry", inquiry);
        model.addAttribute("breadcrumbItems", createInquiryBreadcrumb("문의 상세", "/support/inquiry/" + id));
        model.addAttribute("sidebarMenus", createSupportSidebarMenus());
        model.addAttribute("currentMenu", "/support/inquiry");
        return "support/inquiry-detail";
    }

    // ==================== 자료실 ====================

    /**
     * 자료실 목록 페이지
     */
    @GetMapping("/resource")
    public String resourceList(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "") String keyword,
                               Model model) {

        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE);

        Page<ResourceListResponseDto> resources;
        if (keyword.isBlank()) {
            resources = resourceService.getResourceList(pageable);
        } else {
            resources = resourceService.searchResources(keyword, pageable);
        }

        model.addAttribute("resources", resources);
        model.addAttribute("keyword", keyword);
        model.addAttribute("breadcrumbItems", createResourceBreadcrumb());
        model.addAttribute("sidebarMenus", createSupportSidebarMenus());
        model.addAttribute("currentMenu", "/support/resource");

        return "support/resource-list";
    }

    /**
     * 자료실 상세 페이지
     */
    @GetMapping("/resource/{id}")
    public String resourceDetail(@PathVariable Long id, Model model) {

        ResourceDetailResponseDto resource = resourceService.getResourceDetail(id);
        ResourceNavDto nextResource = resourceService.getNextResource(id);
        ResourceNavDto prevResource = resourceService.getPrevResource(id);

        model.addAttribute("resource", resource);
        model.addAttribute("nextResource", nextResource);
        model.addAttribute("prevResource", prevResource);
        model.addAttribute("breadcrumbItems", createResourceBreadcrumb());
        model.addAttribute("sidebarMenus", createSupportSidebarMenus());
        model.addAttribute("currentMenu", "/support/resource");

        return "support/resource-detail";
    }

    /**
     * 자료 다운로드
     */
    @GetMapping("/resource/{id}/download")
    public ResponseEntity<?> downloadResource(@PathVariable Long id) {
        try {
            var resource = resourceService.getResourceForDownload(id);

            Path filePath = Paths.get(resource.getFilePath());
            var fileResource = new UrlResource(filePath.toUri());

            if (fileResource.exists() && fileResource.isReadable()) {
                String encodedFileName = URLEncoder.encode(resource.getFileName(), StandardCharsets.UTF_8)
                        .replaceAll("\\+", "%20");

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                        .body(fileResource);
            } else {
                // 파일이 없는 경우 (테스트 데이터) - 상세 페이지로 리다이렉트
                return ResponseEntity.status(302)
                        .header(HttpHeaders.LOCATION, "/support/resource/" + id)
                        .build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(302)
                    .header(HttpHeaders.LOCATION, "/support/resource")
                    .build();
        }
    }

    // ==================== 자유게시판 ====================

    /**
     * 게시글 목록 조회
     */
    @GetMapping("/board")
    public String boardList(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "") String keyword,
                            @RequestParam(defaultValue = "all") String searchType,
                            Model model) {
        Page<BoardListResponseDto> boards = boardService.getBoardList(page, keyword, searchType);
        model.addAttribute("boards", boards);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("breadcrumbItems", createBoardBreadcrumb("자유게시판", "/support/board"));
        model.addAttribute("sidebarMenus", createSupportSidebarMenus());
        model.addAttribute("currentMenu", "/support/board");

        return "support/board-list";
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/board/{id}")
    public String boardDetail(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              Model model) {
        BoardDetailResponseDto board = boardService.getBoardDetail(id);

        model.addAttribute("board", board);

        if (userDetails != null) {
            model.addAttribute("isAuthor", board.getAuthorId().equals(userDetails.getMember().getId()));
            model.addAttribute("isAdmin", userDetails.getMember().isAdmin());
        } else {
            model.addAttribute("isAuthor", false);
            model.addAttribute("isAdmin", false);
        }

        model.addAttribute("breadcrumbItems", createBoardBreadcrumb("게시글 상세", "/support/board/" + id));
        model.addAttribute("sidebarMenus", createSupportSidebarMenus());
        model.addAttribute("currentMenu", "/support/board");

        return "support/board-detail";
    }

    /**
     * 게시글 작성 폼
     */
    @GetMapping("/board/write")
    public String boardWriteForm(Model model) {

        model.addAttribute("boardRequest", new BoardRequestDto());
        model.addAttribute("isEdit", false);
        model.addAttribute("breadcrumbItems", createBoardBreadcrumb("글쓰기", "/support/board/write"));
        model.addAttribute("sidebarMenus", createSupportSidebarMenus());
        model.addAttribute("currentMenu", "/support/board");

        return "support/board-write";
    }

    /**
     * 게시글 작성 처리
     */
    @PostMapping("/board/write")
    public String boardWrite(@Valid @ModelAttribute("boardRequest") BoardRequestDto requestDto,
                             BindingResult bindingResult,
                             @RequestParam(required = false) MultipartFile file,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("breadcrumbItems", createBoardBreadcrumb("글쓰기", "/support/board/write"));
            model.addAttribute("sidebarMenus", createSupportSidebarMenus());
            model.addAttribute("currentMenu", "/support/board");
            return "support/board-write";
        }

        Long boardId = boardService.createBoard(requestDto, userDetails.getMember(), file);
        redirectAttributes.addFlashAttribute("message", "게시글이 등록되었습니다.");
        return "redirect:/support/board/" + boardId;
    }

    /**
     * 게시글 수정 폼
     */
    @GetMapping("/board/{id}/edit")
    public String boardEditForm(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        BoardDetailResponseDto board = boardService.getBoardDetailWithoutViewCount(id);

        if (!board.getAuthorId().equals(userDetails.getMember().getId()) && !userDetails.getMember().isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "수정 권한이 없습니다.");
            return "redirect:/support/board/" + id;
        }

        model.addAttribute("board", board);
        model.addAttribute("boardRequest", BoardRequestDto.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .build());
        model.addAttribute("isEdit", true);
        model.addAttribute("breadcrumbItems", createBoardBreadcrumb("글수정", "/support/board/" + id + "/edit"));
        model.addAttribute("sidebarMenus", createSupportSidebarMenus());
        model.addAttribute("currentMenu", "/support/board");

        return "support/board-write";
    }

    /**
     * 게시글 수정 처리
     */
    @PostMapping("/board/{id}/edit")
    public String boardEdit(@PathVariable Long id,
                            @Valid @ModelAttribute("boardRequest") BoardRequestDto requestDto,
                            BindingResult bindingResult,
                            @RequestParam(required = false) MultipartFile file,
                            @RequestParam(defaultValue = "false") boolean deleteAttachment,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        if (bindingResult.hasErrors()) {
            BoardDetailResponseDto board = boardService.getBoardDetailWithoutViewCount(id);
            model.addAttribute("board", board);
            model.addAttribute("isEdit", true);
            model.addAttribute("breadcrumbItems", createBoardBreadcrumb("글수정", "/support/board/" + id + "/edit"));
            model.addAttribute("sidebarMenus", createSupportSidebarMenus());
            model.addAttribute("currentMenu", "/support/board");
            return "support/board-write";
        }

        boardService.updateBoard(id, requestDto, userDetails.getMember(), file, deleteAttachment);
        redirectAttributes.addFlashAttribute("message", "게시글이 수정되었습니다.");
        return "redirect:/support/board/" + id;
    }

    /**
     * 게시글 삭제 처리
     */
    @PostMapping("/board/{id}/delete")
    public String boardDelete(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              RedirectAttributes redirectAttributes) {

        boardService.deleteBoard(id, userDetails.getMember());
        redirectAttributes.addFlashAttribute("message", "게시글이 삭제되었습니다.");
        return "redirect:/support/board";
    }

    /**
     * 게시글 첨부파일 다운로드
     *
     * 흐름:
     * 1. 게시글 조회 (조회수 증가 없이)
     * 2. 첨부파일 존재 여부 확인 → 없으면 상세 페이지로 리다이렉트
     * 3. 파일 경로로 실제 파일 리소스 생성
     * 4. 파일 존재 및 읽기 가능 여부 확인
     *    - 가능하면 → 파일 다운로드 응답 반환
     *    - 불가능하면 → 상세 페이지로 리다이렉트
     * 5. 예외 발생 시 → 목록 페이지로 리다이렉트
     */
    @GetMapping("/board/{id}/download")
    public ResponseEntity<?> boardFileDownload(@PathVariable Long id) {
        try {
            // 1. 게시글 조회 (조회수 증가 없이 - 다운로드는 조회가 아니므로)
            BoardDetailResponseDto board = boardService.getBoardDetailWithoutViewCount(id);

            // 2. 첨부파일 존재 여부 확인
            //    - 첨부파일이 없으면 상세 페이지로 302 리다이렉트
            if (!board.isHasAttachment()) {
                return ResponseEntity.status(302)
                        .header(HttpHeaders.LOCATION, "/support/board/" + id)
                        .build();
            }

            // 3. DB에 저장된 파일 경로로 실제 파일 리소스 객체 생성
            //    - Paths.get(): 문자열 경로를 Path 객체로 변환
            //    - UrlResource: Spring의 Resource 구현체, 파일 접근용
            Path filePath = Paths.get(board.getAttachmentPath());
            var fileResource = new UrlResource(filePath.toUri());

            // 4. 파일이 실제로 존재하고 읽을 수 있는지 확인
            if (fileResource.exists() && fileResource.isReadable()) {
                // 4-A. 파일 다운로드 응답 생성

                // 파일명 URL 인코딩 (한글 파일명 깨짐 방지)
                // - URLEncoder.encode(): 특수문자/한글을 %XX 형식으로 변환
                // - replaceAll("\\+", "%20"): 공백이 +로 변환되는 것을 %20으로 교체
                String encodedFileName = URLEncoder.encode(board.getAttachmentName(), StandardCharsets.UTF_8)
                        .replaceAll("\\+", "%20");

                // Content-Disposition 헤더: 브라우저에게 파일 다운로드 방식 지시
                // - attachment: 다운로드로 처리 (inline이면 브라우저에서 열기 시도)
                // - filename*=UTF-8'': RFC 5987 표준, UTF-8 인코딩된 파일명 지정
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                        .body(fileResource);
            } else {
                // 4-B. 파일이 없거나 읽을 수 없음 (삭제됨, 권한 문제 등)
                //      → 상세 페이지로 리다이렉트
                return ResponseEntity.status(302)
                        .header(HttpHeaders.LOCATION, "/support/board/" + id)
                        .build();
            }
        } catch (Exception e) {
            // 5. 예외 발생 시 (잘못된 ID, DB 오류 등)
            //    → 목록 페이지로 리다이렉트
            return ResponseEntity.status(302)
                    .header(HttpHeaders.LOCATION, "/support/board")
                    .build();
        }
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 고객지원 사이드바 메뉴 생성
     *
     * @return 사이드바 메뉴 목록 (name: 메뉴명, url: 링크)
     *
     * 템플릿에서 사용:
     * - th:each로 반복하여 메뉴 렌더링
     * - currentMenu와 비교하여 활성 메뉴 스타일 적용
     */
    private List<Map<String, String>> createSupportSidebarMenus() {
        return List.of(
                Map.of("name", "공지사항", "url", "/support/notice"),
                Map.of("name", "FAQ", "url", "/support/faq"),
                Map.of("name", "문의하기", "url", "/support/inquiry"),
                Map.of("name", "자료실", "url", "/support/resource"),
                Map.of("name", "자유게시판", "url", "/support/board")
        );
    }

    /**
     * 공지사항 브레드크럼 생성
     *
     * @param currentPageName 현재 페이지명
     * @param currentPageUrl  현재 페이지 URL
     * @return 브레드크럼 아이템 목록
     *
     * 브레드크럼 구조: 홈 > 고객지원 > 현재 페이지
     */
    private List<Map<String, String>> createNoticeBreadcrumb(String currentPageName, String currentPageUrl) {
        return List.of(
                Map.of("name", "고객지원", "url", "#"),
                Map.of("name", currentPageName, "url", currentPageUrl)
        );
    }

    /**
     * FAQ 브레드크럼 생성
     */
    private List<Map<String, String>> createFaqBreadcrumb() {
        return List.of(
                Map.of("name", "고객지원", "url", "#"),
                Map.of("name", "FAQ", "url", "/support/faq")
        );
    }

    /**
     * 문의하기 브레드크럼 생성
     */
    private List<Map<String, String>> createInquiryBreadcrumb(String currentPageName, String currentPageUrl) {
        return List.of(
                Map.of("name", "고객지원", "url", "#"),
                Map.of("name", currentPageName, "url", currentPageUrl)
        );
    }

    /**
     * 자료실 브레드크럼 생성
     */
    private List<Map<String, String>> createResourceBreadcrumb() {
        return List.of(
                Map.of("name", "고객지원", "url", "#"),
                Map.of("name", "자료실", "url", "/support/resource")
        );
    }

    /**
     * 자유게시판 브레드크럼 생성
     */
    private List<Map<String, String>> createBoardBreadcrumb(String currentPageName, String currentPageUrl) {
        return List.of(
                Map.of("name", "고객지원", "url", "#"),
                Map.of("name", currentPageName, "url", currentPageUrl)
        );
    }
}
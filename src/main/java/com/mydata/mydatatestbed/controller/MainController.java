package com.mydata.mydatatestbed.controller;

import com.mydata.mydatatestbed.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final NoticeService noticeService;

    @GetMapping("/")
    public String main(Model model) {
        // 최신 공지사항 3개 조회
        model.addAttribute("notices", noticeService.getRecentNotices(3));
        return "main/index";
    }
}

package com.mydata.mydatatestbed.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api-guide")
public class ApiGuideController {

    /**
     * 데이터 표준 API 기본규격
     */
    @GetMapping
    public String basicSpec(Model model) {
        model.addAttribute("sidebarMenus", getSidebarMenus());
        model.addAttribute("currentMenu", "/api-guide");
        model.addAttribute("breadcrumbItems", getBreadcrumbItems("데이터 표준 API 기본규격"));
        return "api-guide/basic-spec";
    }

    // API가이드 사이드바 메뉴 (하위 그룹 포함)
    private List<Map<String, String>> getSidebarMenus() {
        return List.of(
                Map.of("name", "데이터 표준 API 기본규격", "url", "/api-guide"),
                Map.of("name", "데이터 표준 API 인증규격", "url", "/api-guide/auth"),
                Map.of("name", "마이데이터 참여자별 API 처리 절차", "url", "/api-guide/process"),
                Map.of("name", "마이데이터 인증 API 규격", "url", "/api-guide/auth-api"),
                Map.of("name", "마이데이터 지원 API 규격", "url", "/api-guide/support-api"),
                Map.of("name", "마이데이터 정보제공 API 규격", "url", "/api-guide/info-api")
        );
    }

    // 브레드크럼 생성
    private List<Map<String, String>> getBreadcrumbItems(String current) {
        return List.of(
                Map.of("name", "홈", "url", "/"),
                Map.of("name", "API가이드", "url", "/api-guide"),
                Map.of("name", current, "url", "")
        );
    }
}
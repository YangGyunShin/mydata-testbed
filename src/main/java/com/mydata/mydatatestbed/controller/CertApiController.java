package com.mydata.mydatatestbed.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cert-api")
public class CertApiController {

    @GetMapping
    public String certApiHome() {
        return "redirect:/cert-api/individual";
    }

    @GetMapping("/individual")
    public String individualApi(Model model) {
        model.addAttribute("activeGroup", "cert");
        model.addAttribute("currentMenu", "/cert-api/individual");
        model.addAttribute("breadcrumbItems", getBreadcrumbItems("개별인증 API"));
        return "cert-api/individual-api";
    }

    @GetMapping("/integrated")
    public String integratedApi(Model model) {
        model.addAttribute("activeGroup", "cert");
        model.addAttribute("currentMenu", "/cert-api/integrated");
        model.addAttribute("breadcrumbItems", getBreadcrumbItems("통합인증 API"));
        return "cert-api/integrated-api";
    }

    private List<Map<String, String>> getBreadcrumbItems(String currentPage) {
        return List.of(
                Map.of("name", "홈", "url", "/"),
                Map.of("name", "API가이드", "url", "/api-guide"),
                Map.of("name", "마이데이터 인증 API 규격", "url", "/cert-api"),
                Map.of("name", currentPage, "url", "")
        );
    }
}
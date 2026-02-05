package com.mydata.mydatatestbed.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * 마이데이터 정보제공 API 규격 컨트롤러
 * - activeGroup: "info" (아코디언 사이드바에서 정보제공 API 그룹 활성화)
 * - 업권별 하위 페이지: bank, card, insurance, invest, efin, installment,
 * guarantee, telecom, p2p, bond, loan, kinfa
 */
@Controller
@RequestMapping("/info-api")
public class InfoApiController {

    // 기본 접속 시 은행 업권으로 리다이렉트
    @GetMapping
    public String infoApiHome() {
        return "redirect:/info-api/bank";
    }

    // ===== 은행 업권 =====
    @GetMapping("/bank")
    public String bankApi(Model model) {
        model.addAttribute("activeGroup", "info");
        model.addAttribute("currentMenu", "/info-api/bank");
        model.addAttribute("breadcrumbItems", getBreadcrumbItems("은행 업권 정보제공 API 규격"));
        return "info-api/bank-api";
    }

    // ===== 카드 업권 (추후 구현) =====
    // @GetMapping("/card")
    // public String cardApi(Model model) {
    //     model.addAttribute("activeGroup", "info");
    //     model.addAttribute("currentMenu", "/info-api/card");
    //     model.addAttribute("breadcrumbItems", getBreadcrumbItems("카드 업권 정보제공 API 규격"));
    //     return "info-api/card-api";
    // }

    // ===== 보험 업권 (추후 구현) =====
    // @GetMapping("/insurance")

    // ===== 금융투자 업권 (추후 구현) =====
    // @GetMapping("/invest")

    // ===== 전자금융 업권 (추후 구현) =====
    // @GetMapping("/efin")

    // ===== 할부금융 업권 (추후 구현) =====
    // @GetMapping("/installment")

    // ===== 보증보험 업권 (추후 구현) =====
    // @GetMapping("/guarantee")

    // ===== 통신 업권 (추후 구현) =====
    // @GetMapping("/telecom")

    // ===== P2P 업권 (추후 구현) =====
    // @GetMapping("/p2p")

    // ===== 인수채권 업권 (추후 구현) =====
    // @GetMapping("/bond")

    // ===== 대부 업권 (추후 구현) =====
    // @GetMapping("/loan")

    // ===== 서민금융진흥원 (추후 구현) =====
    // @GetMapping("/kinfa")

    private List<Map<String, String>> getBreadcrumbItems(String currentPage) {
        return List.of(
                Map.of("name", "홈", "url", "/"),
                Map.of("name", "API가이드", "url", "/api-guide"),
                Map.of("name", "마이데이터 정보제공 API 규격", "url", "/info-api"),
                Map.of("name", currentPage, "url", "")
        );
    }
}
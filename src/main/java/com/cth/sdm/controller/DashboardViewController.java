package com.cth.sdm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cth.sdm.dto.DashboardStatsDto;
import com.cth.sdm.service.ApplicantProfileService;
import com.cth.sdm.service.BotConfigurationService;
import com.cth.sdm.service.DashboardService;
import com.cth.sdm.workflow.LinkedInBotService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DashboardViewController {

    private final DashboardService dashboardService;
    private final LinkedInBotService botService;
    private final BotConfigurationService configurationService;
    private final ApplicantProfileService applicantProfileService;

    @GetMapping("/")
    public String index(Model model) {
        var botStatus = botService.getStatus();
        DashboardStatsDto stats = dashboardService.getDashboardStats(botStatus.isRunning(), botStatus.getCurrentKeyword());
        model.addAttribute("stats", stats);
        model.addAttribute("botStatus", botStatus);
        model.addAttribute("maxJobAgeDays", configurationService.getMaxJobAgeDays());
        model.addAttribute("country", configurationService.getCountry());
        model.addAttribute("company", configurationService.getCompany());
        model.addAttribute("easyApplyOnly", configurationService.isEasyApplyOnly());
        model.addAttribute("searchKeywords", configurationService.getSearchKeywords());
        return "index";
    }

    @GetMapping("/config")
    public String configuration(Model model) {
        model.addAttribute("applicantProfile", applicantProfileService.getProfile());
        return "config";
    }
}

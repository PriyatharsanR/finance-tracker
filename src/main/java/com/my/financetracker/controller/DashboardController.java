package com.my.financetracker.controller;

import com.my.financetracker.models.responses.DashboardResponse;
import com.my.financetracker.models.responses.DefaultResponse;
import com.my.financetracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DefaultResponse<DashboardResponse> getDashboard() {
        return dashboardService.getDashboardSummary();
    }
}

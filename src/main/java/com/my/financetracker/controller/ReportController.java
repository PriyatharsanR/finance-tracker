package com.my.financetracker.controller;

import com.my.financetracker.models.responses.DefaultResponse;
import com.my.financetracker.models.responses.MonthlyReportResponse;
import com.my.financetracker.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/yearly")
    public DefaultResponse<List<MonthlyReportResponse>> getYearlyReport(
            @RequestParam int year
    ) {
        return reportService.getYearlyReport(year);
    }
}
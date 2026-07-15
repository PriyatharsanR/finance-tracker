package com.my.financetracker.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyReportResponse {

    private int monthNumber;

    private String monthName;

    private BigDecimal totalIncome;

    private BigDecimal totalExpense;

    private BigDecimal netIncome;
}

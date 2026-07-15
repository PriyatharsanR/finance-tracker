package com.my.financetracker.service;

import com.my.financetracker.entity.Transaction;
import com.my.financetracker.entity.User;
import com.my.financetracker.enums.TransactionType;
import com.my.financetracker.exception.UnAuthorizedException;
import com.my.financetracker.models.responses.DefaultResponse;
import com.my.financetracker.models.responses.MonthlyReportResponse;
import com.my.financetracker.repository.TransactionRepository;
import com.my.financetracker.repository.UserRepository;
import com.my.financetracker.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public DefaultResponse<List<MonthlyReportResponse>> getYearlyReport(int year) {
        log.info("Generate yearly report. year={}", year);

        try {
            if (year < 2025 || year > LocalDate.now().getYear() + 1) {
                return DefaultResponse.<List<MonthlyReportResponse>>builder()
                        .code(ResponseUtil.FAILED_CODE)
                        .title(ResponseUtil.FAILED)
                        .message("Invalid year.")
                        .data(null)
                        .build();
            }

            User user = getCurrentUser();

            LocalDate startDate = LocalDate.of(year, 1, 1);
            LocalDate endDate = LocalDate.of(year, 12, 31);

            List<Transaction> yearlyTransactions = transactionRepository.findByUserIdAndTransactionDateBetween(
                            user.getId(),
                            startDate,
                            endDate
                    );

            List<MonthlyReportResponse> report = new ArrayList<>();

            for (int monthNumber = 1; monthNumber <= 12; monthNumber++) {
                int selectedMonth = monthNumber;

                List<Transaction> monthlyTransaction = yearlyTransactions.stream()
                        .filter(transaction -> transaction.getTransactionDate().getMonthValue() == selectedMonth)
                        .toList();

                BigDecimal totalIncome = calculateTotalByType(monthlyTransaction, TransactionType.INCOME);

                BigDecimal totalExpense = calculateTotalByType(monthlyTransaction, TransactionType.EXPENSE);

                MonthlyReportResponse monthlyReport = MonthlyReportResponse.builder()
                        .monthNumber(monthNumber)
                        .monthName(Month.of(monthNumber).name())
                        .totalIncome(totalIncome)
                        .totalExpense(totalExpense)
                        .netIncome(totalIncome.subtract(totalExpense))
                        .build();

                report.add(monthlyReport);
            }


            log.info("Yearly financial report generated. userId: {}, year: {}, transactionCount: {}", user.getId(), year, yearlyTransactions.size());

            return DefaultResponse.<List<MonthlyReportResponse>>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message("Yearly financial report fetched successfully")
                    .data(report)
                    .build();

        } catch (DateTimeException e) {
            log.warn("Invalid report year: {}", year, e);

            return DefaultResponse.<List<MonthlyReportResponse>>builder()
                    .code(ResponseUtil.FAILED_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Invalid report year")
                    .data(null)
                    .build();

        } catch (Exception e) {
            log.error("Error while generating yearly financial report. year: {}", year, e);

            return DefaultResponse.<List<MonthlyReportResponse>>builder()
                    .code(ResponseUtil.INTERNAL_ERROR_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Unable to generate yearly financial report")
                    .data(null)
                    .build();
        }
    }

    private BigDecimal calculateTotalByType(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(transaction -> transaction.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() ->
                        new UnAuthorizedException("Logged-in user not found"));
    }
}

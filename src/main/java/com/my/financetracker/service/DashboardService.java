package com.my.financetracker.service;

import com.my.financetracker.entity.Transaction;
import com.my.financetracker.entity.User;
import com.my.financetracker.enums.TransactionType;
import com.my.financetracker.models.responses.DashboardResponse;
import com.my.financetracker.models.responses.DefaultResponse;
import com.my.financetracker.repository.TransactionRepository;
import com.my.financetracker.repository.UserRepository;
import com.my.financetracker.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public DefaultResponse<DashboardResponse> getDashboardSummary() {

        log.info("getDashboardSummary for this user.");

        try {
            User user = getCurrentUser();

            List<Transaction> transactions = transactionRepository.findByUserId(user.getId());

            BigDecimal totalIncome = calculateTotalByType(transactions, TransactionType.INCOME);

            BigDecimal totalExpense = calculateTotalByType(transactions, TransactionType.EXPENSE);

            BigDecimal currentBalance = totalIncome.subtract(totalExpense);

            LocalDate currentDate = LocalDate.now();
            int currentYear = currentDate.getYear();
            int currentMonth = currentDate.getMonthValue();

            List<Transaction> currentMonthTransactions = transactions.stream()
                    .filter(transaction -> transaction.getTransactionDate().getYear() == currentYear
                    && transaction.getTransactionDate().getMonthValue() == currentMonth )
                    .toList();

            BigDecimal currentMonthIncome = calculateTotalByType(currentMonthTransactions, TransactionType.INCOME);
            BigDecimal currentMonthExpense = calculateTotalByType(currentMonthTransactions, TransactionType.EXPENSE);

            DashboardResponse response = DashboardResponse.builder()
                    .currentUser(user.getName())
                    .totalIncome(totalIncome)
                    .totalExpenses(totalExpense)
                    .currentBalance(currentBalance)
                    .currentMonthIncome(currentMonthIncome)
                    .currentMonthExpenses(currentMonthExpense)
                    .build();

            log.info("getDashboardSummary for this user: {}, total income: {}, total expense: {}", user.getName() , totalIncome, totalExpense);

            return DefaultResponse.<DashboardResponse>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message("Dashboard summary fetch successfully.")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("Error during getDashboardSummary for this user.", e);

            return DefaultResponse.<DashboardResponse>builder()
                    .code(ResponseUtil.INTERNAL_ERROR_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Unable to fetch dashboard summary")
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

        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
    }
}

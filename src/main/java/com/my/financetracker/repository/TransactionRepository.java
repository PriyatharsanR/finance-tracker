package com.my.financetracker.repository;

import com.my.financetracker.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction>findByUserId(Long userId);
    List<Transaction> findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(Long userId, LocalDate startDate, LocalDate endDate);

    List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
}

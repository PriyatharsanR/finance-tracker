package com.my.financetracker.models.responses;

import com.my.financetracker.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;
    private LocalDate transactionDate;
    private Long categoryId;
    private String categoryName;
    private TransactionType type;
    private BigDecimal amount;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

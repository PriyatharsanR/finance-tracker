package com.my.financetracker.service;

import com.my.financetracker.entity.Category;
import com.my.financetracker.entity.Transaction;
import com.my.financetracker.models.requests.TransactionRequest;
import com.my.financetracker.models.responses.DefaultResponse;
import com.my.financetracker.models.responses.TransactionResponse;
import com.my.financetracker.repository.CategoryRepository;
import com.my.financetracker.repository.TransactionRepository;
import com.my.financetracker.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public DefaultResponse<TransactionResponse> createTransaction(@Valid TransactionRequest transactionRequest) {
        log.info("Creating transaction with amount: {}, type: {}, category: {}",
                transactionRequest.getAmount(),
                transactionRequest.getType(),
                transactionRequest.getCategoryId());
        try {
            Category category = categoryRepository.findById(transactionRequest.getCategoryId())
                        .orElse(null);

            if (category == null) {
                return DefaultResponse.<TransactionResponse>builder()
                        .code(ResponseUtil.NOT_FOUND_CODE)
                        .title(ResponseUtil.FAILED)
                        .message("Category not found.")
                        .build();
            }

            Transaction transaction = new Transaction();
            transaction.setTransactionDate(transactionRequest.getTransactionDate());
            transaction.setCategory(category);
            transaction.setType(transactionRequest.getType());
            transaction.setAmount(transactionRequest.getAmount());
            transaction.setNote(transactionRequest.getNote());
            transaction.setCreatedAt(LocalDateTime.now());
            transaction.setUpdatedAt(LocalDateTime.now());

            Transaction savedTransaction = transactionRepository.save(transaction);
            log.info("Transaction Created: {}", transaction);

            TransactionResponse response = mapToTransactionResponse(savedTransaction);

            return DefaultResponse.<TransactionResponse>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message("Transaction created Successfully.")
                    .data(response)
                    .build();

        }catch (Exception e){
            log.error("Error while creating transaction: {}", e.getMessage());

            return DefaultResponse.<TransactionResponse>builder()
                    .code(ResponseUtil.INTERNAL_ERROR_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Unable to create transaction.")
                    .build();
        }
    }



    public DefaultResponse<TransactionResponse> updateTransaction(Long id, @Valid TransactionRequest transactionRequest) {
        log.info("Updating transaction: {}", transactionRequest);

        try {

            Transaction transaction = transactionRepository.findById(id)
                    .orElse(null);

            if (transaction == null) {
                log.error("Transaction Not Found: {}", id);
                return DefaultResponse.<TransactionResponse>builder()
                        .code(ResponseUtil.NOT_FOUND_CODE)
                        .title(ResponseUtil.FAILED)
                        .message("Transaction not found")
                        .build();
            }

            Category category = categoryRepository.findById(transactionRequest.getCategoryId())
                    .orElse(null);

            if (category == null) {
                log.error("Category not found: {}", transactionRequest.getCategoryId());
                return DefaultResponse.<TransactionResponse>builder()
                        .code(ResponseUtil.NOT_FOUND_CODE)
                        .title(ResponseUtil.FAILED)
                        .message("Category not found")
                        .build();
            }


            transaction.setCategory(category);
            transaction.setTransactionDate(transactionRequest.getTransactionDate());
            transaction.setType(transactionRequest.getType());
            transaction.setAmount(transactionRequest.getAmount());
            transaction.setNote(transactionRequest.getNote());
            transaction.setUpdatedAt(LocalDateTime.now());

            Transaction updatedTransaction = transactionRepository.save(transaction);
            log.info("Transaction Updated successfully id: {}", updatedTransaction.getId());

            TransactionResponse response = mapToTransactionResponse(updatedTransaction);

            return DefaultResponse.<TransactionResponse>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message("Transaction updated successfully.")
                    .data(response)
                    .build();

        }catch (Exception e){
            log.error("Error while updating transaction: {}", e.getMessage());
            return DefaultResponse.<TransactionResponse>builder()
                    .code(ResponseUtil.FAILED_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Unable to update transaction.")
                    .data(null)
                    .build();
        }

    }


    public DefaultResponse<TransactionResponse> deleteTransaction(Long id) {

        log.info("Deleting transaction with id: {}", id);

        try {

            Transaction transaction = transactionRepository.findById(id)
                    .orElse(null);

            if (transaction == null) {
                return DefaultResponse.<TransactionResponse>builder()
                        .code(ResponseUtil.NOT_FOUND_CODE)
                        .title(ResponseUtil.FAILED)
                        .message("Transaction not found with id: " + id)
                        .data(null)
                        .build();
            }

            transactionRepository.delete(transaction);

            log.info("Transaction deleted successfully. id: {}", id);

            return DefaultResponse.<TransactionResponse>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message("Transaction deleted successfully")
                    .data(null)
                    .build();

        } catch (Exception e) {

            log.error("Error while deleting transaction with id: {}", id, e);

            return DefaultResponse.<TransactionResponse>builder()
                    .code(ResponseUtil.INTERNAL_ERROR_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Something went wrong while deleting transaction")
                    .data(null)
                    .build();
        }
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionDate(transaction.getTransactionDate())
                .type(transaction.getType())
                .categoryId(transaction.getCategory().getId())
                .categoryName(transaction.getCategory().getName())
                .amount(transaction.getAmount())
                .note(transaction.getNote())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }

    public DefaultResponse<List<TransactionResponse>> getAllTransactions() {

        log.info("Fetching all transactions");

        try {

            List<TransactionResponse> transactionResponses = transactionRepository.findAll()
                    .stream()
                    .map(this::mapToTransactionResponse)
                    .toList();

            log.info("Found {} transactions", transactionResponses.size());

            return DefaultResponse.<List<TransactionResponse>>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message("Transactions fetched successfully")
                    .data(transactionResponses)
                    .build();

        } catch (Exception e) {

            log.error("Error while fetching transactions", e);

            return DefaultResponse.<List<TransactionResponse>>builder()
                    .code(ResponseUtil.INTERNAL_ERROR_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Unable to fetch transactions")
                    .data(null)
                    .build();
        }
    }


    public DefaultResponse<List<TransactionResponse>> getTransactionsByMonth(int year, int month) {
        log.info("Fetching transactions by month {}", month);

        try {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            List<TransactionResponse> response = transactionRepository.findByTransactionDateBetween(startDate, endDate)
                    .stream()
                    .map(this::mapToTransactionResponse)
                    .toList();

            if (response.isEmpty()) {
                log.info("No transactions found for year {} and month {}", year, month);
                return DefaultResponse.<List<TransactionResponse>>builder()
                        .code(ResponseUtil.SUCCESS_CODE)
                        .title(ResponseUtil.SUCCESS)
                        .message("No transaction happen for this month")
                        .data(null)
                        .build();
            }

            log.info("Found {} transactions by month {}", response.size(), month);
            return DefaultResponse.<List<TransactionResponse>>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message("Transactions fetched successfully")
                    .data(response)
                    .build();

        }catch (Exception e){
            log.error("Error while fetching transactions by month {}", month, e);
            return DefaultResponse.<List<TransactionResponse>>builder()
                    .code(ResponseUtil.INTERNAL_ERROR_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Unable to fetch transactions")
                    .data(null)
                    .build();

        }
    }
}

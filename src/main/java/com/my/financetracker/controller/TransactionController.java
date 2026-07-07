package com.my.financetracker.controller;

import com.my.financetracker.models.requests.TransactionRequest;
import com.my.financetracker.models.responses.DefaultResponse;
import com.my.financetracker.models.responses.TransactionResponse;
import com.my.financetracker.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DefaultResponse<TransactionResponse> createTransaction(@RequestBody @Valid TransactionRequest transactionRequest) {
        return transactionService.createTransaction(transactionRequest);
    }

    @PutMapping("/{id}")
    public DefaultResponse<TransactionResponse> updateTransaction(@PathVariable Long id, @RequestBody @Valid TransactionRequest transactionRequest) {
        return transactionService.updateTransaction(id, transactionRequest);
    }


    @DeleteMapping("/{id}")
    public DefaultResponse<TransactionResponse> deleteTransaction(@PathVariable Long id) {
        return transactionService.deleteTransaction(id);
    }
}

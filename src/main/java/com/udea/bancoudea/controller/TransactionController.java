package com.udea.bancoudea.controller;

import com.udea.bancoudea.DTO.TransactionDTO;
import com.udea.bancoudea.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:5173")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferMoney(
            @RequestBody TransactionDTO transactionDTO
    ) {
        try {
            TransactionDTO result =
                    transactionService.transferMoney(transactionDTO);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsForAccount(
            @PathVariable String accountNumber
    ) {
        List<TransactionDTO> transactions =
                transactionService.getTransactionsForAccount(accountNumber);

        return ResponseEntity.ok(transactions);
    }
}

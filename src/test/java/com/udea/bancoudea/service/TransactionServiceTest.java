package com.udea.bancoudea.service;

import com.udea.bancoudea.DTO.TransactionDTO;
import com.udea.bancoudea.entity.Customer;
import com.udea.bancoudea.entity.Transaction;
import com.udea.bancoudea.repository.CustomerRepository;
import com.udea.bancoudea.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private TransactionService transactionService;

    private TransactionDTO request(String from, String to, Double amount) {
        TransactionDTO dto = new TransactionDTO();
        dto.setSenderAccountNumber(from);
        dto.setReceiverAccountNumber(to);
        dto.setAmount(amount);
        return dto;
    }

    @Test
    void transferMoneyOk() {
        Customer sender = new Customer(1L, "A1", "Ana", "Perez", 100.0);
        Customer receiver = new Customer(2L, "A2", "Luis", "Gomez", 50.0);
        when(customerRepository.findByAccountNumber("A1")).thenReturn(Optional.of(sender));
        when(customerRepository.findByAccountNumber("A2")).thenReturn(Optional.of(receiver));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        TransactionDTO result = transactionService.transferMoney(request("A1", "A2", 30.0));

        assertEquals(10L, result.getId());
        assertEquals(30.0, result.getAmount());
        assertEquals(70.0, sender.getBalance());
        assertEquals(80.0, receiver.getBalance());
    }

    @Test
    void transferMoneyNullAccounts() {
        TransactionDTO body = request(null, "A2", 10.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transferMoney(body));
        assertTrue(ex.getMessage().contains("cannot be null"));
    }

    @Test
    void transferMoneySenderNotFound() {
        when(customerRepository.findByAccountNumber("A1")).thenReturn(Optional.empty());
        TransactionDTO body = request("A1", "A2", 10.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transferMoney(body));
        assertEquals("Sender Account Number not found", ex.getMessage());
    }

    @Test
    void transferMoneyReceiverNotFound() {
        when(customerRepository.findByAccountNumber("A1"))
                .thenReturn(Optional.of(new Customer(1L, "A1", "Ana", "Perez", 100.0)));
        when(customerRepository.findByAccountNumber("A2")).thenReturn(Optional.empty());
        TransactionDTO body = request("A1", "A2", 10.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transferMoney(body));
        assertEquals("Receiver Account Number not found", ex.getMessage());
    }

    @Test
    void transferMoneyInsufficientBalance() {
        when(customerRepository.findByAccountNumber("A1"))
                .thenReturn(Optional.of(new Customer(1L, "A1", "Ana", "Perez", 5.0)));
        when(customerRepository.findByAccountNumber("A2"))
                .thenReturn(Optional.of(new Customer(2L, "A2", "Luis", "Gomez", 50.0)));
        TransactionDTO body = request("A1", "A2", 100.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transferMoney(body));
        assertEquals("Sender Balance not enough", ex.getMessage());
    }

    @Test
    void getTransactionsForAccountMapsList() {
        Transaction t = new Transaction(1L, "A1", "A2", 25.0, LocalDateTime.now());
        when(transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber("A1", "A1"))
                .thenReturn(List.of(t));

        List<TransactionDTO> result = transactionService.getTransactionsForAccount("A1");

        assertEquals(1, result.size());
        assertEquals("A2", result.get(0).getReceiverAccountNumber());
        assertEquals(25.0, result.get(0).getAmount());
    }
}

package com.udea.bancoudea.repository;

import com.udea.bancoudea.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySenderAccountNumberOrReceiverAccountNumber(
            String senderAccountNumber,
            String receiverAccountNumber
    );
}

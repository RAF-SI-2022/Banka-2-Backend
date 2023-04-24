package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.exceptions.TransactionNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.Transaction;
import com.raf.si.Banka2Backend.models.mariadb.TransactionStatus;
import com.raf.si.Banka2Backend.repositories.mariadb.TransactionRepository;
import com.raf.si.Banka2Backend.services.interfaces.TransactionServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService implements TransactionServiceInterface {
    private final TransactionRepository transactionRepository;
    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    @Override
    public Transaction create(Transaction transaction) {
        return this.transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> findAllByUserEmailAndCurrencyCode(String userEmail, String currencyCode) {
        return this.transactionRepository.findAllByUserEmailAndCurrencyCurrencyCode(userEmail, currencyCode);
    }

    @Override
    public List<Transaction> findAllByOrderId(Long orderId) {
        return this.transactionRepository.findAllByOrderId(orderId);
    }

    @Override
    public Transaction changeTransactionStatus(Long transactionId, TransactionStatus status) {
        Optional<Transaction> transactionOptional = this.transactionRepository.findById(transactionId);
        if(transactionOptional.isEmpty()) {
            throw new TransactionNotFoundException(transactionId);
        }
        Transaction transaction = transactionOptional.get();
        transaction.setStatus(status);
        return this.transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> createAll(List<Transaction> transactionList) {
        return this.transactionRepository.saveAll(transactionList);
    }


}

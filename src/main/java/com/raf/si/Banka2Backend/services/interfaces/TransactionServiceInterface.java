package com.raf.si.Banka2Backend.services.interfaces;
import com.raf.si.Banka2Backend.models.mariadb.Transaction;
import com.raf.si.Banka2Backend.models.mariadb.TransactionStatus;

import java.util.List;

public interface TransactionServiceInterface {
    Transaction create(Transaction transaction);
    List<Transaction> findAllByOrderId(Long orderId);
    List<Transaction> findAllByUserEmailAndCurrencyCode(String userEmail, String currencyCode);
    Transaction changeTransactionStatus(Long transactionId, TransactionStatus status);
}

package com.raf.si.Banka2Backend.services.interfaces;

import com.raf.si.Banka2Backend.models.mariadb.Balance;
import com.raf.si.Banka2Backend.models.mariadb.Transaction;
import com.raf.si.Banka2Backend.models.mariadb.TransactionStatus;
import com.raf.si.Banka2Backend.models.mariadb.orders.Order;
import java.util.List;

public interface TransactionServiceInterface {
    Transaction save(Transaction transaction);

    List<Transaction> findAllByOrderId(Long orderId);

    List<Transaction> findAllByUserEmailAndCurrencyCode(String userEmail, String currencyCode);

    Transaction changeTransactionStatus(Long transactionId, TransactionStatus status);

    List<Transaction> saveAll(List<Transaction> transactionList);

    Transaction createTransaction(Order order, Balance balance, Float amount);
}

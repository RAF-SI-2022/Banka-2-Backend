package rs.edu.raf.si.bank2.securities.services.interfaces;

import java.util.List;
import rs.edu.raf.si.bank2.securities.models.mariadb.Balance;
import rs.edu.raf.si.bank2.securities.models.mariadb.Transaction;
import rs.edu.raf.si.bank2.securities.models.mariadb.TransactionStatus;
import rs.edu.raf.si.bank2.securities.models.mariadb.orders.Order;

public interface TransactionServiceInterface {
    Transaction save(Transaction transaction);

    List<Transaction> findAllByOrderId(Long orderId);

    List<Transaction> findAllByUserEmailAndCurrencyCode(String userEmail, String currencyCode);

    Transaction changeTransactionStatus(Long transactionId, TransactionStatus status);

    List<Transaction> saveAll(List<Transaction> transactionList);

    Transaction createTransaction(Order order, Balance balance, Float amount);
}

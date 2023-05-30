package rs.edu.raf.si.bank2.otc.services.interfaces;

import java.util.List;
import rs.edu.raf.si.bank2.otc.models.mariadb.Balance;
import rs.edu.raf.si.bank2.otc.models.mariadb.Transaction;
import rs.edu.raf.si.bank2.otc.models.mariadb.TransactionStatus;
import rs.edu.raf.si.bank2.otc.models.mariadb.orders.FutureOrder;
import rs.edu.raf.si.bank2.otc.models.mariadb.orders.Order;
import rs.edu.raf.si.bank2.otc.requests.FutureRequestBuySell;

public interface TransactionServiceInterface {
    Transaction save(Transaction transaction);

    List<Transaction> findAllByOrderId(Long orderId);

    List<Transaction> findAllByUserEmailAndCurrencyCode(String userEmail, String currencyCode);

    Transaction changeTransactionStatus(Long transactionId, TransactionStatus status);

    List<Transaction> saveAll(List<Transaction> transactionList);

    Transaction createTransaction(Order order, Balance balance, Float amount);

    Transaction createFutureOrderTransaction(
            FutureOrder futureOrder,
            Balance balance,
            Float amount,
            FutureRequestBuySell request,
            TransactionStatus status);
}

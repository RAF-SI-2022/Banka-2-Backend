package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.exceptions.TransactionNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.Balance;
import com.raf.si.Banka2Backend.models.mariadb.Currency;
import com.raf.si.Banka2Backend.models.mariadb.Transaction;
import com.raf.si.Banka2Backend.models.mariadb.TransactionStatus;
import com.raf.si.Banka2Backend.models.mariadb.orders.Order;
import com.raf.si.Banka2Backend.models.mariadb.orders.StockOrder;
import com.raf.si.Banka2Backend.repositories.mariadb.TransactionRepository;
import com.raf.si.Banka2Backend.services.interfaces.TransactionServiceInterface;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService implements TransactionServiceInterface {
    private final TransactionRepository transactionRepository;
    private final CurrencyService currencyService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, CurrencyService currencyService) {
        this.transactionRepository = transactionRepository;
        this.currencyService = currencyService;
    }

    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getTransactionsByCurrencyValue(String currencyCode) {
        List<Transaction> allTransactions = getAll();
        List<Transaction> toReturnTrans = new ArrayList<>();

        Optional<Currency> currency = currencyService.findByCurrencyCode(currencyCode);
        long test = currency.get().getId();

        for (Transaction trans : allTransactions) {
            if (trans.getCurrency().getId().equals(test)) {
                toReturnTrans.add(trans);
            }
        }
        return toReturnTrans;
    }

    @Override
    public Transaction save(Transaction transaction) {
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
        if (transactionOptional.isEmpty()) {
            throw new TransactionNotFoundException(transactionId);
        }
        Transaction transaction = transactionOptional.get();
        transaction.setStatus(status);
        return this.transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> saveAll(List<Transaction> transactionList) {
        return this.transactionRepository.saveAll(transactionList);
    }

    @Override
    public Transaction createTransaction(Order order, Balance balance, Float amount) {
        String currencyCode = "RSD";
        if (order instanceof StockOrder) currencyCode = ((StockOrder) order).getCurrencyCode();
        Currency c;
        Optional<Currency> currency = this.currencyService.findByCurrencyCode(currencyCode);
        c = currency.get();
        return Transaction.builder()
                .balance(balance)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .order(order)
                .user(order.getUser())
                .description(order.getOrderType() + " " + order.getTradeType().toString() + " transaction")
                .currency(c)
                .amount(amount)
                .reserved((float) order.getPrice())
                .status(TransactionStatus.WAITING)
                .build();
    }
}

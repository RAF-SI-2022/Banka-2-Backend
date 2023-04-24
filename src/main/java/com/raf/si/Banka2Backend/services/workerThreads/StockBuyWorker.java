package com.raf.si.Banka2Backend.services.workerThreads;

import com.raf.si.Banka2Backend.exceptions.OrderNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.*;
import com.raf.si.Banka2Backend.models.mariadb.orders.Order;
import com.raf.si.Banka2Backend.models.mariadb.orders.OrderStatus;
import com.raf.si.Banka2Backend.models.mariadb.orders.StockOrder;
import com.raf.si.Banka2Backend.repositories.mariadb.OrderRepository;
import com.raf.si.Banka2Backend.services.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class StockBuyWorker extends Thread {

    BlockingQueue<StockOrder> stockBuyRequestsQueue;
    UserStockService userStockService;
    StockService stockService;
    Random random = new Random();
    BalanceService balanceService;
    CurrencyService currencyService;
    TransactionService transactionService;
    OrderRepository orderRepository;

    public StockBuyWorker(BlockingQueue<StockOrder> blockingQueue, UserStockService userStockService,
                          StockService stockService, BalanceService balanceService, CurrencyService currencyService, TransactionService transactionService, OrderRepository orderRepository) {
        this.stockBuyRequestsQueue = blockingQueue;
        this.stockService = stockService;
        this.userStockService = userStockService;
        this.balanceService = balanceService;
        this.currencyService = currencyService;
        this.transactionService = transactionService;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run() {
        processBuyRequests();
    }


    //todo dodaj limit i stop kada budemo na kubernetesu sa influxDb
    private void processBuyRequests() {
        while (true) {
            try {
                StockOrder stockOrder = stockBuyRequestsQueue.take();

                Optional<UserStock> usersStockToChange = userStockService.findUserStockByUserIdAndStockSymbol(stockOrder.getUser().getId(), stockOrder.getSymbol());

                //todo DODATI CHECKOVE ZA LIMIT I STOP
                Balance balance = this.balanceService.findBalanceByUserIdAndCurrency(stockOrder.getUser().getId(), stockOrder.getCurrencyCode());
                if (stockOrder.isAllOrNone()) {
                    usersStockToChange.get().setAmount(usersStockToChange.get().getAmount() + stockOrder.getAmount());
                    Transaction transaction = this.transactionService.createTransaction(stockOrder, balance, (float) stockOrder.getPrice());
                    this.transactionService.save(transaction);
                } else {
                    int stockAmountSum = 0;
                    Stock stock = stockService.getStockBySymbol(stockOrder.getSymbol());
                    BigDecimal price = stock.getPriceValue().multiply(BigDecimal.valueOf(stockOrder.getAmount()));
                    List<Transaction> transactionList = new ArrayList<>();
                    while (stockOrder.getAmount() != stockAmountSum) {
                        int amountBought = random.nextInt(stockOrder.getAmount() - stockAmountSum) + 1;
                        stockAmountSum += amountBought;
                        usersStockToChange.get().setAmount(usersStockToChange.get().getAmount() + amountBought);
                        Transaction transaction = this.transactionService.createTransaction(stockOrder, balance, price.floatValue()*amountBought);
                        transactionList.add(transaction);
                    }
                    this.transactionService.saveAll(transactionList);
                }
                userStockService.save(usersStockToChange.get());
                this.balanceService.updateBalance(stockOrder, stockOrder.getUser().getEmail(), stockOrder.getCurrencyCode());
                Thread.sleep(10000);
                this.updateOrderStatus(stockOrder.getId(), OrderStatus.COMPLETE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void updateOrderStatus(Long id, OrderStatus orderStatus) {
        Optional<Order> order = this.orderRepository.findById(id);
        if(order.isPresent()) {
            order.get().setStatus(orderStatus);
            this.orderRepository.save(order.get());
        }
        throw new OrderNotFoundException(id);
    }


}

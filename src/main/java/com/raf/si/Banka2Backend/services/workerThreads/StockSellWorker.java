package com.raf.si.Banka2Backend.services.workerThreads;

import com.raf.si.Banka2Backend.exceptions.OrderNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.Balance;
import com.raf.si.Banka2Backend.models.mariadb.Stock;
import com.raf.si.Banka2Backend.models.mariadb.Transaction;
import com.raf.si.Banka2Backend.models.mariadb.UserStock;
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

public class StockSellWorker extends Thread {

    BlockingQueue<StockOrder> stockSellRequestsQueue;
    UserStockService userStockService;
    StockService stockService;
    TransactionService transactionService;
    OrderRepository orderRepository;
    BalanceService balanceService;
    Random random = new Random();

    public StockSellWorker(
            BlockingQueue<StockOrder> stockSellRequestsQueue,
            UserStockService userStockService,
            StockService stockService,
            TransactionService transactionService,
            OrderRepository orderRepository,
            BalanceService balanceService) {
        this.stockSellRequestsQueue = stockSellRequestsQueue;
        this.userStockService = userStockService;
        this.stockService = stockService;
        this.transactionService = transactionService;
        this.orderRepository = orderRepository;
        this.balanceService = balanceService;
    }

    @Override
    public void run() {
        processSellRequest();
    }

    // todo dodaj limit i stop kada budemo na kubernetesu sa influxDb
    private void processSellRequest() {
        while (true) {
            try {
                StockOrder stockOrder = stockSellRequestsQueue.take();

                if (stockOrder.getStop() == 0 && stockOrder.getStockLimit() == 0) {
                    Optional<UserStock> usersStockToChange = userStockService.findUserStockByUserIdAndStockSymbol(
                            stockOrder.getUser().getId(), stockOrder.getSymbol());
                    Balance balance = this.balanceService.findBalanceByUserIdAndCurrency(
                            stockOrder.getUser().getId(), stockOrder.getCurrencyCode());
                    if (stockOrder.isAllOrNone()) {
                        usersStockToChange
                                .get()
                                .setAmount(usersStockToChange.get().getAmount() - stockOrder.getAmount());
                        Transaction transaction = this.transactionService.createTransaction(
                                stockOrder, balance, (float) stockOrder.getPrice());
                        transactionService.save(transaction);
                    } else {
                        int stockAmountSum = 0;
                        Stock stock = stockService.getStockBySymbol(stockOrder.getSymbol());
                        BigDecimal price = stock.getPriceValue().multiply(BigDecimal.valueOf(stockOrder.getAmount()));
                        List<Transaction> transactionList = new ArrayList<>();
                        while (stockOrder.getAmount() != stockAmountSum) {
                            int amountBought = random.nextInt(stockOrder.getAmount() - stockAmountSum) + 1;
                            stockAmountSum += amountBought;
                            usersStockToChange
                                    .get()
                                    .setAmount(usersStockToChange.get().getAmount() - amountBought);
                            transactionList.add(this.transactionService.createTransaction(
                                    stockOrder, balance, price.floatValue() * amountBought));
                        }
                        this.transactionService.saveAll(transactionList);
                    }
                    userStockService.save(usersStockToChange.get());
                    this.balanceService.updateBalance(
                            stockOrder, stockOrder.getUser().getEmail(), stockOrder.getCurrencyCode());
                    this.updateOrderStatus(stockOrder.getId(), OrderStatus.COMPLETE);
                } else {
                    System.out.println("limit stop sell");
                }
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateOrderStatus(Long id, OrderStatus orderStatus) {
        System.out.println("usli smo");
        Optional<Order> order = this.orderRepository.findById(id);
        if (order.isPresent()) {
            System.out.println(order.get().getId());
            order.get().setStatus(orderStatus);
            this.orderRepository.save(order.get());
        } else {
            System.out.println("KURACCCC");
            throw new OrderNotFoundException(id);
        }
    }
}

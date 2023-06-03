package rs.edu.raf.si.bank2.main.services.workerThreads;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import rs.edu.raf.si.bank2.main.exceptions.OrderNotFoundException;
import rs.edu.raf.si.bank2.main.models.mariadb.*;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.Order;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.OrderStatus;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.StockOrder;
import rs.edu.raf.si.bank2.main.repositories.mariadb.OrderRepository;
import rs.edu.raf.si.bank2.main.services.BalanceService;
import rs.edu.raf.si.bank2.main.services.StockService;
import rs.edu.raf.si.bank2.main.services.TransactionService;
import rs.edu.raf.si.bank2.main.services.UserStockService;

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
                // These are checks for limit and stop. If they are set and order doesn't meet the requirements we skip that order and try again later.
                if(!this.stockService.checkLimitAndStopForSell(stockOrder)) {
                    stockSellRequestsQueue.put(stockOrder); // Put order back at the end of the queue.
                    continue;
                }
                Optional<UserStock> usersStockToChange = userStockService.findUserStockByUserIdAndStockSymbol(
                        stockOrder.getUser().getId(), stockOrder.getSymbol());
                Balance balance = this.balanceService.findBalanceByUserIdAndCurrency(
                        stockOrder.getUser().getId(), stockOrder.getCurrencyCode());
                Stock stock = this.stockService.findStockBySymbolInDb(stockOrder.getSymbol());
                if (stockOrder.isAllOrNone()) {
                    usersStockToChange
                            .get()
                            .setAmount(usersStockToChange.get().getAmount() - stockOrder.getAmount());
                    Transaction transaction = this.transactionService.createTransaction(
                            stockOrder, balance, stock.getPriceValue().floatValue()*stockOrder.getAmount(), stock.getPriceValue().floatValue()*stockOrder.getAmount());
                    transactionService.save(transaction);
                } else {
                    int stockAmountSum = 0;
                    BigDecimal price = stock.getPriceValue().multiply(BigDecimal.valueOf(stockOrder.getAmount()));
                    List<Transaction> transactionList = new ArrayList<>();
                    while (stockOrder.getAmount() != stockAmountSum) {
                        int amountBought = random.nextInt(stockOrder.getAmount() - stockAmountSum) + 1;
                        stockAmountSum += amountBought;
                        usersStockToChange
                                .get()
                                .setAmount(usersStockToChange.get().getAmount() - amountBought);
                        transactionList.add(this.transactionService.createTransaction(
                                stockOrder, balance, stock.getPriceValue().floatValue() * amountBought, stock.getPriceValue().floatValue()*stockOrder.getAmount()));
                    }
                    this.transactionService.saveAll(transactionList);
                }
                userStockService.save(usersStockToChange.get());
                this.balanceService.updateBalance(
                        stockOrder, stockOrder.getUser().getEmail(), stockOrder.getCurrencyCode());
                this.updateOrderStatus(stockOrder.getId(), OrderStatus.COMPLETE);
                this.transactionService.updateTransactionsStatusesOfOrder(stockOrder.getId(), TransactionStatus.COMPLETE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateOrderStatus(Long id, OrderStatus orderStatus) {
        Optional<Order> order = this.orderRepository.findById(id);
        if (order.isPresent()) {
            System.out.println(order.get().getId());
            order.get().setStatus(orderStatus);
            this.orderRepository.save(order.get());
        } else {
            throw new OrderNotFoundException(id);
        }
    }
}

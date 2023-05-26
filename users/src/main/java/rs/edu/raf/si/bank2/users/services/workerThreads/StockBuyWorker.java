package rs.edu.raf.si.bank2.users.services.workerThreads;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import rs.edu.raf.si.bank2.users.exceptions.OrderNotFoundException;
import rs.edu.raf.si.bank2.users.models.mariadb.Balance;
import rs.edu.raf.si.bank2.users.models.mariadb.Stock;
import rs.edu.raf.si.bank2.users.models.mariadb.Transaction;
import rs.edu.raf.si.bank2.users.models.mariadb.UserStock;
import rs.edu.raf.si.bank2.users.models.mariadb.orders.Order;
import rs.edu.raf.si.bank2.users.models.mariadb.orders.OrderStatus;
import rs.edu.raf.si.bank2.users.models.mariadb.orders.StockOrder;
import rs.edu.raf.si.bank2.users.repositories.mariadb.OrderRepository;
import rs.edu.raf.si.bank2.users.services.*;

public class StockBuyWorker extends Thread {

    BlockingQueue<StockOrder> stockBuyRequestsQueue;
    UserStockService userStockService;
    StockService stockService;
    Random random = new Random();
    BalanceService balanceService;
    CurrencyService currencyService;
    TransactionService transactionService;
    OrderRepository orderRepository;

    public StockBuyWorker(
            BlockingQueue<StockOrder> blockingQueue,
            UserStockService userStockService,
            StockService stockService,
            BalanceService balanceService,
            CurrencyService currencyService,
            TransactionService transactionService,
            OrderRepository orderRepository) {
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

    // TODO ovo treba popraviti, baca null pointer exception i jos mnogo
    //  drugih gresaka (foreign key, itd), testirati
    // 2023-05-09 02:29:19.658 ERROR 18608 --- [       Thread-1] o.h.engine
    // .jdbc.spi.SqlExceptionHelper   : (conn=323) Cannot add or update a
    // child row: a foreign key constraint fails (`test`.`transactions`,
    // CONSTRAINT `transactions_ibfk_2` FOREIGN KEY (`order_id`) REFERENCES
    // `orders` (`id`))
    // todo dodaj limit i stop kada budemo na kubernetesu sa influxDb
    private void processBuyRequests() {
        while (true) {
            try {
                if (userStockService == null) {
                    System.err.println("userStockService is null");
                    return;
                }

                StockOrder stockOrder = stockBuyRequestsQueue.take();

                Optional<UserStock> usersStockToChange = userStockService.findUserStockByUserIdAndStockSymbol(
                        stockOrder.getUser().getId(), stockOrder.getSymbol());

                // prvi put kupujemo stock
                if (usersStockToChange.isEmpty() && !stockOrder.getSymbol().isBlank()) {
                    Stock stock = stockService.getStockBySymbol(stockOrder.getSymbol());
                    UserStock userStock = new UserStock(0L, stockOrder.getUser(), stock, 0, 0);
                    userStockService.save(userStock);
                    usersStockToChange = userStockService.findUserStockByUserIdAndStockSymbol(
                            stockOrder.getUser().getId(), stockOrder.getSymbol());
                }

                // todo DODATI CHECKOVE ZA LIMIT I STOP
                Balance balance = this.balanceService.findBalanceByUserIdAndCurrency(
                        stockOrder.getUser().getId(), stockOrder.getCurrencyCode());
                //
                //                if (balance.getAmount() < (stockOrder.getAmount() * stockOrder.getPrice()){
                //
                //                }

                //                this.balanceService.reserveAmount( // duplikat - pare se rezervisu pre nego sto se
                // order doda queue; ovo je zakomentarisano(a ne skroz izbrisano) za svaki slucaj ako se pokaze da je
                // ipak potrebno u nekim slucajevima
                //                        (float) (stockOrder.getAmount() * stockOrder.getPrice()),
                //                        stockOrder.getUser().getEmail(),
                //                        stockOrder.getCurrencyCode());

                if (stockOrder.isAllOrNone()) {
                    usersStockToChange.get().setAmount(usersStockToChange.get().getAmount() + stockOrder.getAmount());
                    Transaction transaction = this.transactionService.createTransaction(
                            stockOrder, balance, (float) stockOrder.getPrice());
                    this.transactionService.save(transaction);
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
                                .setAmount(usersStockToChange.get().getAmount() + amountBought);
                        Transaction transaction = this.transactionService.createTransaction(
                                stockOrder, balance, price.floatValue() * amountBought);
                        transactionList.add(transaction);
                    }
                    this.transactionService.saveAll(transactionList);
                }
                userStockService.save(usersStockToChange.get());
                this.balanceService.updateBalance(
                        stockOrder, stockOrder.getUser().getEmail(), stockOrder.getCurrencyCode());
                this.updateOrderStatus(stockOrder.getId(), OrderStatus.COMPLETE);
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateOrderStatus(Long id, OrderStatus orderStatus) {
        Optional<Order> order = this.orderRepository.findById(id);

        if (order.isPresent()) {
            order.get().setStatus(orderStatus);
            this.orderRepository.save(order.get());
        } else throw new OrderNotFoundException(id);
    }
}

package rs.edu.raf.si.bank2.main.services.workerThreads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import rs.edu.raf.si.bank2.main.dto.AccountType;
import rs.edu.raf.si.bank2.main.dto.CommunicationDto;
import rs.edu.raf.si.bank2.main.dto.MarginTransactionDto;
import rs.edu.raf.si.bank2.main.dto.TransactionType;
import rs.edu.raf.si.bank2.main.exceptions.OrderNotFoundException;
import rs.edu.raf.si.bank2.main.models.mariadb.*;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.Order;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.OrderStatus;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.StockOrder;
import rs.edu.raf.si.bank2.main.repositories.mariadb.OrderRepository;
import rs.edu.raf.si.bank2.main.services.*;

public class StockBuyWorker extends Thread {

    BlockingQueue<StockOrder> stockBuyRequestsQueue;
    UserStockService userStockService;
    StockService stockService;
    Random random = new Random();
    BalanceService balanceService;
    CurrencyService currencyService;
    TransactionService transactionService;
    UserService userService;
    OrderRepository orderRepository;
    ObjectMapper mapper = new ObjectMapper();
    UserCommunicationService userCommunicationService;

    public StockBuyWorker(
            BlockingQueue<StockOrder> blockingQueue,
            UserStockService userStockService,
            StockService stockService,
            BalanceService balanceService,
            CurrencyService currencyService,
            TransactionService transactionService,
            OrderRepository orderRepository,
            UserService userService,
            UserCommunicationService userCommunicationService) {
        this.stockBuyRequestsQueue = blockingQueue;
        this.stockService = stockService;
        this.userStockService = userStockService;
        this.balanceService = balanceService;
        this.currencyService = currencyService;
        this.transactionService = transactionService;
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.userCommunicationService = userCommunicationService;
    }

    @Override
    public void run() {
        processBuyRequests();
    }
    // todo dodaj limit i stop kada budemo na kubernetesu sa influxDb
    private void processBuyRequests() {
        while (true) {
            try {
                if (userStockService == null) {
                    System.err.println("userStockService is null");
                    return;
                }
                StockOrder stockOrder = stockBuyRequestsQueue.take();

                // These are checks for limit and stop. If they are set and order doesn't meet the requirements we skip
                // that order and try again later.
                if (!this.stockService.checkLimitAndStopForBuy(stockOrder)) {
                    stockBuyRequestsQueue.put(stockOrder); // Put order back at the end of the queue.
                    continue;
                }

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

                CommunicationDto communicationDto;
                Balance balance = this.balanceService.findBalanceByUserIdAndCurrency(
                        stockOrder.getUser().getId(), stockOrder.getCurrencyCode());
                Stock stock = this.stockService.findStockBySymbolInDb(stockOrder.getSymbol());

                // todo if not margin
                if (!stockOrder.isMargin()) {
                    this.balanceService.reserveAmount(
                            stock.getPriceValue().floatValue() * stockOrder.getAmount(),
                            stockOrder.getUser().getEmail(),
                            stockOrder.getCurrencyCode(),
                            true);
                }

                User user = stockOrder.getUser();
                user.setDailyLimit(user.getDailyLimit() - stock.getPriceValue().floatValue() * stockOrder.getAmount());
                this.userService.save(user);

                if (stockOrder.isAllOrNone()) {
                    usersStockToChange.get().setAmount(usersStockToChange.get().getAmount() + stockOrder.getAmount());

                    // ako je margin saljemo otc servisu
                    if (stockOrder.isMargin()) {
                        sendMarginTransaction(stockOrder, user, stockOrder.getPrice());
                    } else {
                        Transaction transaction = this.transactionService.createTransaction(
                                stockOrder,
                                balance,
                                stock.getPriceValue().floatValue() * stockOrder.getAmount(),
                                stock.getPriceValue().floatValue() * stockOrder.getAmount());
                        this.transactionService.save(transaction);
                    }
                } else {
                    int stockAmountSum = 0;
                    List<Transaction> transactionList = new ArrayList<>();
                    while (stockAmountSum < stockOrder.getAmount()) {
                        int amountBought = random.nextInt(stockOrder.getAmount() - stockAmountSum) + 1;
                        stockAmountSum += amountBought;
                        usersStockToChange
                                .get()
                                .setAmount(usersStockToChange.get().getAmount() + amountBought);

                        // todo ovde pozovi margin send
                        if (stockOrder.isMargin()) {
                            sendMarginTransaction(
                                    stockOrder, user, stock.getPriceValue().doubleValue() * amountBought);
                        } else {
                            Transaction transaction = this.transactionService.createTransaction(
                                    stockOrder,
                                    balance,
                                    stock.getPriceValue().floatValue() * amountBought,
                                    stock.getPriceValue().floatValue() * stockOrder.getAmount());
                            transactionList.add(transaction);
                        }
                    }
                    if (!stockOrder.isMargin()) this.transactionService.saveAll(transactionList);
                }
                userStockService.save(usersStockToChange.get());

                if (stockOrder.isMargin()) {
                    this.updateOrderStatus(stockOrder.getId(), OrderStatus.COMPLETE);
                    continue;
                }

                // todo ako je margin preskoco ovo isamo markiraj da je order complete
                this.balanceService.updateBalance(
                        stockOrder, stockOrder.getUser().getEmail(), stockOrder.getCurrencyCode(), true);
                this.updateOrderStatus(stockOrder.getId(), OrderStatus.COMPLETE);
                this.transactionService.updateTransactionsStatusesOfOrder(
                        stockOrder.getId(), TransactionStatus.COMPLETE);

            } catch (Exception e) {
                // If unexpected error occurs, we want to print it and to continue.
                // Exception must not crash this worker thread.
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

    private CommunicationDto sendMarginTransaction(StockOrder stockOrder, User user, Double price) {
        // ako je MARGIN order posalji ga na drugi service umesto da ga ovde obradjujes
        CommunicationDto communicationDto;
        MarginTransactionDto marginTransactionDto = new MarginTransactionDto();
        marginTransactionDto.setAccountType(AccountType.MARGIN);
        marginTransactionDto.setOrderId(stockOrder.getId());
        marginTransactionDto.setTransactionComment("Prodaja akcija");
        marginTransactionDto.setCurrencyCode(stockOrder.getCurrencyCode());
        marginTransactionDto.setTransactionType(TransactionType.BUY);
        marginTransactionDto.setInitialMargin(price);
        marginTransactionDto.setMaintenanceMargin(price * 0.4); // za odrzavanje je 40% full cene
        try {
            String marginDtoJson = mapper.writeValueAsString(marginTransactionDto);
            communicationDto =
                    userCommunicationService.sendMarginTransaction("/makeTransaction", marginDtoJson, user.getEmail());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return communicationDto;
    }
}

package rs.edu.raf.si.bank2.main.services.workerThreads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    ObjectMapper mapper = new ObjectMapper();
    UserCommunicationService userCommunicationService;


    public StockSellWorker(
            BlockingQueue<StockOrder> stockSellRequestsQueue,
            UserStockService userStockService,
            StockService stockService,
            TransactionService transactionService,
            OrderRepository orderRepository,
            BalanceService balanceService,
            UserCommunicationService userCommunicationService
    ) {
        this.stockSellRequestsQueue = stockSellRequestsQueue;
        this.userStockService = userStockService;
        this.stockService = stockService;
        this.transactionService = transactionService;
        this.orderRepository = orderRepository;
        this.balanceService = balanceService;
        this.userCommunicationService = userCommunicationService;
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
                if (!this.stockService.checkLimitAndStopForSell(stockOrder)) {
                    stockSellRequestsQueue.put(stockOrder); // Put order back at the end of the queue.
                    continue;
                }

                Optional<UserStock> usersStockToChange = userStockService.findUserStockByUserIdAndStockSymbol(stockOrder.getUser().getId(), stockOrder.getSymbol());
                Balance balance = this.balanceService.findBalanceByUserIdAndCurrency(stockOrder.getUser().getId(), stockOrder.getCurrencyCode());
                Stock stock = this.stockService.findStockBySymbolInDb(stockOrder.getSymbol());
                User user = stockOrder.getUser();


                if (stockOrder.isAllOrNone()) {
                    usersStockToChange.get().setAmount(usersStockToChange.get().getAmount() - stockOrder.getAmount());

                    //ako je margin saljemo otc servisu
                    if (stockOrder.isMargin()) {
                        sendMarginTransaction(stockOrder, user, stockOrder.getPrice());
                    }
                    else {
                        Transaction transaction = this.transactionService.createTransaction(
                                stockOrder, balance, stock.getPriceValue().floatValue() * stockOrder.getAmount(), stock.getPriceValue().floatValue() * stockOrder.getAmount());
                        transactionService.save(transaction);
                    }
                } else {
                    int stockAmountSum = 0;
//                    BigDecimal price = stock.getPriceValue().multiply(BigDecimal.valueOf(stockOrder.getAmount()));
                    List<Transaction> transactionList = new ArrayList<>();
                    while (stockOrder.getAmount() != stockAmountSum) {
                        int amountBought = random.nextInt(stockOrder.getAmount() - stockAmountSum) + 1;
                        stockAmountSum += amountBought;
                        usersStockToChange.get().setAmount(usersStockToChange.get().getAmount() - amountBought);

                        if (stockOrder.isMargin()) {
                            sendMarginTransaction(stockOrder, user, stock.getPriceValue().doubleValue() * amountBought);
                        }
                        else {
                            transactionList.add(this.transactionService.createTransaction(
                                    stockOrder, balance, stock.getPriceValue().floatValue() * amountBought, stock.getPriceValue().floatValue() * stockOrder.getAmount()));
                        }
                    }
                    if (!stockOrder.isMargin())
                        this.transactionService.saveAll(transactionList);
                }
                userStockService.save(usersStockToChange.get());

                if (stockOrder.isMargin()){
                    this.updateOrderStatus(stockOrder.getId(), OrderStatus.COMPLETE);
                    continue;
                }

                //todo ako je margin preskoco ovo isamo markiraj da je order complete
                this.balanceService.updateBalance(stockOrder, stockOrder.getUser().getEmail(), stockOrder.getCurrencyCode(), true);
                this.updateOrderStatus(stockOrder.getId(), OrderStatus.COMPLETE);
                this.transactionService.updateTransactionsStatusesOfOrder(stockOrder.getId(), TransactionStatus.COMPLETE);
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
            System.out.println(order.get().getId());
            order.get().setStatus(orderStatus);
            this.orderRepository.save(order.get());
        } else {
            throw new OrderNotFoundException(id);
        }
    }

    private CommunicationDto sendMarginTransaction(StockOrder stockOrder, User user, Double price) {
        //ako je MARGIN order posalji ga na drugi service umesto da ga ovde obradjujes
        CommunicationDto communicationDto;
        MarginTransactionDto marginTransactionDto = new MarginTransactionDto();
        marginTransactionDto.setAccountType(AccountType.MARGIN);
        marginTransactionDto.setOrderId(stockOrder.getId());
        marginTransactionDto.setTransactionComment("Prodaja akcija");
        marginTransactionDto.setCurrencyCode(stockOrder.getCurrencyCode());
        marginTransactionDto.setTransactionType(TransactionType.SELL);
        marginTransactionDto.setInitialMargin(price);
        marginTransactionDto.setMaintenanceMargin(price * 0.4); //za odrzavanje je 40% full cene
        try {
            String marginDtoJson = mapper.writeValueAsString(marginTransactionDto);
            communicationDto = userCommunicationService.sendMarginTransaction("/makeTransaction", marginDtoJson, user.getEmail());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return communicationDto;
    }
}

package com.raf.si.Banka2Backend.services.workerThreads;

import com.raf.si.Banka2Backend.models.mariadb.Stock;
import com.raf.si.Banka2Backend.models.mariadb.UserStock;
import com.raf.si.Banka2Backend.models.mariadb.orders.StockOrder;
import com.raf.si.Banka2Backend.requests.StockRequest;
import com.raf.si.Banka2Backend.services.StockService;
import com.raf.si.Banka2Backend.services.UserStockService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class StockSellWorker extends Thread {

    BlockingQueue<StockOrder> stockSellRequestsQueue;
    UserStockService userStockService;
    StockService stockService;
    Random random = new Random();

    public StockSellWorker(BlockingQueue<StockOrder> stockSellRequestsQueue, UserStockService userStockService, StockService stockService) {
        this.stockSellRequestsQueue = stockSellRequestsQueue;
        this.userStockService = userStockService;
        this.stockService = stockService;
    }

    @Override
    public void run() {
        processSellRequest();
    }

    //todo dodaj limit i stop kada budemo na kubernetesu sa influxDb
    private void processSellRequest() {
        while (true) {
            try {
                StockOrder stockOrder = stockSellRequestsQueue.take();

                if (stockOrder.getStop() == 0 && stockOrder.getLimit() == 0) {
                    Optional<UserStock> usersStockToChange = userStockService.findUserStockByUserIdAndStockSymbol(stockOrder.getUser().getId(), stockOrder.getSymbol());

                    if (stockOrder.isAllOrNone()) {
                        usersStockToChange.get().setAmount(usersStockToChange.get().getAmount() - stockOrder.getAmount());
                        //todo DODATI TRANSAKCIJU I PROMENITI BALANS

                    } else {
                        int stockAmountSum = 0;
                        Stock stock = stockService.getStockBySymbol(stockOrder.getSymbol());
                        BigDecimal price = stock.getPriceValue().multiply(BigDecimal.valueOf(stockOrder.getAmount()));

                        while (stockOrder.getAmount() != stockAmountSum) {
                            int amountBought = random.nextInt(stockOrder.getAmount() - stockAmountSum) + 1;
                            stockAmountSum += amountBought;
                            usersStockToChange.get().setAmount(usersStockToChange.get().getAmount() - amountBought);
                            //todo napravi transakciju i dodaj je u neku listu (sacuvaj u njoj koliko je kupljeno stockova i price * amountBought)
                        }
                    }
                    userStockService.save(usersStockToChange.get());
                } else {
                    System.out.println("limit stop sell");
                }
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}


package com.raf.si.Banka2Backend.services.workerThreads;

import com.raf.si.Banka2Backend.models.mariadb.Stock;
import com.raf.si.Banka2Backend.models.mariadb.UserStock;
import com.raf.si.Banka2Backend.requests.StockRequest;
import com.raf.si.Banka2Backend.services.StockService;
import com.raf.si.Banka2Backend.services.UserStockService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class StockBuyWorker extends Thread {

    BlockingQueue<StockRequest> stockBuyRequestsQueue;
    UserStockService userStockService;
    StockService stockService;
    Random random = new Random();

    public StockBuyWorker(BlockingQueue<StockRequest> blockingQueue, UserStockService userStockService, StockService stockService) {
        this.stockBuyRequestsQueue = blockingQueue;
        this.stockService = stockService;
        this.userStockService = userStockService;
    }

    @Override
    public void run() {
        processBuyRequests();
    }


    //todo dodaj limit i stop kada budemo na kubernetesu sa influxDb
    private void processBuyRequests() {
        while (true) {
            try {
                StockRequest stockRequest = stockBuyRequestsQueue.take();

                Optional<UserStock> usersStockToChange = userStockService.findUserStockByUserIdAndStockSymbol(stockRequest.getUserId(), stockRequest.getStockSymbol());

                //todo DODATI CHECKOVE ZA LIMIT I STOP

                if (stockRequest.isAllOrNone()) {
                    usersStockToChange.get().setAmount(usersStockToChange.get().getAmount() + stockRequest.getAmount());
                    //todo OVDE URADITI TRANSAKCIJU ZA USER BALANS
                } else {
                    int stockAmountSum = 0;
                    Stock stock = stockService.getStockBySymbol(stockRequest.getStockSymbol());
                    BigDecimal price = stock.getPriceValue().multiply(BigDecimal.valueOf(stockRequest.getAmount()));

                    while (stockRequest.getAmount() != stockAmountSum) {
                        int amountBought = random.nextInt(stockRequest.getAmount() - stockAmountSum) + 1;
                        stockAmountSum += amountBought;
                        usersStockToChange.get().setAmount(usersStockToChange.get().getAmount() + amountBought);
                        //todo napravi transakciju i dodaj je u neku listu (sacuvaj u njoj koliko je kupljeno stockova i price * amountBought)
                    }
                }
                userStockService.save(usersStockToChange.get());
                //todo pozvati funkciju koja menja balans na osnovu transakcija
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

}

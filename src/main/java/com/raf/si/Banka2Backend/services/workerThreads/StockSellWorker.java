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

public class StockSellWorker extends Thread {

    BlockingQueue<StockRequest> stockSellRequestsQueue;
    UserStockService userStockService;
    StockService stockService;
    Random random = new Random();

    public StockSellWorker(BlockingQueue<StockRequest> stockSellRequestsQueue, UserStockService userStockService, StockService stockService) {
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
                StockRequest stockRequest = stockSellRequestsQueue.take();

                if (stockRequest.getStop() == 0 && stockRequest.getLimit() == 0) {
                    Optional<UserStock> usersStockToChange = userStockService.findUserStockByUserIdAndStockSymbol(stockRequest.getUserId(), stockRequest.getStockSymbol());

                    if (stockRequest.isAllOrNone()) {
                        usersStockToChange.get().setAmount(usersStockToChange.get().getAmount() - stockRequest.getAmount());
                        //todo DODATI TRANSAKCIJU I PROMENITI BALANS
                    } else {
                        int stockAmountSum = 0;
                        Stock stock = stockService.getStockBySymbol(stockRequest.getStockSymbol());
                        BigDecimal price = stock.getPriceValue().multiply(BigDecimal.valueOf(stockRequest.getAmount()));

                        while (stockRequest.getAmount() != stockAmountSum) {
                            int amountBought = random.nextInt(stockRequest.getAmount() - stockAmountSum) + 1;
                            stockAmountSum += amountBought;
                            usersStockToChange.get().setAmount(usersStockToChange.get().getAmount() - amountBought);
                            //todo napravi transakciju i dodaj je u neku listu (sacuvaj u njoj koliko je kupljeno stockova i price * amountBought)
                        }
                    }
                    userStockService.save(usersStockToChange.get());
                } else {
                    System.out.println("limit stop sell");
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}


package com.raf.si.Banka2Backend.services.workerThreads;

import com.raf.si.Banka2Backend.requests.StockRequest;
import com.raf.si.Banka2Backend.services.StockService;
import com.raf.si.Banka2Backend.services.UserStockService;

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

    private void processSellRequest() {
        while (true) {
            try {
                StockRequest stockRequest = stockSellRequestsQueue.take();


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*
            if (stockRequest.getStop() == 0 && stockRequest.getLimit() == 0) {
            Optional<UserStock> userStock =userStockService.findUserStockByUserIdAndStockSymbol(stockRequest.getUserId(), stockRequest.getStockSymbol());

            // premestamo iz amount u amount_for_sale
            if ((userStock.get().getAmount() - stockRequest.getAmount()) < 0) {
                return ResponseEntity.status(500).body("Internal error");
            }

            userStock.get().setAmount(userStock.get().getAmount() - stockRequest.getAmount());
            userStock.get().setAmountForSale(userStock.get().getAmountForSale() + stockRequest.getAmount());
            return ResponseEntity.ok().body(userStockService.save(userStock.get()));
        } else {
            // todo sell with limits
            System.out.println("sell with limits");
        }
     */

}


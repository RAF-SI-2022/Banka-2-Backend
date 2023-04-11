package com.raf.si.Banka2Backend.services.workerThreads;

import com.raf.si.Banka2Backend.requests.StockRequest;
import lombok.SneakyThrows;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StockBuyWorker extends Thread{

    List<StockRequest> buyOrders = new CopyOnWriteArrayList<>();

    public StockBuyWorker() {}

    @SneakyThrows
    @Override
    public void run() {








        Thread.sleep(10000);
    }


    public List<StockRequest> getBuyOrders() {
        return buyOrders;
    }
}

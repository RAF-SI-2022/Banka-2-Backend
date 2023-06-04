package rs.edu.raf.si.bank2.main.services.workerThreads;

import rs.edu.raf.si.bank2.main.services.StockService;

public class StocksRetrieverFromApiWorker extends Thread {
    private final StockService stockService;

    public StocksRetrieverFromApiWorker(StockService stockService) {
        this.stockService = stockService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.stockService.updateAllStocksInDb();
                sleep(900000); // 15min = 1000 * 60 * 15 = 900000
            } catch (Exception e) {
                // If any unexpected error occurs, we want to print it and to continue.
                // Exception must not crash this thread worker.
                e.printStackTrace();
            }
        }
    }
}

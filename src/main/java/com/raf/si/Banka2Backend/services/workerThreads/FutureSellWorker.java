package com.raf.si.Banka2Backend.services.workerThreads;

import com.raf.si.Banka2Backend.models.mariadb.Future;
import com.raf.si.Banka2Backend.requests.FutureRequestBuySell;
import com.raf.si.Banka2Backend.services.FutureService;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.SneakyThrows;

public class FutureSellWorker extends Thread {

  private List<FutureRequestBuySell> futuresRequests;
  private List<Future> futuresByName;
  private FutureService futureService;
  private Future futureToCheckFor;
  private List<Long> requestsToRemove;

  public FutureSellWorker(FutureService futureService) {
    futuresRequests = new CopyOnWriteArrayList<>();
    futuresByName = new CopyOnWriteArrayList<>();
    requestsToRemove = new CopyOnWriteArrayList<>();
    this.futureService = futureService;
  }

  @SneakyThrows
  @Override
  public void run() {
    while (true) {

      for (FutureRequestBuySell request : futuresRequests) {
        futuresByName =
            futureService
                .findFuturesByFutureName(request.getFutureName())
                .get(); // nadjemo sve kojie imaju isto ime kao request

        for (Future futureFromTable : futuresByName) {
          if (request.getLimit() != 0) { // ako je postalvjen limit
            if (futureFromTable.isForSale()
                && futureFromTable.getMaintenanceMargin()
                    > request.getLimit()) { // ako se pojavio neki koji triggeruje limit
              // todo promeni cenu i stavi da je for sale
              // continue
              // add to remove
            }
          }

          if (request.getStop() != 0) {
            //            if(){
            // todo in progress
            //            }
          }
        }
      }

      Thread.sleep(30000); // todo promeni ako treba duzinu sleep-a
    }
  }
}

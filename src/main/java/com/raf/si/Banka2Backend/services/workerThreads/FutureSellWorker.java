package com.raf.si.Banka2Backend.services.workerThreads;

import com.raf.si.Banka2Backend.models.mariadb.Future;
import com.raf.si.Banka2Backend.requests.FutureRequestBuySell;
import com.raf.si.Banka2Backend.services.FutureService;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.SneakyThrows;

public class FutureSellWorker extends Thread {

  private List<FutureRequestBuySell> futuresRequests;
  private List<FutureRequestBuySell> requestsToRemove;
  private List<Future> futuresByName;
  private FutureService futureService;
  private boolean next = false;

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

        // nadjemo sve kojie imaju isto ime kao request
        futuresByName = futureService.findFuturesByFutureName(request.getFutureName()).get();

        for (Future futureFromTable : futuresByName) {
          if (next) continue;

          if (request.getLimit() != 0) { // ako je postalvjen limit
            // ako se pojavio neki koji triggeruje limit
            if (futureFromTable.isForSale()
                && futureFromTable.getMaintenanceMargin() > request.getLimit()
                && !futureFromTable.getId().equals(request.getId())) {
              Future futureFromRequest = futureService.findById(request.getId()).get();
              futureFromRequest.setMaintenanceMargin(request.getPrice());
              futureFromRequest.setForSale(true);
              requestsToRemove.add(request);
              futureService.updateFuture(futureFromRequest);
              next = true;
            }
          }
          if (request.getStop() != 0) { // ako je postalvjen stop
            // ako se pojavio neki koji triggeruje stop
            if (futureFromTable.isForSale()
                && futureFromTable.getMaintenanceMargin() < request.getStop()
                && !futureFromTable.getId().equals(request.getId())) {
              Future futureFromRequest = futureService.findById(request.getId()).get();
              futureFromRequest.setMaintenanceMargin(request.getPrice());
              futureFromRequest.setForSale(true);
              requestsToRemove.add(request);
              futureService.updateFuture(futureFromRequest);
              next = true;
            }
          }
        }
        next = false;
      }

      removeFinishedRequests();
      Thread.sleep(10000); // todo promeni ako treba duzinu sleep-a
    }
  }

  private void removeFinishedRequests() {
    for (FutureRequestBuySell request : requestsToRemove) {
      if (futuresRequests.contains(request)) futuresRequests.remove(request);
    }
    futuresRequests.clear();
  }

  public List<FutureRequestBuySell> getFuturesRequests() {
    return futuresRequests;
  }
}

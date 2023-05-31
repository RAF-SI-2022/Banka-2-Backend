package rs.edu.raf.si.bank2.otc.services.workerThreads;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.SneakyThrows;
import rs.edu.raf.si.bank2.otc.models.mariadb.Future;
import rs.edu.raf.si.bank2.otc.requests.FutureRequestBuySell;
import rs.edu.raf.si.bank2.otc.services.FutureService;

public class FutureSellWorker extends Thread {

    private Map<Long, FutureRequestBuySell> futuresRequestsMap = new ConcurrentHashMap<>();
    private List<FutureRequestBuySell> requestsToRemove;
    private List<Future> futuresByName;
    private FutureService futureService;
    private boolean next = false;

    public FutureSellWorker(FutureService futureService) {
        futuresByName = new CopyOnWriteArrayList<>();
        requestsToRemove = new CopyOnWriteArrayList<>();
        this.futureService = futureService;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {

            for (Map.Entry<Long, FutureRequestBuySell> request : futuresRequestsMap.entrySet()) {

                // nadjemo sve kojie imaju isto ime kao request
                futuresByName = futureService
                        .findFuturesByFutureName(request.getValue().getFutureName())
                        .get();

                for (Future futureFromTable : futuresByName) {
                    if (next) continue;

                    if (request.getValue().getLimit() != 0) { // ako je postalvjen limit
                        // ako se pojavio neki koji triggeruje limit
                        if (!futureFromTable.isForSale()
                                && futureFromTable.getMaintenanceMargin()
                                        > request.getValue().getLimit()
                                && !futureFromTable
                                        .getId()
                                        .equals(request.getValue().getId())) {
                            Future futureFromRequest = futureService
                                    .findById(request.getValue().getId())
                                    .get();
                            futureFromRequest.setMaintenanceMargin(
                                    request.getValue().getPrice());
                            futureFromRequest.setForSale(true);
                            futuresRequestsMap.remove(request.getKey());
                            futureService.saveFuture(futureFromRequest);
                            next = true;
                        }
                    }
                    if (request.getValue().getStop() != 0) { // ako je postalvjen stop
                        // ako se pojavio neki koji triggeruje stop
                        if (!futureFromTable.isForSale()
                                && futureFromTable.getMaintenanceMargin()
                                        < request.getValue().getStop()
                                && !futureFromTable
                                        .getId()
                                        .equals(request.getValue().getId())) {
                            Future futureFromRequest = futureService
                                    .findById(request.getValue().getId())
                                    .get();
                            futureFromRequest.setMaintenanceMargin(
                                    request.getValue().getPrice());
                            futureFromRequest.setForSale(true);
                            futuresRequestsMap.remove(request.getKey());
                            futureService.saveFuture(futureFromRequest);
                            next = true;
                        }
                    }
                }
                next = false;
            }

            Thread.sleep(10000); // todo promeni ako treba duzinu sleep-a
        }
    }

    public Map<Long, FutureRequestBuySell> getFuturesRequestsMap() {
        return futuresRequestsMap;
    }

    public void putInFuturesRequestsMap(Long singleId, FutureRequestBuySell future) {
        this.futuresRequestsMap.put(singleId, future);
    }

    public boolean removeFuture(Long id) {

        if (this.futuresRequestsMap.containsKey(id)) {
            this.futuresRequestsMap.remove(id);
            return false;
        }
        return true;
    }
}

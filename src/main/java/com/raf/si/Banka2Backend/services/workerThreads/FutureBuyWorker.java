package com.raf.si.Banka2Backend.services.workerThreads;

import com.raf.si.Banka2Backend.exceptions.BalanceNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.Balance;
import com.raf.si.Banka2Backend.models.mariadb.Future;
import com.raf.si.Banka2Backend.models.mariadb.User;
import com.raf.si.Banka2Backend.models.mariadb.orders.FutureOrder;
import com.raf.si.Banka2Backend.models.mariadb.orders.OrderStatus;
import com.raf.si.Banka2Backend.models.mariadb.orders.OrderTradeType;
import com.raf.si.Banka2Backend.repositories.mariadb.OrderRepository;
import com.raf.si.Banka2Backend.requests.FutureRequestBuySell;
import com.raf.si.Banka2Backend.services.BalanceService;
import com.raf.si.Banka2Backend.services.FutureService;
import com.raf.si.Banka2Backend.services.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;

public class FutureBuyWorker extends Thread {

    private final Map<Long, FutureRequestBuySell> futuresRequestsMap;
    private final List<Long> keysToRemove;
    private List<Future> futuresByName;
    private final FutureService futureService;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final BalanceService balanceService;
    private boolean next = false;

    public FutureBuyWorker(FutureService futureService, UserService userService, OrderRepository orderRepository, BalanceService balanceService) {
        this.futuresRequestsMap = new ConcurrentHashMap<>();
        this.futuresByName = new CopyOnWriteArrayList<>();
        this.keysToRemove = new CopyOnWriteArrayList<>();
        this.futureService = futureService;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.balanceService = balanceService;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            for (Map.Entry<Long, FutureRequestBuySell> requestEntry : futuresRequestsMap.entrySet()) {
                this.futuresByName = futureService.findFuturesByFutureName(requestEntry.getValue().getFutureName()).get();

                for (Future futureFromTable : futuresByName) {
                    if (next) continue;

                    Optional<User> optionalUserBuyer = userService.findById(requestEntry.getValue().getUserId());
                    Double price = (double) futureFromTable.getMaintenanceMargin();
                    Double dailyLimit = optionalUserBuyer.get().getDailyLimit();
                    if (optionalUserBuyer.isPresent() && optionalUserBuyer.get().getDailyLimit() != null) {
                        if (price > dailyLimit) {
                            continue;
                        }
                    } else {
                        keysToRemove.add(requestEntry.getKey());
                    }
                    if (requestEntry.getValue().getLimit() != 0 || requestEntry.getValue().getStop() != 0) { // ako je postavljen limit ili stop
                        if (futureFromTable.isForSale() && (price < requestEntry.getValue().getLimit() || price > requestEntry.getValue().getStop())) {
                            if (optionalUserBuyer.isPresent() && optionalUserBuyer.get().getDailyLimit() != null) {
                                Balance balance;
                                try {
                                    balance = this.balanceService.findBalanceByUserEmailAndCurrencyCode(optionalUserBuyer.get().getEmail(), requestEntry.getValue().getCurrencyCode());
                                } catch (BalanceNotFoundException e) {
                                    e.printStackTrace();
                                    continue;
                                }
                                this.futureService.processFutureBuyRequest(requestEntry.getValue(), price, optionalUserBuyer.get(), dailyLimit, futureFromTable, balance);
                                futuresRequestsMap.remove(requestEntry.getKey());
                                next = true;
                            } else {
                                keysToRemove.add(requestEntry.getKey());
                            }
                        }
                    }
                }
                next = false;
            }
            // u slucaju da se user izbrisao dodajemo kljuceve tih requestova i brisemo ih
            for (Long key : this.keysToRemove) {
                futuresRequestsMap.remove(key);
            }
            this.keysToRemove.clear();
            Thread.sleep(10000);
        }
    }

    public Map<Long, FutureRequestBuySell> getFuturesRequestsMap() {
        return futuresRequestsMap;
    }

    public void putInFuturesRequestsMap(Long id, FutureRequestBuySell futureRequest) {
        this.futuresRequestsMap.put(id, futureRequest);
    }

    public boolean removeFuture(Long id) {

        if (this.futuresRequestsMap.containsKey(id)) {
            this.futuresRequestsMap.remove(id);
            return false;
        }
        return true;
    }
}

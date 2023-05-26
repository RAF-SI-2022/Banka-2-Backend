package rs.edu.raf.si.bank2.users.services.workerThreads;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.SneakyThrows;
import rs.edu.raf.si.bank2.users.exceptions.BalanceNotFoundException;
import rs.edu.raf.si.bank2.users.models.mariadb.Balance;
import rs.edu.raf.si.bank2.users.models.mariadb.Future;
import rs.edu.raf.si.bank2.users.models.mariadb.User;
import rs.edu.raf.si.bank2.users.repositories.mariadb.OrderRepository;
import rs.edu.raf.si.bank2.users.requests.FutureRequestBuySell;
import rs.edu.raf.si.bank2.users.services.BalanceService;
import rs.edu.raf.si.bank2.users.services.FutureService;
import rs.edu.raf.si.bank2.users.services.UserService;

public class FutureBuyWorker extends Thread {

    private final Map<Long, FutureRequestBuySell> futuresRequestsMap;
    private final List<Long> keysToRemove;
    private List<Future> futuresByName;
    private final FutureService futureService;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final BalanceService balanceService;
    private boolean next = false;

    public FutureBuyWorker(
            FutureService futureService,
            UserService userService,
            OrderRepository orderRepository,
            BalanceService balanceService) {
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
                this.futuresByName = futureService
                        .findFuturesByFutureName(requestEntry.getValue().getFutureName())
                        .get();

                for (Future futureFromTable : futuresByName) {
                    if (next) continue;

                    Optional<User> optionalUserBuyer =
                            userService.findById(requestEntry.getValue().getUserId());
                    Double price = (double) futureFromTable.getMaintenanceMargin();
                    Double dailyLimit = optionalUserBuyer.get().getDailyLimit();
                    if (optionalUserBuyer.isPresent() && optionalUserBuyer.get().getDailyLimit() != null) {
                        if (price > dailyLimit) {
                            continue;
                        }
                    } else {
                        keysToRemove.add(requestEntry.getKey());
                    }
                    if (requestEntry.getValue().getLimit() != 0
                            || requestEntry.getValue().getStop() != 0) { // ako je postavljen limit ili stop
                        if (futureFromTable.isForSale()
                                && (price < requestEntry.getValue().getLimit()
                                        || price > requestEntry.getValue().getStop())) {
                            if (optionalUserBuyer.isPresent()
                                    && optionalUserBuyer.get().getDailyLimit() != null) {
                                Balance balance;
                                try {
                                    balance = this.balanceService.findBalanceByUserEmailAndCurrencyCode(
                                            optionalUserBuyer.get().getEmail(),
                                            requestEntry.getValue().getCurrencyCode());
                                } catch (BalanceNotFoundException e) {
                                    e.printStackTrace();
                                    continue;
                                }
                                this.futureService.processFutureBuyRequest(
                                        requestEntry.getValue(),
                                        price,
                                        optionalUserBuyer.get(),
                                        dailyLimit,
                                        futureFromTable,
                                        balance);
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

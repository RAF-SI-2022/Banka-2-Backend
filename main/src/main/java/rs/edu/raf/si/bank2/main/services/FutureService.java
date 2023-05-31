package rs.edu.raf.si.bank2.main.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.main.exceptions.BalanceNotFoundException;
import rs.edu.raf.si.bank2.main.models.mariadb.*;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.FutureOrder;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.OrderStatus;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.OrderTradeType;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.OrderType;
import rs.edu.raf.si.bank2.main.repositories.mariadb.FutureRepository;
import rs.edu.raf.si.bank2.main.repositories.mariadb.OrderRepository;
import rs.edu.raf.si.bank2.main.requests.FutureRequestBuySell;
import rs.edu.raf.si.bank2.main.services.interfaces.FutureServiceInterface;
import rs.edu.raf.si.bank2.main.services.workerThreads.FutureBuyWorker;
import rs.edu.raf.si.bank2.main.services.workerThreads.FutureSellWorker;

@Service
public class FutureService implements FutureServiceInterface {

    private final UserService userService;
    private final FutureRepository futureRepository;
    private final FutureSellWorker futureSellWorker;
    private final FutureBuyWorker futureBuyWorker;
    private final BalanceService balanceService;
    private final OrderRepository orderRepository;
    private final TransactionService transactionService;

    public FutureService(
            UserService userService,
            FutureRepository futureRepository,
            BalanceService balanceService,
            OrderRepository orderRepository,
            TransactionService transactionService) {
        this.futureRepository = futureRepository;
        this.userService = userService;
        this.balanceService = balanceService;
        this.orderRepository = orderRepository;
        this.transactionService = transactionService;

        futureSellWorker = new FutureSellWorker(this);
        futureBuyWorker = new FutureBuyWorker(this, userService, orderRepository, balanceService);

        futureSellWorker.start();
        futureBuyWorker.start();
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    CacheManager cacheManager;


    @Override
    @Cacheable(value = "futureALL")
    public List<Future> findAll() {

        System.out.println("Getting all futures first time (caching into redis)");
        return futureRepository.findAll();
    }

    @Override
    @Cacheable(value = "futureID", key = "#id")
    public Optional<Future> findById(Long id) {
        System.out.println("Getting future by id first time (caching into redis)");
        if (cacheManager != null) this.clearFindAllCache();
        return futureRepository.findFutureById(id);
    }

    public void clearFindAllCache(){
        cacheManager.getCache("futureALL").clear();
    }
    public void evictAllCaches() {
        cacheManager.getCacheNames().stream().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

    @Override
    @Cacheable(value = "futureByName", key = "#futureName")
    public Optional<List<Future>> findFuturesByFutureName(String futureName) {
        System.out.println("Getting future by name first time (caching into redis)");

        return futureRepository.findFuturesByFutureName(futureName);
    }

    public Future saveFuture(Future future) {
        if (cacheManager != null) this.evictAllCaches();
        return futureRepository.save(future);
    }

    @Override
    public ResponseEntity<?> buyFuture(FutureRequestBuySell futureRequest, String userBuyerEmail, Float usersMoneyInCurrency) {
        if (cacheManager != null) this.evictAllCaches();
        if (futureRequest.getLimit() == 0
                && futureRequest.getStop() == 0) { // regular buy - kupuje se odmah, ne ceka se nista;
            return this.regularBuy(futureRequest, userBuyerEmail, usersMoneyInCurrency);
        }
        futureBuyWorker.putInFuturesRequestsMap(futureRequest.getId(), futureRequest);
        return ResponseEntity.ok().body("Future is set for custom sale and is waiting for trigger.");
    }

    @Override
    public ResponseEntity<?> sellFuture(FutureRequestBuySell futureRequest) {
        if (cacheManager != null) this.evictAllCaches();
        if (futureRequest.getLimit() == 0 && futureRequest.getStop() == 0) {
            return this.regularSell(futureRequest);
        } else {
            futureSellWorker.putInFuturesRequestsMap(futureRequest.getId(), futureRequest);
            return ResponseEntity.ok().body(findById(futureRequest.getId()));
        }
    }

    @Override
    public ResponseEntity<?> removeFromMarket(Long futureId) {
        Optional<Future> future = findById(futureId);
        if (future.isEmpty()) return ResponseEntity.status(500).body("Doslo je do neocekivane greske.");

        if (!future.get().isForSale()) {
            return ResponseEntity.status(500)
                    .body("Ne mozete skinuti terminski ugovor sa prodaje, zato sto ugovor nije na prodaji.");
        }

        future.get().setForSale(false);
        saveFuture(future.get());

        if (!findById(futureId).get().isForSale()) return ResponseEntity.ok().body(findById(futureId));
        return ResponseEntity.status(500).body("Doslo je do neocekivane greske.");
    }

    @Override
    public ResponseEntity<?> removeWaitingSellFuture(Long id) {

        Map<Long, FutureRequestBuySell> mapToSearch = new HashMap<>();
        mapToSearch = futureSellWorker.getFuturesRequestsMap();

        boolean error = true;
        for (Map.Entry<Long, FutureRequestBuySell> future : mapToSearch.entrySet()) {
            if (future.getValue().getId().equals(id)) {

                error = futureSellWorker.removeFuture(id);
                break;
            }
        }

        if (error) {
            return ResponseEntity.status(500).body("Doslo je do neocekivane greske.");
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Uspesno skinut terminski ugovor sa prodaje.");
        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<?> removeWaitingBuyFuture(Long id) {

        Map<Long, FutureRequestBuySell> mapToSearch = new HashMap<>();
        mapToSearch = futureBuyWorker.getFuturesRequestsMap();

        boolean error = true;
        for (Map.Entry<Long, FutureRequestBuySell> future : mapToSearch.entrySet()) {
            if (future.getValue().getId().equals(id)) {

                error = futureBuyWorker.removeFuture(id);
                break;
            }
        }

        if (error) {
            return ResponseEntity.status(500).body("Doslo je do neocekivane greske.");
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Uspesno skinuto cekanje za kupovinu ugovora.");
        return ResponseEntity.ok().body(response);
    }

    @Override
    public List<Long> getWaitingFuturesForUser(Long userId, String type, String futureName) {
        List<Long> futureIdsToReturn = new ArrayList<>();

        Map<Long, FutureRequestBuySell> mapToSearch = new HashMap<>();

        if (type.equals("buy")) mapToSearch = futureBuyWorker.getFuturesRequestsMap();
        else if (type.equals("sell")) mapToSearch = futureSellWorker.getFuturesRequestsMap();

        for (Map.Entry<Long, FutureRequestBuySell> future : mapToSearch.entrySet()) {
            if (future.getValue().getUserId().equals(userId)
                    && future.getValue().getFutureName().equals(futureName)) {
                futureIdsToReturn.add(future.getKey());
            }
        }

        return futureIdsToReturn;
    }

    @Override
    public Optional<List<Future>> findFuturesByUserId(Long id) {
        return futureRepository.findFuturesByUserId(id);
    }

    @Deprecated
    @Override
    public Optional<Future> findByName(String futureName) {
        return futureRepository.findFutureByFutureName(futureName);
    }

    private ResponseEntity<?> regularSell(FutureRequestBuySell futureRequest) {
        Optional<Future> future = futureRepository.findById(futureRequest.getId());
        if (future.isEmpty())
            return ResponseEntity.status(500)
                    .body("Interna serverska greska. Terminski ugovor" + futureRequest.getFutureName()
                            + " nije pronadjen.");
        if (future.get().isForSale())
            return ResponseEntity.status(400)
                    .body("Terminski ugovor" + futureRequest.getFutureName()
                            + " ne moze biti prodat zato sto je vec ranije postavljen na prodaju.");
        future.get().setForSale(true);
        future.get().setMaintenanceMargin(futureRequest.getPrice());
        futureRepository.save(future.get());
        return ResponseEntity.ok().body(findById(futureRequest.getId()));
    }

    private ResponseEntity<?> regularBuy(
            FutureRequestBuySell futureRequest, String userBuyerEmail, Float usersMoneyInCurrency) {
        Optional<Future> future = futureRepository.findById(futureRequest.getId());
        if (future.isEmpty())
            return ResponseEntity.status(500)
                    .body("Interna serverska greska. Terminski ugovor" + futureRequest.getFutureName()
                            + " nije pronadjen.");
        if (!future.get().isForSale())
            return ResponseEntity.status(400)
                    .body("Terminski ugovor" + futureRequest.getFutureName() + " nije na prodaju.");
        Optional<User> userBuyerOptional = this.userService.findByEmail(userBuyerEmail);
        if (userBuyerOptional.isEmpty())
            return ResponseEntity.status(400).body("The buyer, i.e. the user making the purchase has not been found");
        User userBuyer = userBuyerOptional.get();

        // Provera da li user koji kupuje ima dovoljno para
        float price = future.get().getMaintenanceMargin();
        if (usersMoneyInCurrency < price) {
            return ResponseEntity.status(400).body("You don't have enough money in balance.");
        }

        // Provera za daily limit user-a koji kupuje future
        Double limit = userBuyer.getDailyLimit();
        Double priceDouble = (double) price;
        if (limit == null || priceDouble > limit) {
            return ResponseEntity.status(400)
                    .body("User making the purchase exceeded daily limit OR daily limit is not defined.");
        }

        // Trazimo balance da bi mogli da napravimo transakciju
        Balance balance;
        try {
            balance = this.balanceService.findBalanceByUserEmailAndCurrencyCode(
                    userBuyerEmail, futureRequest.getCurrencyCode());
        } catch (BalanceNotFoundException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
        Future processedFuture =
                this.processFutureBuyRequest(futureRequest, priceDouble, userBuyer, limit, future.get(), balance);
        return ResponseEntity.ok().body(processedFuture);
    }

    public FutureOrder createFutureOrder(
            FutureRequestBuySell request,
            Double price,
            User user,
            OrderStatus status,
            OrderTradeType orderTradeType,
            Integer amount) {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        long id = random.nextLong();

        return new FutureOrder(
                id,
                OrderType.FUTURE,
                orderTradeType,
                status,
                request.getFutureName().substring(0, 3),
                amount,
                price,
                this.getTimestamp(),
                user,
                request.getFutureName(),
                request.getStop());
    }

    private String getTimestamp() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentDateTime.format(formatter);
    }

    public Future processFutureBuyRequest(
            FutureRequestBuySell futureRequest,
            Double priceDouble,
            User userBuyer,
            Double limit,
            Future future,
            Balance balance) {
        // Kreiramo order za kupovinu
        FutureOrder order = this.createFutureOrder(
                futureRequest, priceDouble, userBuyer, OrderStatus.IN_PROGRESS, OrderTradeType.BUY, 1);
        order = this.orderRepository.save(
                order); // moramo da sacuvamo ponovo order zato sto mu se u bazi dodeli genericni id koji nam je posle
        // potreban za kreiranje transakcije

        // Update-ovanje daily limita
        userBuyer.setDailyLimit(limit - priceDouble);
        userService.save(userBuyer);

        // Rezervisanje novca za skidanje sa racuna
        balanceService.reserveAmount(priceDouble.floatValue(), userBuyer.getEmail(), futureRequest.getCurrencyCode());

        // Kreiramo transakciju za kupovinu
        Transaction transaction = this.transactionService.createFutureOrderTransaction(
                order, balance, priceDouble.floatValue(), futureRequest, TransactionStatus.IN_PROGRESS);
        this.transactionService.save(transaction);

        User userSeller = future.getUser();
        if (userSeller == null) { // Ako je vlasnik future-a null, to znaci da je vlasnik neka "kompanija" tj. ne radimo
            // exchange novca nego samo skidamo novac sa buyer-ovog balance-a.
            balanceService.decreaseBalance(
                    userBuyer.getEmail(), futureRequest.getCurrencyCode(), priceDouble.floatValue());
        } else { // Ako vlasnik nije null, to znaci da jedan user kupuje future od nekog drugog user-a.
            balanceService.exchangeMoney(
                    userSeller.getEmail(),
                    userBuyer.getEmail(),
                    priceDouble.floatValue(),
                    futureRequest.getCurrencyCode());
        }

        // Setovanje order i transaction statusa na complete
        order.setStatus(OrderStatus.COMPLETE);
        this.orderRepository.save(order);
        transaction.setStatus(TransactionStatus.COMPLETE);
        this.transactionService.save(transaction);

        // Update-ovanje future-a
        future.setUser(userBuyer);
        future.setForSale(false);
        return futureRepository.save(future);
    }
}

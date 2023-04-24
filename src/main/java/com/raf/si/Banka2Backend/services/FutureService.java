package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.models.mariadb.Future;
import com.raf.si.Banka2Backend.models.mariadb.User;
import com.raf.si.Banka2Backend.repositories.mariadb.FutureRepository;
import com.raf.si.Banka2Backend.requests.FutureRequestBuySell;
import com.raf.si.Banka2Backend.services.interfaces.FutureServiceInterface;
import com.raf.si.Banka2Backend.services.workerThreads.FutureBuyWorker;
import com.raf.si.Banka2Backend.services.workerThreads.FutureSellWorker;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FutureService implements FutureServiceInterface {

    private final UserService userService;
    private final FutureRepository futureRepository;
    private final FutureSellWorker futureSellWorker;
    private final FutureBuyWorker futureBuyWorker;
    private final BalanceService balanceService;

    public FutureService(
            UserService userService, FutureRepository futureRepository, BalanceService balanceService) {
        this.futureRepository = futureRepository;
        this.userService = userService;
        this.balanceService = balanceService;
        futureSellWorker = new FutureSellWorker(this);
        futureBuyWorker = new FutureBuyWorker(this, userService);

        futureSellWorker.start();
        futureBuyWorker.start();
    }

    @Override
    public List<Future> findAll() {
        return futureRepository.findAll();
    }

    @Override
    public Optional<Future> findById(Long id) {
        return futureRepository.findFutureById(id);
    }

    @Override
    public Optional<List<Future>> findFuturesByFutureName(String futureName) {
        return futureRepository.findFuturesByFutureName(futureName);
    }

    @Override
    public ResponseEntity<?> buyFuture(FutureRequestBuySell futureRequest, String fromUserEmail, Float usersMoneyInCurrency) {
        System.out.println(futureRequest);
        if (futureRequest.getLimit() == 0 && futureRequest.getStop() == 0) { // regularni buy
            Optional<Future> future = futureRepository.findById(futureRequest.getId());
            if (future.isEmpty()) return ResponseEntity.status(500).body("Internal server error ovaj");
            if (!future.get().isForSale())
                return ResponseEntity.status(500).body("Internal server error");

            User toUser = future.get().getUser();
            if (toUser != null) { // provera da li user ima dovoljno para //todo trenutno je sve preko USD
                float amount = future.get().getMaintenanceMargin();
                if (usersMoneyInCurrency < amount)
                    return ResponseEntity.status(500).body("Not enough money in balance");

                // Provera za daily limit
//            System.out.println("Ovde sam");
                Optional<User> optionalUser = userService.findByEmail(fromUserEmail);
                if(optionalUser.isPresent()) {
//                System.out.println("Sad sam ovde");
//                    float amount = future.get().getMaintenanceMargin();
                    Double limit = optionalUser.get().getDailyLimit();
                    Double amountDouble = Double.valueOf(amount);
                    Double suma = limit-amountDouble;
//                    System.out.println("Limit " + limit + " vrednost " + amountDouble + " oduzimanje " + suma);
                    boolean limitTestBoolean = limit-amountDouble < 0?false:true;
                    if (!limitTestBoolean)
                        return ResponseEntity.status(500).body("Exceeded daily limit");
                    else {
                        optionalUser.get().setDailyLimit(limit-amountDouble);
                        userService.save(optionalUser.get());
                    }
                }

                balanceService.exchangeMoney(fromUserEmail, toUser.getEmail(), amount, "USD");
            }

            // Provera za daily limit
            Optional<User> optionalUser = userService.findByEmail(fromUserEmail);
            if(optionalUser.isPresent() && !(optionalUser.get().getDailyLimit() == null)) {

                float amount = future.get().getMaintenanceMargin();
                Double limit = optionalUser.get().getDailyLimit();
                Double amountDouble = Double.valueOf(amount);
                Double suma = limit-amountDouble;
//                System.out.println("Limit " + limit + " vrednost " + amountDouble + " oduzimanje " + suma);
                boolean limitTestBoolean = limit-amountDouble < 0?false:true;
                if (!limitTestBoolean)
                    return ResponseEntity.status(500).body("Exceeded daily limit");
                else {
                    optionalUser.get().setDailyLimit(limit-amountDouble);
                    userService.save(optionalUser.get());
                }
            }

            future.get().setUser(userService.findById(futureRequest.getUserId()).get());
            future.get().setForSale(false);
            futureRepository.save(future.get());
            return ResponseEntity.ok().body(findById(futureRequest.getId()));
        } else {
            futureBuyWorker.setFuturesRequestsMap(futureRequest.getId(), futureRequest);
            return ResponseEntity.ok().body("Future is set for custom sale and is waiting for trigger");
        }
    }

    public void updateFuture(Future future) {
        futureRepository.save(future);
    }

    @Override
    public ResponseEntity<?> sellFuture(FutureRequestBuySell futureRequest) {
        if (futureRequest.getLimit() == 0 && futureRequest.getStop() == 0) {
            Optional<Future> future = futureRepository.findById(futureRequest.getId());
            if (future.isEmpty()) return ResponseEntity.status(500).body("Internal server error");
            if (future.get().isForSale()) return ResponseEntity.status(500).body("Internal server error");
            future.get().setForSale(true);
            future.get().setMaintenanceMargin(futureRequest.getPrice());
            futureRepository.save(future.get());
            return ResponseEntity.ok().body(findById(futureRequest.getId()));
        } else {
            futureSellWorker.setFuturesRequestsMap(futureRequest.getId(), futureRequest);
            return ResponseEntity.ok().body(findById(futureRequest.getId()));
        }
    }

    @Override
    public ResponseEntity<?> removeFromMarket(Long futureId) {
        Optional<Future> future = findById(futureId);
        if (future.isEmpty()) return ResponseEntity.status(500).body("Internal server error");

        if (!future.get().isForSale()) {
            return ResponseEntity.status(500).body("This isnt for sale");
        }

        future.get().setForSale(false);
        updateFuture(future.get());

        if (!findById(futureId).get().isForSale()) return ResponseEntity.ok().body(findById(futureId));
        return ResponseEntity.status(500).body("Internal server error");
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
            return ResponseEntity.status(500).body("Internal server error");
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Uspesno skinut");
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
            return ResponseEntity.status(500).body("Internal server error");
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Uspesno skinut");
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

    @Deprecated
    @Override
    public Optional<Future> findByName(String futureName) {
        return futureRepository.findFutureByFutureName(futureName);
    }
}

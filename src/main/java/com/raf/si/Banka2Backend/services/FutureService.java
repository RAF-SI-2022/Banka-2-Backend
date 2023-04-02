package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.models.mariadb.Future;
import com.raf.si.Banka2Backend.repositories.mariadb.FutureRepository;
import com.raf.si.Banka2Backend.requests.FutureRequestBuySell;
import com.raf.si.Banka2Backend.services.interfaces.FutureServiceInterface;
import com.raf.si.Banka2Backend.services.workerThreads.FutureBuyWorker;
import com.raf.si.Banka2Backend.services.workerThreads.FutureSellWorker;

import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FutureService implements FutureServiceInterface {

  private UserService userService;
  private FutureRepository futureRepository;
  private FutureSellWorker futureSellWorker;
  private FutureBuyWorker futureBuyWorker;

  public FutureService(UserService userService, FutureRepository futureRepository) {
    this.futureRepository = futureRepository;
    this.userService = userService;
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
  public ResponseEntity<?> buyFuture(
      FutureRequestBuySell futureRequest) { // todo ako se kupuje od druge osobe razmeni pare
    if (futureRequest.getLimit() == 0 && futureRequest.getStop() == 0) { // regularni buy
      Optional<Future> future = futureRepository.findById(futureRequest.getId());
      if (future.isEmpty()) return ResponseEntity.status(500).body("Internal server error");
      if(!future.get().isForSale()) return ResponseEntity.status(500).body("Internal server error");
      future.get().setUser(userService.findById(futureRequest.getUserId()).get());
      future.get().setForSale(false);
      futureRepository.save(future.get());
      return ResponseEntity.ok().body(findById(futureRequest.getId()));
    } else {
      futureBuyWorker.getFuturesRequestsMap().put(futureRequest.getId(), futureRequest);
      return ResponseEntity.ok().body("Future is set for custom sale and is waiting for trigger");
    }
  }

  public void updateFuture(Future future) {
    futureRepository.save(future);
  }

  @Override
  public ResponseEntity<?> sellFuture(
      FutureRequestBuySell futureRequest) { // todo ako se kupuje od druge osobe razmeni pare
    if (futureRequest.getLimit() == 0 && futureRequest.getStop() == 0) {
      Optional<Future> future = futureRepository.findById(futureRequest.getId());
      if (future.isEmpty()) return ResponseEntity.status(500).body("Internal server error");
      if(future.get().isForSale()) return ResponseEntity.status(500).body("Internal server error");
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
    if(future.isEmpty()) return ResponseEntity.status(500).body("Internal server error");

    if(!future.get().isForSale()){
      return ResponseEntity.status(500).body("This isnt for sale");
    }

    future.get().setForSale(false);
    updateFuture(future.get());

    if (!findById(futureId).get().isForSale())
      return ResponseEntity.ok().body(findById(futureId));
    return ResponseEntity.status(500).body("Internal server error");
  }

  @Override
  public List<Long> getWaitingFuturesForUser(Long userId, String type, String futureName) {
    List<Long> futureIdsToReturn = new ArrayList<>();

    Map<Long,FutureRequestBuySell> mapToSearch = new HashMap<>();

    if (type.equals("buy")) mapToSearch = futureBuyWorker.getFuturesRequestsMap();
    else if (type.equals("sell")) mapToSearch = futureSellWorker.getFuturesRequestsMap();


    for (Map.Entry<Long, FutureRequestBuySell> future: mapToSearch.entrySet()) {
      if (future.getValue().getUserId().equals(userId) && future.getValue().getFutureName().equals(futureName)){
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

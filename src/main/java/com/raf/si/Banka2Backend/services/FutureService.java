package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.models.mariadb.Future;
import com.raf.si.Banka2Backend.repositories.mariadb.FutureRepository;
import com.raf.si.Banka2Backend.requests.FutureRequestBuySell;
import com.raf.si.Banka2Backend.services.interfaces.FutureServiceInterface;
import com.raf.si.Banka2Backend.services.workerThreads.FutureSellWorker;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FutureService implements FutureServiceInterface {

  private FutureRepository futureRepository;
  private UserService userService;
  private FutureSellWorker futureSellWorker;

  public FutureService(UserService userService, FutureRepository futureRepository) {
    this.futureRepository = futureRepository;
    this.userService = userService;
    futureSellWorker = new FutureSellWorker(this);
    futureSellWorker.start();
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
  public ResponseEntity<?> buyFuture(FutureRequestBuySell futureRequest) {
    if (futureRequest.getLimit() == 0 && futureRequest.getStop() == 0) { // regularni buy
      Optional<Future> future = futureRepository.findById(futureRequest.getId());

      if (future.isPresent()) {
        future.get().setUser(userService.findById(futureRequest.getUserId()).get());
        future.get().setForSale(false);
        futureRepository.save(future.get());
      } else {
        System.out.println("custom buy");
        // todo
      }
    }

    return null; // todo return future
  }

  public void updateFuture(Future future) {
    futureRepository.save(future);
  }

  @Override
  public ResponseEntity<?> sellFuture(FutureRequestBuySell futureRequest) {
    if (futureRequest.getLimit() == 0 && futureRequest.getStop() == 0) {
      Optional<Future> future = futureRepository.findById(futureRequest.getId());
      future.get().setForSale(true);
      future.get().setMaintenanceMargin(futureRequest.getPrice());
      futureRepository.save(future.get());
      return ResponseEntity.ok().body("Future is set for sale");
    } else {
      futureSellWorker.getFuturesRequests().add(futureRequest);
      return ResponseEntity.ok().body("Future is set for custom sale and is waiting for trigger");
    }
  }

  @Override
  public ResponseEntity<?> removeFromMarket(Long futureId) {
    Optional<Future> future = findById(futureId);
    future.get().setForSale(false);
    updateFuture(future.get());

    if (!findById(futureId).get().isForSale())
      return ResponseEntity.ok().body("Removed from market");
    return ResponseEntity.status(500).body("Internal server error");
  }

  @Deprecated
  @Override
  public Optional<Future> findByName(String futureName) {
    return futureRepository.findFutureByFutureName(futureName);
  }
}

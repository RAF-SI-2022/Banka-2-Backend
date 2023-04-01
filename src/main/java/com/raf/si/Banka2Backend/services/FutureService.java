package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.models.mariadb.Future;
import com.raf.si.Banka2Backend.repositories.mariadb.FutureRepository;
import com.raf.si.Banka2Backend.requests.FutureRequestBuySell;
import com.raf.si.Banka2Backend.services.interfaces.FutureServiceInterface;
import com.raf.si.Banka2Backend.services.workerThreads.FutureSellWorker;
import java.util.List;
import java.util.Optional;
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
    //    futureWorker.start();
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
  public Optional<Future> buyFuture(FutureRequestBuySell futureRequest) {
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

    return Optional.empty(); // todo return future
  }

  @Override
  public Optional<Future> sellFuture(FutureRequestBuySell futureRequest) {

    return Optional.empty();
  }

  @Deprecated
  @Override
  public Optional<Future> findByName(String futureName) {
    return futureRepository.findFutureByFutureName(futureName);
  }
}

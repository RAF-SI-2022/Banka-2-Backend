package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.models.mariadb.Future;
import com.raf.si.Banka2Backend.repositories.mariadb.FutureRepository;
import com.raf.si.Banka2Backend.requests.FutureRequestBuySell;
import com.raf.si.Banka2Backend.services.interfaces.FutureServiceInterface;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class FutureService implements FutureServiceInterface {

  private FutureRepository futureRepository;
  private UserService userService;

  public FutureService(UserService userService, FutureRepository futureRepository) {
    this.futureRepository = futureRepository;
    this.userService = userService;
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
  public Optional<Future> buySellFuture(FutureRequestBuySell futureRequest) {
    Optional<Future> future = futureRepository.findById(futureRequest.getId());

    if (futureRequest.getType().equals("BUY")){//todo ovo promeni, trenutno postavljeno divljacki radi testiranja
      future.get().setUser(userService.findById(futureRequest.getUserId()).get());
      futureRepository.save(future.get());

    }
    else if (futureRequest.getType().equals("SELL")){
      future.get().setUser(null);
      futureRepository.save(future.get());
    }
    return future;
  }

  @Deprecated
  @Override
  public Optional<Future> findByName(String futureName) {
    return futureRepository.findFutureByFutureName(futureName);
  }

}

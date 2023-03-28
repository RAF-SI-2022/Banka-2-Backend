package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.models.mariadb.Future;
import com.raf.si.Banka2Backend.repositories.mariadb.FutureRepository;
import com.raf.si.Banka2Backend.services.interfaces.FutureServiceInterface;
import java.util.List;
import java.util.Optional;

public class FutureService implements FutureServiceInterface {

  private FutureRepository futureRepository;

  public FutureService(FutureRepository futureRepository) {
    this.futureRepository = futureRepository;
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
  public Optional<Future> findByName(String futureName) {
    return futureRepository.findFutureByFutureName(futureName);
  }
}

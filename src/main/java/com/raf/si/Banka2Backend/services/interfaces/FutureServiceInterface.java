package com.raf.si.Banka2Backend.services.interfaces;

import com.raf.si.Banka2Backend.models.mariadb.Future;
import java.util.List;
import java.util.Optional;

public interface FutureServiceInterface {

  List<Future> findAll();

  Optional<Future> findById(Long Id);

  Optional<Future> findByName(String contractName);
}

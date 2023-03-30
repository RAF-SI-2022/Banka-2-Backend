package com.raf.si.Banka2Backend.services.interfaces;

import com.raf.si.Banka2Backend.models.mariadb.Future;
import com.raf.si.Banka2Backend.requests.FutureRequestBuySell;
import java.util.List;
import java.util.Optional;

public interface FutureServiceInterface {

  List<Future> findAll();

  Optional<Future> findById(Long Id);

  Optional<List<Future>> findFuturesByFutureName(String futureName);

  Optional<Future> buySellFuture(FutureRequestBuySell futureRequest);

  @Deprecated
  Optional<Future> findByName(String contractName);
}

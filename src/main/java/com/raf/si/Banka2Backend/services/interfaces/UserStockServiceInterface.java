package com.raf.si.Banka2Backend.services.interfaces;

import com.raf.si.Banka2Backend.models.mariadb.UserStock;
import java.util.List;
import java.util.Optional;

public interface UserStockServiceInterface {

  Optional<UserStock> findUserStockByUserIdAndStockSymbol(long userId, String stockSymbol);

  UserStock save(UserStock userStock);

  List<UserStock> findAll();

  UserStock removeFromMarket(long userId, String stockSymbol);
}

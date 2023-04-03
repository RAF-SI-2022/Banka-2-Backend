package com.raf.si.Banka2Backend.repositories.mariadb;

import com.raf.si.Banka2Backend.models.mariadb.UserStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStocksRepository extends JpaRepository<UserStock, Long> {

    Optional<UserStock> findUserStockByUserIdAndStockSymbol(long userId, String stockSymbol);


}

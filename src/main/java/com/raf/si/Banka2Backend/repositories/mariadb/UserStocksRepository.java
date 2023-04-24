package com.raf.si.Banka2Backend.repositories.mariadb;

import com.raf.si.Banka2Backend.models.mariadb.UserStock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStocksRepository extends JpaRepository<UserStock, Long> {

    Optional<UserStock> findUserStockByUserIdAndStockSymbol(long userId, String stockSymbol);

    List<UserStock> findUserStocksByUserId(long userId);
}

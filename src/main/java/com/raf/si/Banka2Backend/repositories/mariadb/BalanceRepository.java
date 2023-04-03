package com.raf.si.Banka2Backend.repositories.mariadb;

import com.raf.si.Banka2Backend.models.mariadb.Balance;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
  Optional<Balance> findBalanceByUser_EmailAndCurrency_CurrencyCode(
      String userEmail, String currencyCode);

  List<Balance> findAllByUser_Id(Long userId);

  Optional<Balance> findBalanceByUserIdAndCurrencyId(Long userId, Long currencyId);
}

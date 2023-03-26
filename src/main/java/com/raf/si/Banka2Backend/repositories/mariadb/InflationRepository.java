package com.raf.si.Banka2Backend.repositories.mariadb;

import com.raf.si.Banka2Backend.models.mariadb.Inflation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InflationRepository extends JpaRepository<Inflation, Long> {
  List<Inflation> findAllByCurrencyId(Long currencyId);

  List<Inflation> findAllByCurrencyIdAndYear(Long currencyId, int year);
}

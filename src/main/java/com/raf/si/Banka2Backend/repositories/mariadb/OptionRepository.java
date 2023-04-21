package com.raf.si.Banka2Backend.repositories.mariadb;

import com.raf.si.Banka2Backend.models.mariadb.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findAllByStockSymbol(String symbol);

    List<Option> findAllByStockSymbolAndExpirationDate(String symbol, LocalDate expirationDate);
}

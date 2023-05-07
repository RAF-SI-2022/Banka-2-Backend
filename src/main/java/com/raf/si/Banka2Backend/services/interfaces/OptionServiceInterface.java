package com.raf.si.Banka2Backend.services.interfaces;

import com.raf.si.Banka2Backend.models.mariadb.Option;
import java.util.List;
import java.util.Optional;

public interface OptionServiceInterface {

    List<Option> findAll();

    Option save(Option option);

    Optional<Option> findById(Long id);

    List<Option> findByStock(String stockSymbol);

    List<Option> findByStockAndDate(String stockSymbol, String dateMils);
}

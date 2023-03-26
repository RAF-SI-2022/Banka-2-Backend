package com.raf.si.Banka2Backend.services.interfaces;

import com.raf.si.Banka2Backend.models.mariadb.Inflation;
import java.util.List;
import java.util.Optional;

public interface InflationServiceInterface {

  Optional<List<Inflation>> findAllByCurrencyId(Long currencyId);

  Optional<List<Inflation>> findByYear(Long currencyId, Integer year);

  Inflation save(Inflation inflation);
}

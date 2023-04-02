package com.raf.si.Banka2Backend.services.interfaces;

import com.raf.si.Banka2Backend.models.mariadb.Currency;
import java.util.List;
import java.util.Optional;

public interface CurrencyServiceInterface {
  List<Currency> findAll();

  Optional<Currency> findById(Long currencyId);

  Optional<Currency> findByCurrencyCode(String currencyCode);

  Optional<Currency> findCurrencyByCurrencyCode(String currencyCode);
}

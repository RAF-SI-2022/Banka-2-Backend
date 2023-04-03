package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.exceptions.CurrencyNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.Currency;
import com.raf.si.Banka2Backend.repositories.mariadb.CurrencyRepository;
import com.raf.si.Banka2Backend.services.interfaces.CurrencyServiceInterface;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService implements CurrencyServiceInterface {
  private final CurrencyRepository currencyRepository;

  @Autowired
  public CurrencyService(CurrencyRepository currencyRepository) {
    this.currencyRepository = currencyRepository;
  }

  @Override
  public List<Currency> findAll() {
    return this.currencyRepository.findAll();
  }

  @Override
  public Optional<Currency> findById(Long currencyId) {
    Optional<Currency> currency = this.currencyRepository.findById(currencyId);
    if (currency.isPresent()) {
      return currency;
    } else {
      throw new CurrencyNotFoundException(currencyId);
    }
  }

  @Override
  public Optional<Currency> findByCurrencyCode(String currencyCode) {
    Optional<Currency> currency = this.currencyRepository.findCurrencyByCurrencyCode(currencyCode);
    if (currency.isPresent()) {
      return currency;
    } else {
      throw new CurrencyNotFoundException(currencyCode);
    }
  }

  @Override
  public Optional<Currency> findCurrencyByCurrencyCode(String currencyCode) {
    return currencyRepository.findCurrencyByCurrencyCode(currencyCode);
  }
}

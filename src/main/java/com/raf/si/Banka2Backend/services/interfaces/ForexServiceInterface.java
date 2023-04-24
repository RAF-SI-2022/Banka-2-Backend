package com.raf.si.Banka2Backend.services.interfaces;

import com.raf.si.Banka2Backend.models.mariadb.Forex;
import java.util.List;

public interface ForexServiceInterface {

    List<Forex> findAll();

    Forex getForexForCurrencies(String fromCurrency, String toCurrency);
}

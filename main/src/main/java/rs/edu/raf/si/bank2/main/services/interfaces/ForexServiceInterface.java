package rs.edu.raf.si.bank2.main.services.interfaces;

import rs.edu.raf.si.bank2.main.models.mariadb.Forex;

import java.util.List;

public interface ForexServiceInterface {

    List<Forex> findAll();

    Forex getForexForCurrencies(String fromCurrency, String toCurrency);
}

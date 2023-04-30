package rs.edu.raf.si.bank2.securities.services.interfaces;

import java.util.List;
import rs.edu.raf.si.bank2.securities.models.mariadb.Forex;

public interface ForexServiceInterface {

    List<Forex> findAll();

    Forex getForexForCurrencies(String fromCurrency, String toCurrency);
}

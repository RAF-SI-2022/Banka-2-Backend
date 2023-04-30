package rs.edu.raf.si.bank2.Bank2Backend.services.interfaces;

import java.util.List;
import rs.edu.raf.si.bank2.Bank2Backend.models.mariadb.Forex;

public interface ForexServiceInterface {

    List<Forex> findAll();

    Forex getForexForCurrencies(String fromCurrency, String toCurrency);
}

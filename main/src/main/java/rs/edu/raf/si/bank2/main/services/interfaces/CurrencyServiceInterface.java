package rs.edu.raf.si.bank2.main.services.interfaces;

import rs.edu.raf.si.bank2.main.models.mariadb.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyServiceInterface {
    List<Currency> findAll();

    Optional<Currency> findById(Long currencyId);

    Optional<Currency> findByCurrencyCode(String currencyCode);

    Optional<Currency> findCurrencyByCurrencyCode(String currencyCode);
}

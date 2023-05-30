package rs.edu.raf.si.bank2.otc.services.interfaces;

import java.util.List;
import java.util.Optional;
import rs.edu.raf.si.bank2.otc.models.mariadb.Currency;

public interface CurrencyServiceInterface {
    List<Currency> findAll();

    Optional<Currency> findById(Long currencyId);

    Optional<Currency> findByCurrencyCode(String currencyCode);

    Optional<Currency> findCurrencyByCurrencyCode(String currencyCode);
}

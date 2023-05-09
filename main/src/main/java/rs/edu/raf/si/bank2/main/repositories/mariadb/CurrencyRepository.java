package rs.edu.raf.si.bank2.main.repositories.mariadb;

import rs.edu.raf.si.bank2.main.models.mariadb.Currency;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findCurrencyByCurrencyName(String currencyName);

    Optional<Currency> findCurrencyByCurrencyCode(String currencyCode);
}

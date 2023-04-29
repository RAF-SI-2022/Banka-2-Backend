package rs.edu.raf.si.bank2.Bank2Backend.repositories.mariadb;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.Bank2Backend.models.mariadb.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findCurrencyByCurrencyName(String currencyName);

    Optional<Currency> findCurrencyByCurrencyCode(String currencyCode);
}

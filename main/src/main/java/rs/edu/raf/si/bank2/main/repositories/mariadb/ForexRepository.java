package rs.edu.raf.si.bank2.main.repositories.mariadb;

import rs.edu.raf.si.bank2.main.models.mariadb.Forex;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForexRepository extends JpaRepository<Forex, Long> {

    Optional<Forex> findForexByFromCurrencyCodeAndToCurrencyCode(String fromCurrencyCode, String toCurrencyCode);
}

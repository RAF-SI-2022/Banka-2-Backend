package rs.edu.raf.si.bank2.users.repositories.mariadb;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.users.models.mariadb.Forex;

@Repository
public interface ForexRepository extends JpaRepository<Forex, Long> {

    Optional<Forex> findForexByFromCurrencyCodeAndToCurrencyCode(String fromCurrencyCode, String toCurrencyCode);
}

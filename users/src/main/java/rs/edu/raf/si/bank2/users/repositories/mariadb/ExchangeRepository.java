package rs.edu.raf.si.bank2.users.repositories.mariadb;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.edu.raf.si.bank2.users.models.mariadb.Exchange;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

    Optional<Exchange> findExchangeByAcronym(String acronym);

    Optional<Exchange> findExchangeByMicCode(String micCode);
}

package rs.edu.raf.si.bank2.Bank2Backend.services.interfaces;

import java.util.List;
import java.util.Optional;
import rs.edu.raf.si.bank2.Bank2Backend.models.mariadb.Exchange;

public interface ExchangeServiceInterface {

    List<Exchange> findAll();

    Optional<Exchange> findById(Long id);

    Optional<Exchange> findByMicCode(String micCode);

    Optional<Exchange> findByAcronym(String acronym);

    boolean isExchangeActive(String micCode);
}

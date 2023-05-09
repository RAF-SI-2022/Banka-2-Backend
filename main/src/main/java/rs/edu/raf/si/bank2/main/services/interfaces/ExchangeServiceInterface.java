package rs.edu.raf.si.bank2.main.services.interfaces;

import rs.edu.raf.si.bank2.main.models.mariadb.Exchange;

import java.util.List;
import java.util.Optional;

public interface ExchangeServiceInterface {

    List<Exchange> findAll();

    Optional<Exchange> findById(Long id);

    Optional<Exchange> findByMicCode(String micCode);

    Optional<Exchange> findByAcronym(String acronym);

    boolean isExchangeActive(String micCode);
}

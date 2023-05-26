package rs.edu.raf.si.bank2.users.services.interfaces;

import java.util.List;
import rs.edu.raf.si.bank2.users.models.mariadb.Exchange;

public interface ExchangeServiceInterface {

    List<Exchange> findAll();

    Exchange findById(Long id);

    Exchange findByMicCode(String micCode);

    Exchange findByAcronym(String acronym);

    boolean isExchangeActive(String micCode);
}

package rs.edu.raf.si.bank2.securities.services;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.securities.exceptions.ExchangeNotFoundException;
import rs.edu.raf.si.bank2.securities.models.mariadb.Exchange;
import rs.edu.raf.si.bank2.securities.repositories.mariadb.ExchangeRepository;
import rs.edu.raf.si.bank2.securities.services.interfaces.ExchangeServiceInterface;

@Service
public class ExchangeService implements ExchangeServiceInterface {

    private ExchangeRepository exchangeRepository;
    private UserService userService;

    @Autowired
    public ExchangeService(ExchangeRepository exchangeRepository, UserService userService) {
        this.exchangeRepository = exchangeRepository;
        this.userService = userService;
    }

    @Override
    public List<Exchange> findAll() {
        return exchangeRepository.findAll();
    }

    @Override
    public Optional<Exchange> findById(Long id) {

        Optional<Exchange> exchange = exchangeRepository.findById(id);
        if (exchange.isPresent()) {
            return exchange;
        } else {
            throw new ExchangeNotFoundException();
        }
    }

    @Override
    public Optional<Exchange> findByMicCode(String micCode) {
        Optional<Exchange> exchange = exchangeRepository.findExchangeByMicCode(micCode);
        if (exchange.isPresent()) {
            return exchange;
        } else {
            throw new ExchangeNotFoundException();
        }
    }

    @Override
    public Optional<Exchange> findByAcronym(String acronym) {
        Optional<Exchange> exchange = exchangeRepository.findExchangeByAcronym(acronym);
        if (exchange.isPresent()) {
            return exchange;
        } else {
            throw new ExchangeNotFoundException();
        }
    }

    @Override
    public boolean isExchangeActive(String micCode) {

        Optional<Exchange> exchangeEntry = exchangeRepository.findExchangeByMicCode(micCode);
        if (exchangeEntry.isEmpty()) {
            throw new ExchangeNotFoundException();
        }
        Exchange exchange = exchangeEntry.get();
        LocalTime openTime = LocalTime.parse(exchange.getOpenTime().substring(1));
        LocalTime closeTime = LocalTime.parse(exchange.getCloseTime().substring(1));
        ZoneId zone = ZoneId.of(exchange.getTimeZone());
        LocalTime currTime = LocalTime.now(zone);
        return !currTime.isBefore(openTime) && !currTime.isAfter(closeTime);
    }
}

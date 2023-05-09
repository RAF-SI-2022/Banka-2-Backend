package rs.edu.raf.si.bank2.main.services;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.main.exceptions.CurrencyNotFoundException;
import rs.edu.raf.si.bank2.main.models.mariadb.Currency;
import rs.edu.raf.si.bank2.main.repositories.mariadb.CurrencyRepository;
import rs.edu.raf.si.bank2.main.services.interfaces.CurrencyServiceInterface;

@Service
public class CurrencyService implements CurrencyServiceInterface {
    private final CurrencyRepository currencyRepository;

    @Autowired
    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Override
    public List<Currency> findAll() {
        return this.currencyRepository.findAll();
    }

    @Override
    public Optional<Currency> findById(Long currencyId) {
        Optional<Currency> currency = this.currencyRepository.findById(currencyId);
        if (currency.isPresent()) {
            return currency;
        } else {
            throw new CurrencyNotFoundException(currencyId);
        }
    }

    @Override
    public Optional<Currency> findByCurrencyCode(String currencyCode) {
        Optional<Currency> currency = this.currencyRepository.findCurrencyByCurrencyCode(currencyCode);
        if (currency.isPresent()) {
            return currency;
        } else {
            throw new CurrencyNotFoundException(currencyCode);
        }
    }

    @Override
    public Optional<Currency> findCurrencyByCurrencyCode(String currencyCode) {
        return currencyRepository.findCurrencyByCurrencyCode(currencyCode);
    }
}

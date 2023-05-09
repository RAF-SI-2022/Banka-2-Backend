package rs.edu.raf.si.bank2.main.services;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.main.models.mariadb.Inflation;
import rs.edu.raf.si.bank2.main.repositories.mariadb.InflationRepository;
import rs.edu.raf.si.bank2.main.services.interfaces.InflationServiceInterface;

@Service
public class InflationService implements InflationServiceInterface {
    private final InflationRepository inflationRepository;

    @Autowired
    public InflationService(InflationRepository inflationRepository) {
        this.inflationRepository = inflationRepository;
    }

    @Override
    public Optional<List<Inflation>> findAllByCurrencyId(Long currencyId) {
        return Optional.ofNullable(this.inflationRepository.findAllByCurrencyId(currencyId));
    }

    @Override
    public Optional<List<Inflation>> findByYear(Long currencyId, Integer year) {
        return Optional.ofNullable(this.inflationRepository.findAllByCurrencyIdAndYear(currencyId, year));
    }

    @Override
    public Inflation save(Inflation inflation) {
        return this.inflationRepository.save(inflation);
    }
}

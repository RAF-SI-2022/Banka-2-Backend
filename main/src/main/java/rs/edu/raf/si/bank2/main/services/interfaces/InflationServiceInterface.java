package rs.edu.raf.si.bank2.main.services.interfaces;

import rs.edu.raf.si.bank2.main.models.mariadb.Inflation;

import java.util.List;
import java.util.Optional;

public interface InflationServiceInterface {

    Optional<List<Inflation>> findAllByCurrencyId(Long currencyId);

    Optional<List<Inflation>> findByYear(Long currencyId, Integer year);

    Inflation save(Inflation inflation);
}

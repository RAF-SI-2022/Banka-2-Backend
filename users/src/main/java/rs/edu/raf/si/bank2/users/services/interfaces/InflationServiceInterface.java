package rs.edu.raf.si.bank2.users.services.interfaces;

import java.util.List;
import java.util.Optional;
import rs.edu.raf.si.bank2.users.models.mariadb.Inflation;

public interface InflationServiceInterface {

    Optional<List<Inflation>> findAllByCurrencyId(Long currencyId);

    Optional<List<Inflation>> findByYear(Long currencyId, Integer year);

    Inflation save(Inflation inflation);
}

package rs.edu.raf.si.bank2.securities.repositories.mariadb;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.securities.models.mariadb.Inflation;

@Repository
public interface InflationRepository extends JpaRepository<Inflation, Long> {
    List<Inflation> findAllByCurrencyId(Long currencyId);

    List<Inflation> findAllByCurrencyIdAndYear(Long currencyId, int year);
}

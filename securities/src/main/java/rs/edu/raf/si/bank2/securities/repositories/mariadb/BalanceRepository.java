package rs.edu.raf.si.bank2.securities.repositories.mariadb;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.securities.models.mariadb.Balance;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findBalanceByUser_EmailAndCurrency_CurrencyCode(String userEmail, String currencyCode);

    List<Balance> findAllByUser_Id(Long userId);

    Optional<Balance> findBalanceByUserIdAndCurrencyId(Long userId, Long currencyId);
}

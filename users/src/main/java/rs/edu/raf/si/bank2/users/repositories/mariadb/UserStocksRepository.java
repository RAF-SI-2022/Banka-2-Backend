package rs.edu.raf.si.bank2.users.repositories.mariadb;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.users.models.mariadb.UserStock;

@Repository
public interface UserStocksRepository extends JpaRepository<UserStock, Long> {

    Optional<UserStock> findUserStockByUserIdAndStockSymbol(long userId, String stockSymbol);

    List<UserStock> findUserStocksByUserId(long userId);
}

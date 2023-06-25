package rs.edu.raf.si.bank2.main.repositories.mariadb;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.main.models.mariadb.UserStock;

@Repository
public interface UserStocksRepository extends JpaRepository<UserStock, Long> {

    Optional<UserStock> findUserStockByUserIdAndStockSymbol(long userId, String stockSymbol);

    Optional<UserStock> findUserStockByUserIdAndStockId(Long userId, Long stockId);

    Optional<UserStock> findUserStockById(Long stockId);

    List<UserStock> findUserStocksByUserId(long userId);

    Optional<UserStock> findByIdAndStockId(Long id, Long userId);
}

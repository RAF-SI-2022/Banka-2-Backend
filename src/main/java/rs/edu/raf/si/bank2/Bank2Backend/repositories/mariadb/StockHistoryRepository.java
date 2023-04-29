package rs.edu.raf.si.bank2.Bank2Backend.repositories.mariadb;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import rs.edu.raf.si.bank2.Bank2Backend.models.mariadb.StockHistory;
import rs.edu.raf.si.bank2.Bank2Backend.models.mariadb.StockHistoryType;

@Repository
public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {

    @Query(
            value =
                    "SELECT * FROM (SELECT * FROM stock_history s WHERE s.stock_id = :id AND s.type = 'DAILY' ORDER BY s.on_date DESC LIMIT :period) sh ORDER BY sh.on_date ASC",
            nativeQuery = true)
    List<StockHistory> getStockHistoryByStockIdAndHistoryType(Long id, Integer period);

    @Query(
            value =
                    "SELECT * FROM (SELECT * FROM stock_history s WHERE s.stock_id = :id AND s.type = 'DAILY' AND year(on_date) = year(CURDATE()) ORDER BY s.on_date DESC) sh ORDER BY sh.on_date ASC",
            nativeQuery = true)
    List<StockHistory> getStockHistoryByStockIdForYTD(Long id);

    @Query(
            value = "SELECT * FROM stock_history WHERE stock_id = :id AND type = :type ORDER BY on_date ASC",
            nativeQuery = true)
    List<StockHistory> getStockHistoryByStockIdAndHistoryType(Long id, String type);

    @Transactional
    void deleteByStockIdAndType(Long stockId, StockHistoryType type);
}

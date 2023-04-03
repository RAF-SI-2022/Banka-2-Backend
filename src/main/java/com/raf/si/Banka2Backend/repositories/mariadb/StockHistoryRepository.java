package com.raf.si.Banka2Backend.repositories.mariadb;

import com.raf.si.Banka2Backend.models.mariadb.StockHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {

  @Query("SELECT sh FROM StockHistory sh WHERE sh.stock.id = :id")
  List<StockHistory> getStockHistoryByStockId(Long id);

  @Query(
      value =
          "SELECT * FROM (SELECT * FROM stock_history s WHERE s.stock_id = :id AND s.type = 'DAILY' ORDER BY s.on_date DESC LIMIT :period) sh ORDER BY sh.on_date ASC",
      nativeQuery = true)
  List<StockHistory> getStockHistoryByStockIdAndTimePeriod(Long id, Integer period);

  @Query(
          value = "SELECT * FROM (SELECT * FROM stock_history s WHERE s.stock_id = :id AND s.type = 'DAILY' AND year(on_date) = year(CURDATE()) ORDER BY s.on_date DESC) sh ORDER BY sh.on_date ASC",
          nativeQuery = true
  )
  List<StockHistory> getStockHistoryByStockIdAndTimePeriodForYTD(Long id);

  @Query(
      value =
          "SELECT * FROM WHERE stock_id = :id AND type = :type ORDER BY on_date ASC",
      nativeQuery = true)
  List<StockHistory> getStockHistoryByStockIdAndTimePeriod(Long id, String type);

}

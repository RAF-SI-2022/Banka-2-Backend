package rs.edu.raf.si.bank2.main.repositories.mariadb;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.main.models.mariadb.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findStockBySymbol(String symbol);

    @Modifying
    @Transactional
    @Query(
            "UPDATE Stock s SET s.companyName = :companyName, s.outstandingShares = :outstandingShares, s.dividendYield = :dividendYield,"
                    + " s.priceValue = :priceValue, s.openValue = :openValue, s.lowValue = :lowValue, s.highValue = :highValue, s.changeValue = :changeValue,"
                    + " s.previousClose = :previousClose, s.volumeValue = :volumeValue, s.lastUpdated = :lastUpdated, s.changePercent = :changePercent,"
                    + " s.websiteUrl = :websiteUrl WHERE s.symbol = :symbol")
    void updateStock(
            @Param("symbol") String symbol,
            @Param("companyName") String companyName,
            @Param("outstandingShares") Long outstandingShares,
            @Param("dividendYield") BigDecimal dividendYield,
            @Param("priceValue") BigDecimal priceValue,
            @Param("openValue") BigDecimal openValue,
            @Param("lowValue") BigDecimal lowValue,
            @Param("highValue") BigDecimal highValue,
            @Param("changeValue") BigDecimal changeValue,
            @Param("previousClose") BigDecimal previousClose,
            @Param("volumeValue") Long volumeValue,
            @Param("lastUpdated") LocalDate lastUpdated,
            @Param("changePercent") String changePercent,
            @Param("websiteUrl") String websiteUrl);
}

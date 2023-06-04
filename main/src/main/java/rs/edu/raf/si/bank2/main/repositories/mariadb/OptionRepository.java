package rs.edu.raf.si.bank2.main.repositories.mariadb;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.main.models.mariadb.Option;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findAllByStockSymbol(String symbol);

    List<Option> findAllByStockSymbolAndExpirationDate(String symbol, LocalDate expirationDate);

    Optional<Option> findByContractSymbolAndStockSymbolAndOptionType(
            String contractSymbol, String stockSymbol, String optionType);

    @Modifying
    @Transactional
    @Query(
            "UPDATE Option o SET o.stockSymbol = :stockSymbol, o.contractSymbol = :contractSymbol, o.optionType = :optionType, o.strike = :strike,"
                    + " o.impliedVolatility = :impliedVolatility, o.price = :price, o.expirationDate = :expirationDate, o.openInterest = :openInterest,"
                    + " o.contractSize = :contractSize, o.maintenanceMargin = :maintenanceMargin, o.bid = :bid, o.ask = :ask, o.changePrice = :changePrice,"
                    + " o.percentChange = :percentChange, o.inTheMoney = :inTheMoney WHERE o.id = :id")
    void updateOption(
            @Param("id") Long id,
            @Param("stockSymbol") String stockSymbol,
            @Param("contractSymbol") String contractSymbol,
            @Param("optionType") String optionType,
            @Param("strike") Double strike,
            @Param("impliedVolatility") Double impliedVolatility,
            @Param("price") Double price,
            @Param("expirationDate") LocalDate expirationDate,
            @Param("openInterest") Integer openInterest,
            @Param("contractSize") Integer contractSize,
            @Param("maintenanceMargin") Double maintenanceMargin,
            @Param("bid") Double bid,
            @Param("ask") Double ask,
            @Param("changePrice") Double changePrice,
            @Param("percentChange") Double percentChange,
            @Param("inTheMoney") Boolean inTheMoney);
}

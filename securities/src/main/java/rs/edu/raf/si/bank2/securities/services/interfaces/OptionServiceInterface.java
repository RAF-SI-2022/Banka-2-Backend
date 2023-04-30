package rs.edu.raf.si.bank2.securities.services.interfaces;

import java.util.List;
import java.util.Optional;
import rs.edu.raf.si.bank2.securities.models.mariadb.Option;

public interface OptionServiceInterface {

    List<Option> findAll();

    Option save(Option option);

    Optional<Option> findById(Long id);

    List<Option> findByStock(String stockSymbol);

    List<Option> findByStockAndDate(String stockSymbol, String dateMils);
}

package rs.edu.raf.si.bank2.main.services.interfaces;

import rs.edu.raf.si.bank2.main.models.mariadb.UserStock;

import java.util.List;
import java.util.Optional;

public interface UserStockServiceInterface {

    Optional<UserStock> findUserStockByUserIdAndStockSymbol(long userId, String stockSymbol);

    UserStock save(UserStock userStock);

    List<UserStock> findAll();

    List<UserStock> findAllForUser(long userId);

    UserStock removeFromMarket(long userId, String stockSymbol);
}

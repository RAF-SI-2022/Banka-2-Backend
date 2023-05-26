package rs.edu.raf.si.bank2.users.services.interfaces;

import java.util.List;
import java.util.Optional;
import rs.edu.raf.si.bank2.users.models.mariadb.UserStock;

public interface UserStockServiceInterface {

    Optional<UserStock> findUserStockByUserIdAndStockSymbol(long userId, String stockSymbol);

    UserStock save(UserStock userStock);

    List<UserStock> findAll();

    List<UserStock> findAllForUser(long userId);

    UserStock removeFromMarket(long userId, String stockSymbol);
}

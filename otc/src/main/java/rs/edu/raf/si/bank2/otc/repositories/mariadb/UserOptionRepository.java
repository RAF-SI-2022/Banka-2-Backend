package rs.edu.raf.si.bank2.otc.repositories.mariadb;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.otc.models.mariadb.UserOption;

@Repository
public interface UserOptionRepository extends JpaRepository<UserOption, Long> {

    List<UserOption> getUserOptionsByUserId(Long userId);

    List<UserOption> getUserOptionsByUserIdAndStockSymbol(Long userId, String stockSymbol);
}

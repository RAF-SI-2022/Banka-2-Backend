package rs.edu.raf.si.bank2.main.repositories.mariadb;

import rs.edu.raf.si.bank2.main.models.mariadb.UserOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOptionRepository extends JpaRepository<UserOption, Long> {

    List<UserOption> getUserOptionsByUserId(Long userId);

    List<UserOption> getUserOptionsByUserIdAndStockSymbol(Long userId, String stockSymbol);
}

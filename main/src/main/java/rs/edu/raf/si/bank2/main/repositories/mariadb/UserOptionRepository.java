package rs.edu.raf.si.bank2.main.repositories.mariadb;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.main.models.mariadb.UserOption;

@Repository
public interface UserOptionRepository extends JpaRepository<UserOption, Long> {

    List<UserOption> getUserOptionsByUserId(Long userId);

    List<UserOption> getUserOptionsByUserIdAndStockSymbol(Long userId, String stockSymbol);

    Optional<UserOption> findByIdAndUserId(Long id, Long userId);
}

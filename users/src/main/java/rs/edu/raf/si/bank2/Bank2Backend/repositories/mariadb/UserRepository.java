package rs.edu.raf.si.bank2.Bank2Backend.repositories.mariadb;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.Bank2Backend.models.mariadb.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);
}

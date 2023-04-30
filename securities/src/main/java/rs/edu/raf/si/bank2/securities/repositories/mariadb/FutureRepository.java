package rs.edu.raf.si.bank2.securities.repositories.mariadb;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.securities.models.mariadb.Future;

@Repository
public interface FutureRepository extends JpaRepository<Future, Long> {

    Optional<Future> findFutureByFutureName(String futureName);

    Optional<List<Future>> findFuturesByFutureName(String futureName);

    Optional<Future> findFutureById(Long Id);
}

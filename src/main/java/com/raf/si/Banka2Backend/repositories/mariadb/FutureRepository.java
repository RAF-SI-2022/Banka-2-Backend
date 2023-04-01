package com.raf.si.Banka2Backend.repositories.mariadb;

import com.raf.si.Banka2Backend.models.mariadb.Future;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FutureRepository extends JpaRepository<Future, Long> {

  Optional<Future> findFutureByFutureName(String futureName);

  Optional<List<Future>> findFuturesByFutureName(String futureName);

  Optional<Future> findFutureById(Long Id);
}

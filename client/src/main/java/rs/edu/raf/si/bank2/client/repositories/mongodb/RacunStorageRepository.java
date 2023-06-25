package rs.edu.raf.si.bank2.client.repositories.mongodb;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.client.models.mongodb.RacunStorage;

@Repository
public interface RacunStorageRepository extends MongoRepository<RacunStorage, String> {

    Optional<RacunStorage> findRacunStorageByBalanceRegistrationNumber(String regNumber);
}

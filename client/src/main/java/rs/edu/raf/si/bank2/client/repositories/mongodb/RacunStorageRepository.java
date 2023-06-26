package rs.edu.raf.si.bank2.client.repositories.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.client.models.mongodb.RacunStorage;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.Balance;

import java.util.Optional;

@Repository
public interface RacunStorageRepository extends MongoRepository<RacunStorage, String> {

    Optional<RacunStorage> findRacunStorageByBalanceRegistrationNumber(String regNumber);

}

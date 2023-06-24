package rs.edu.raf.si.bank2.client.repositories.mongodb;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.client.models.mongodb.DevizniRacun;

@Repository
public interface DevizniRacunRepository extends MongoRepository<DevizniRacun, String> {

    List<DevizniRacun> findDevizniRacunByOwnerId(String ownerId);

    Optional<DevizniRacun> findDevizniRacunByRegistrationNumber(String registrationNumber);
}

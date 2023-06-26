package rs.edu.raf.si.bank2.client.repositories.mongodb;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.client.models.mongodb.PoslovniRacun;

@Repository
public interface PoslovniRacunRepository extends MongoRepository<PoslovniRacun, String> {

    List<PoslovniRacun> findPoslovniRacunByOwnerId(String ownerId);

    Optional<PoslovniRacun> findPoslovniRacunByRegistrationNumber(String registrationNumber);
}

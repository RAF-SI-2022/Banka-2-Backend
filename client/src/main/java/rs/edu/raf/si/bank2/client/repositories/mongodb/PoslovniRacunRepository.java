package rs.edu.raf.si.bank2.client.repositories.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import rs.edu.raf.si.bank2.client.models.mongodb.PoslovniRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.TekuciRacun;

import java.util.List;
import java.util.Optional;

public interface PoslovniRacunRepository extends MongoRepository<PoslovniRacun, String> {

    List<PoslovniRacun> findPoslovniRacunByOwnerId(String ownerId);

    Optional<PoslovniRacun> findPoslovniRacunByRegistrationNumber(String registrationNumber);

}

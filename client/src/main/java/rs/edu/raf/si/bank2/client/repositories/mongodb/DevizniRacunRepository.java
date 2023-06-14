package rs.edu.raf.si.bank2.client.repositories.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import rs.edu.raf.si.bank2.client.models.mongodb.DevizniRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.TekuciRacun;

import java.util.List;

public interface DevizniRacunRepository extends MongoRepository<DevizniRacun, String> {

    List<DevizniRacun> findDevizniRacunByOwnerId(String ownerId);
}

package rs.edu.raf.si.bank2.client.repositories.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import rs.edu.raf.si.bank2.client.models.mongodb.PoslovniRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.TekuciRacun;

public interface PoslovniRacunRepository extends MongoRepository<PoslovniRacun, String> {
}

package rs.edu.raf.si.bank2.client.repositories.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import rs.edu.raf.si.bank2.client.models.mongodb.TekuciRacun;

public interface TekuciRacunRepository extends MongoRepository<TekuciRacun, String> {
}

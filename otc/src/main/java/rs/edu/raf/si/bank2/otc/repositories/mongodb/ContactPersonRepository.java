package rs.edu.raf.si.bank2.otc.repositories.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContactPerson;

public interface ContactPersonRepository extends MongoRepository<ContactPerson, Long> {
}

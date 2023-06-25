package rs.edu.raf.si.bank2.otc.repositories.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContactPerson;

@Repository
public interface ContactPersonRepository extends MongoRepository<ContactPerson, String> {}

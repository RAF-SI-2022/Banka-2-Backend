package rs.edu.raf.si.bank2.otc.repositories.mongodb;

import io.swagger.v3.oas.models.info.Contact;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContactPerson;
import rs.edu.raf.si.bank2.otc.models.mongodb.Contract;

@Repository
public interface ContactRepository extends MongoRepository<Contract, String> {


}

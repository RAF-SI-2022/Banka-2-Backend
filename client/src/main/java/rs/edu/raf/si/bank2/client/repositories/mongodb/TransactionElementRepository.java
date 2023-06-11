package rs.edu.raf.si.bank2.client.repositories.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.client.models.mongodb.TransactionElement;

@Repository
public interface TransactionElementRepository extends MongoRepository<TransactionElement, String> {

}

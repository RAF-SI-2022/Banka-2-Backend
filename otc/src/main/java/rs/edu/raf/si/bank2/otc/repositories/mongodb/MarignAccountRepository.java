package rs.edu.raf.si.bank2.otc.repositories.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.otc.models.mongodb.MarginAccount;
import rs.edu.raf.si.bank2.otc.models.mongodb.MarginTransaction;

@Repository
public interface MarignAccountRepository extends MongoRepository<MarginAccount, String> {
}

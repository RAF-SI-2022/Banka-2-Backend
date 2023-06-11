package rs.edu.raf.si.bank2.client.repositories.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.client.models.mongodb.MarginTransaction;

import java.util.List;

@Repository
public interface MarginTransactionRepository extends MongoRepository<MarginTransaction,String> {
    List<MarginTransaction> findMarginTransactionsByUserEmail(String email);
}

package rs.edu.raf.si.bank2.otc.repositories.mongodb;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.otc.models.mongodb.MarginTransaction;

@Repository
public interface MarginTransactionRepository extends MongoRepository<MarginTransaction, String> {
    List<MarginTransaction> findMarginTransactionsByUserEmail(String email);

    List<MarginTransaction> findMarginTransactionByOrderType(String orderType);
}

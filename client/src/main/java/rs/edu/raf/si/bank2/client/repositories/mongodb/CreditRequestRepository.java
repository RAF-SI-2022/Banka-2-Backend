package rs.edu.raf.si.bank2.client.repositories.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.client.models.mongodb.Credit;
import rs.edu.raf.si.bank2.client.models.mongodb.CreditRequest;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.CreditApproval;

import java.util.List;

@Repository
public interface CreditRequestRepository extends MongoRepository<CreditRequest, String> {

    List<CreditRequest> findAllByClientEmail(String clientEmail);

    List<CreditRequest> findAllByCreditApproval (CreditApproval creditApproval);

}

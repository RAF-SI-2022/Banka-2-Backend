package rs.edu.raf.si.bank2.client.repositories.mongodb;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.client.models.mongodb.Payment;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    List<Payment> findAllBySenderEmail(String senderEmail);
}

package rs.edu.raf.si.bank2.client.repositories.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.client.models.mongodb.PaymentReceiver;

import java.util.List;

@Repository
public interface PaymentReceiverRepository extends MongoRepository<PaymentReceiver, String> {

    List<PaymentReceiver> findPaymentReceiversBySavedByClientId(String SavedByClientId);
}

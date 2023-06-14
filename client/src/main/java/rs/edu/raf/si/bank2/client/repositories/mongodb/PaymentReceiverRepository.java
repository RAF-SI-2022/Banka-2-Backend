package rs.edu.raf.si.bank2.client.repositories.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import rs.edu.raf.si.bank2.client.models.mongodb.PaymentReceiver;

import java.util.List;

public interface PaymentReceiverRepository extends MongoRepository<PaymentReceiver, String> {

    List<PaymentReceiver> findPaymentReceiversBySavedByClientId(String SavedByClientId);
}

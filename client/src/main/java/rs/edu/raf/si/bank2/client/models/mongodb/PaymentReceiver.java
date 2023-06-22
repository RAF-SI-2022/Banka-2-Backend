package rs.edu.raf.si.bank2.client.models.mongodb;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Data
@Builder
@AllArgsConstructor
//@RequiredArgsConstructor
@Document("paymentReceivers")
public class PaymentReceiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String savedByClientEmail;
    private String receiverName;
    private String balanceRegistrationNumber;
    private String referenceNumber;//poziv na broj
    private String paymentNumber;
    private String paymentDescription;


    public PaymentReceiver() {
    }

    public PaymentReceiver(String savedByClientEmail, String receiverName, String balanceRegistrationNumber,
                           String referenceNumber, String paymentNumber, String paymentDescription) {
        this.savedByClientEmail = savedByClientEmail;
        this.receiverName = receiverName;
        this.balanceRegistrationNumber = balanceRegistrationNumber;
        this.referenceNumber = referenceNumber;
        this.paymentNumber = paymentNumber;
        this.paymentDescription = paymentDescription;
    }
}

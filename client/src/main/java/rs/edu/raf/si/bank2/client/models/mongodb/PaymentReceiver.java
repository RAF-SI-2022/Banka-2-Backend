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
    private String name;
    private String balanceRegistrationNumber;
    private String savedByClientId;


    public PaymentReceiver() {
    }

    public PaymentReceiver(String name, String balanceRegistrationNumber, String savedByClientId) {
        this.name = name;
        this.balanceRegistrationNumber = balanceRegistrationNumber;
        this.savedByClientId = savedByClientId;
    }

}

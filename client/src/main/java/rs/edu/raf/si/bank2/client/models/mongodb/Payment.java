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
@Document("payments")
public class Payment {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String receiverName;
    private String balanceRegistrationNumber;//reg broj racuna
    private Double amount;
    private String referenceNumber;//poziv na broj
    private String paymentNumber;
    private String paymentDescription;

    public Payment() {
    }

    public Payment(String receiverName, String balanceRegistrationNumber,
                   Double amount, String referenceNumber, String paymentNumber, String paymentDescription) {
        this.receiverName = receiverName;
        this.balanceRegistrationNumber = balanceRegistrationNumber;
        this.amount = amount;
        this.referenceNumber = referenceNumber;
        this.paymentNumber = paymentNumber;
        this.paymentDescription = paymentDescription;
    }
}

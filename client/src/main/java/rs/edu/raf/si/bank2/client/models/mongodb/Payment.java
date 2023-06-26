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
    private String senderEmail;
    private String receiverName;
    private String balanceRegistrationNumber;//reg broj racuna
    private String toBalanceRegistrationNumber;//reg broj racuna
    private Double amount;
    private String referenceNumber;//poziv na broj
    private String paymentNumber;
    private String paymentDescription;

    public Payment() {
    }

    public Payment(String senderEmail, String receiverName, String balanceRegistrationNumber, String toBalanceRegistrationNumber,
                   Double amount, String referenceNumber, String paymentNumber, String paymentDescription) {
        this.receiverName = receiverName;
        this.senderEmail = senderEmail;
        this.balanceRegistrationNumber = balanceRegistrationNumber;
        this.toBalanceRegistrationNumber = toBalanceRegistrationNumber;
        this.amount = amount;
        this.referenceNumber = referenceNumber;
        this.paymentNumber = paymentNumber;
        this.paymentDescription = paymentDescription;
    }
}

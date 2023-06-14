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
    private String senderId;//klijent koji je posalo
    private String receiverName;
    private String fromBalanceRegNum;
    private String toBalanceRegNum;
    private Double amount;
    private String referenceNumber;//poziv na broj
    private String paymentNumber;
    private String paymentDescription;

    public Payment() {
    }

    public Payment(String senderId, String receiverName, String fromBalanceRegNum, String toBalanceRegNum,
                   Double amount, String referenceNumber, String paymentNumber, String paymentDescription) {
        this.senderId = senderId;
        this.receiverName = receiverName;
        this.fromBalanceRegNum = fromBalanceRegNum;
        this.toBalanceRegNum = toBalanceRegNum;
        this.amount = amount;
        this.referenceNumber = referenceNumber;
        this.paymentNumber = paymentNumber;
        this.paymentDescription = paymentDescription;
    }

}

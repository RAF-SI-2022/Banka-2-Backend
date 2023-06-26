package rs.edu.raf.si.bank2.client.dto;

import lombok.Data;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.Balance;

@Data
public class PaymentDto {

    private String senderEmail;
    private String receiverName;
    private String fromBalanceRegNum;
    private String toBalanceRegNum;
    private Double amount;
    private String referenceNumber;
    private String paymentNumber;
    private String paymentDescription;

    public PaymentDto(String receiverName, String fromBalanceRegNum, String toBalanceRegNum,
                      Double amount, String referenceNumber, String paymentNumber, String paymentDescription) {
        this.receiverName = receiverName;
        this.fromBalanceRegNum = fromBalanceRegNum;
        this.toBalanceRegNum = toBalanceRegNum;
        this.amount = amount;
        this.referenceNumber = referenceNumber;
        this.paymentNumber = paymentNumber;
        this.paymentDescription = paymentDescription;
    }
}

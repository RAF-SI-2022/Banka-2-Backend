package rs.edu.raf.si.bank2.client.dto;

import lombok.Data;

@Data
public class PaymentDto {

    private String senderId;
    private String receiverName;
    private String fromBalanceRegNum;
    private String toBalanceRegNum;
    private Double amount;
    private String referenceNumber;
    private String paymentNumber;
    private String paymentDescription;


    public PaymentDto(String senderId, String receiverName, String fromBalanceRegNum,
                      String toBalanceRegNum, Double amount, String referenceNumber, String paymentNumber, String paymentDescription) {
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

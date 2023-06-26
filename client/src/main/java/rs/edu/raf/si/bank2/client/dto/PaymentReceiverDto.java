package rs.edu.raf.si.bank2.client.dto;

import lombok.Data;

@Data
public class PaymentReceiverDto {
    private String savedByClientEmail;
    private String receiverName;
    private String balanceRegistrationNumber;
    private String referenceNumber;//poziv na broj
    private String paymentNumber;
    private String paymentDescription;

    public PaymentReceiverDto(String savedByClientEmail, String receiverName, String balanceRegistrationNumber,
                              String referenceNumber, String paymentNumber, String paymentDescription) {
        this.savedByClientEmail = savedByClientEmail;
        this.receiverName = receiverName;
        this.balanceRegistrationNumber = balanceRegistrationNumber;
        this.referenceNumber = referenceNumber;
        this.paymentNumber = paymentNumber;
        this.paymentDescription = paymentDescription;
    }

}

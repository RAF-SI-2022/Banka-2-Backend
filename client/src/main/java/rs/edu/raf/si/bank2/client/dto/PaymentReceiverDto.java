package rs.edu.raf.si.bank2.client.dto;

import lombok.Data;

@Data
public class PaymentReceiverDto {
    private String name;
    private String balanceRegistrationNumber;
    private String savedByClientId;

    public PaymentReceiverDto(String name, String balanceRegistrationNumber, String savedByClientId) {
        this.name = name;
        this.balanceRegistrationNumber = balanceRegistrationNumber;
        this.savedByClientId = savedByClientId;
    }

}

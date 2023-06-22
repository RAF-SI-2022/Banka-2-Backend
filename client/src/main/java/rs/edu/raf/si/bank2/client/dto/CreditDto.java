package rs.edu.raf.si.bank2.client.dto;

import lombok.Data;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.CreditApproval;

@Data
public class CreditDto {

    private String clientEmail;
    private String name;
    private String accountRegNumber;
    private Double amount;
    private Integer ratePercentage;//stopa na iznos
    private Double monthlyRate;//koliko se mesecno placa
    private String dueDate;//do kad se otplacuje
    private String currency;

    public CreditDto(String clientEmail, String name, String accountRegNumber, Double amount,
                     Integer ratePercentage, Double monthlyRate, String dueDate, String currency) {
        this.clientEmail = clientEmail;
        this.name = name;
        this.accountRegNumber = accountRegNumber;
        this.amount = amount;
        this.ratePercentage = ratePercentage;
        this.monthlyRate = monthlyRate;
        this.dueDate = dueDate;
        this.currency = currency;
    }
}

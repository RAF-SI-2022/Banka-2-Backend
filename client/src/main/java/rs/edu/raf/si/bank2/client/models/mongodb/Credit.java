package rs.edu.raf.si.bank2.client.models.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.CreditApproval;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Data
@Builder
@AllArgsConstructor
//@RequiredArgsConstructor
@Document("credit")
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String clientEmail;
    private CreditApproval creditApproval; //approved / denied
//    private String name;
//    private String accountRegNumber;
    private Double amount;
    private Double remainingAmount;
    private Integer ratePercentage;//stopa na iznos
    private Double monthlyRate;//koliko se mesecno placa
    private String creationDate;
    private String monthsOwned;//koliko ga dugo ima
    private String dueDate;//do kad se otplacuje
    private String currency;

    public Credit() {
    }


}

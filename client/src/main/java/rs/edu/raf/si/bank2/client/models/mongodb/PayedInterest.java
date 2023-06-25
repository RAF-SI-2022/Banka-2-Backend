package rs.edu.raf.si.bank2.client.models.mongodb;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
// @RequiredArgsConstructor
@Document("interest")
public class PayedInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String name;
    private String creditId;
    private String date;
    private Double amount;

    public PayedInterest() {}

    public PayedInterest(String name, String creditId, String date, Double amount) {
        this.name = name;
        this.creditId = creditId;
        this.date = date;
        this.amount = amount;
    }
}

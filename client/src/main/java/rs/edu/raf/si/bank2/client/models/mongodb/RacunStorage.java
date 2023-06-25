package rs.edu.raf.si.bank2.client.models.mongodb;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.Balance;

@Data
@Builder
@AllArgsConstructor
// @RequiredArgsConstructor
@Document("balanceTypeStorage")
public class RacunStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String balanceRegistrationNumber;
    private Balance type;

    public RacunStorage(String balanceRegistrationNumber, Balance type) {
        this.balanceRegistrationNumber = balanceRegistrationNumber;
        this.type = type;
    }

    public RacunStorage() {}
}

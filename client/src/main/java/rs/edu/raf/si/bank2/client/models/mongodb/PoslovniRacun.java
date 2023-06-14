package rs.edu.raf.si.bank2.client.models.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.Balance;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BalanceStatus;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BalanceType;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BussinessAccountType;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Data
@Builder
@AllArgsConstructor
//@RequiredArgsConstructor
@Document("poslovniRacun")
public class PoslovniRacun extends Racun{

    public PoslovniRacun() {
    }

    public PoslovniRacun(String registrationNumber, String ownerId, Double balance, Double availableBalance, Long assignedAgentId,
                         String creationDate, String expirationDate, String currency, BalanceStatus balanceStatus, BussinessAccountType bussinessAccountType) {
        super(registrationNumber, ownerId, balance, availableBalance, assignedAgentId, creationDate, Balance.POSLOVNI ,expirationDate, currency,
                balanceStatus, null, null, null);
        this.bussinessAccountType = bussinessAccountType;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private BussinessAccountType bussinessAccountType;
//    private String companyId; //todo ovo stavi kasnije (treba da se gettuje iz otc servisa ili sa fronta)

}

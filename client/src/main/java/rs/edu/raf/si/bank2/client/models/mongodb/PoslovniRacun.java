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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String registrationNumber;
    private String ownerId;
    private Balance type; //koji je balans zapravo
    private Double balance;
    private Double availableBalance; //ovo je toliko glupo .i.
    private Long assignedAgentId;
    private String creationDate;
    private String expirationDate;
    private String currency;
    private BalanceStatus balanceStatus; //enum active / not active
    private BussinessAccountType bussinessAccountType;
//    private String companyId; //todo ovo stavi kasnije (treba da se gettuje iz otc servisa ili sa fronta)


    public PoslovniRacun() {
    }

    public PoslovniRacun(String registrationNumber, String ownerId, Double balance, Double availableBalance, Long assignedAgentId,
                         String creationDate, String expirationDate, String currency, BalanceStatus balanceStatus, BussinessAccountType bussinessAccountType) {
        this.registrationNumber = registrationNumber;
        this.ownerId = ownerId;
        this.balance = balance;
        this.availableBalance = availableBalance;
        this.assignedAgentId = assignedAgentId;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
        this.currency = currency;
        this.balanceStatus = balanceStatus;
        this.type = Balance.POSLOVNI;
        this.bussinessAccountType = bussinessAccountType;
    }

}

package rs.edu.raf.si.bank2.client.models.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.Balance;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BalanceStatus;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BalanceType;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
//@RequiredArgsConstructor
@Document("devizniRacun")
public class DevizniRacun extends Racun{

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
    private BalanceType balanceType; //enum //licni, stedni ...
    private Integer interestRatePercentage; //1% - kamatna stopa
    private Double accountMaintenance;
    private Boolean defaultCurrency;
    private List<String> allowedCurrencies;


    public DevizniRacun() {
    }

    public DevizniRacun(String registrationNumber, String ownerId, Double balance, Double availableBalance, Long assignedAgentId,
                        String creationDate, String expirationDate, String currency, BalanceStatus balanceStatus, BalanceType balanceType,
                        Integer interestRatePercentage, Double accountMaintenance, Boolean defaultCurrency, List<String> allowedCurrencies) {
        this.registrationNumber = registrationNumber;
        this.ownerId = ownerId;
        this.balance = balance;
        this.availableBalance = availableBalance;
        this.assignedAgentId = assignedAgentId;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
        this.currency = currency;
        this.balanceStatus = balanceStatus;
        this.balanceType = balanceType;
        this.interestRatePercentage = interestRatePercentage;
        this.accountMaintenance = accountMaintenance;
        this.type = Balance.DEVIZNI;
        this.defaultCurrency = defaultCurrency;
        this.allowedCurrencies = allowedCurrencies;
    }
}

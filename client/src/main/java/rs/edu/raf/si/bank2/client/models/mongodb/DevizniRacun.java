package rs.edu.raf.si.bank2.client.models.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BalanceStatus;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BalanceType;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Data
@Builder
@AllArgsConstructor
//@RequiredArgsConstructor
@Document("devizniRacun")
public class DevizniRacun extends Racun{

    public DevizniRacun() {
    }

    public DevizniRacun(String registrationNumber, String ownerId, Double balance, Double availableBalance, Long assignedAgentId,
                        String creationDate, String expirationDate, String currency, BalanceStatus balanceStatus, BalanceType balanceType,
                        Integer interestRatePercentage, Double accountMaintenance, Boolean defaultCurrency, Integer allowedNumOfCurrencies) {
        super(registrationNumber, ownerId, balance, availableBalance, assignedAgentId, creationDate, expirationDate, currency,
                balanceStatus, balanceType, interestRatePercentage, accountMaintenance);
        this.defaultCurrency = defaultCurrency;
        this.allowedNumOfCurrencies = allowedNumOfCurrencies;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private Boolean defaultCurrency;
    private Integer allowedNumOfCurrencies;



}

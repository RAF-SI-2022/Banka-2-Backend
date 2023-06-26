package rs.edu.raf.si.bank2.client.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BalanceType;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class DevizniRacunDto {

    private String ownerId;
    private Long assignedAgentId;
    private String currency;
    private BalanceType balanceType;
    private Integer interestRatePercentage;
    private Double accountMaintenance;
    //      private Boolean defaultCurrency;
    private List<String> allowedCurrencies;
}

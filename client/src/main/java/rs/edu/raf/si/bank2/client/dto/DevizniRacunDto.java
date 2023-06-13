package rs.edu.raf.si.bank2.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BalanceStatus;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BalanceType;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class DevizniRacunDto {

//      private String id;
//      private String registrationNumber;
      private String ownerId;
//      private Double balance;
//      private Double availableBalance;
      private Long assignedAgentId;
      private String currency;
//      private BalanceStatus balanceStatus; //enum active / not active
      private BalanceType balanceType; //enum //licni, stedni ...
      private Integer interestRatePercentage; //1% - kamatna stopa
      private Double accountMaintenance;
      private Boolean defaultCurrency;
      private List<String> allowedNumOfCurrencies;

}

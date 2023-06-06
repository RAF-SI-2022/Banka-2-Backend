package rs.edu.raf.si.bank2.otc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContractElements;
import rs.edu.raf.si.bank2.otc.models.mongodb.TransactionElements;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TransactionElementDto {

    private String contractId;
    private String elementId;//prazno kada se pravi prvi put pravi
    private ContractElements buyOrSell;
    private TransactionElements transactionElement;// STOCK || OPTION || FUTURE
    private ContractElements balance; //cache margin
    private String currency;// USD, RSD
    private Integer amount;
    private Double priceOfOneElement;
    private Long userId;
    private Long mariaDbId;
    private String futureStorageField; //ako nije future bice null, sluzi da se recoveruje future nakon brisanja

}

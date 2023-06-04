package rs.edu.raf.si.bank2.otc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContractElements;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TransactionElementDto {

    private String              contractId;
    private String              elementId;
    private ContractElements    buyOrSell;
    private String              transactionElement;// stock forex, koji je ...
    private ContractElements    balance;            //cache margin
    private String              currency;
    private Double              amount;
    private Double              priceOfOneElement;

}

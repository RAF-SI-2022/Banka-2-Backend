package rs.edu.raf.si.bank2.otc.models.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Document("transactionElement")
public class TransactionElement {


    private ContractElements buyOrSEll;
    private String transactionElement; //koji stock forex ili sta vec
    private ContractElements balance; //da li je cash ili margin
    private String currency;
    private Double amount;
    private Double priceOfOneElement;

}

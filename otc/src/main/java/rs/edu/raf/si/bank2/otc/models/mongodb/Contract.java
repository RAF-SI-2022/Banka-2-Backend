package rs.edu.raf.si.bank2.otc.models.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Document("contract")
public class Contract {

    public Contract(String id, ContractElements contractStatus, String creationDate,
                    String lastUpdatedDate, String contractNumber, String description, String note, Collection<TransactionElement> transactionElements) {
        this.id = id;
        this.contractStatus = contractStatus;
        this.creationDate = creationDate;
        this.lastUpdatedDate = lastUpdatedDate;
        this.contractNumber = contractNumber;
        this.description = description;
        this.transactionElements = transactionElements;
    }

    @Id
    private String id;
    @DBRef(lazy = true)
    private Company company;
    private ContractElements contractStatus;
    private String creationDate;
    private String lastUpdatedDate;
    private String contractNumber;
    private String description;
//    @DBRef(lazy = true)
    private Collection<TransactionElement> transactionElements;

}

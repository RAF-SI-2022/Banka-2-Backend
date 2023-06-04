package rs.edu.raf.si.bank2.otc.models.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Document("companyBankAccount")
public class CompanyBankAccount {
    @Id
    private Long id;
    @DBRef(lazy = true)
    private Company company;
    private String accountNumber;
    private String bankName;
    private String accountType;
}

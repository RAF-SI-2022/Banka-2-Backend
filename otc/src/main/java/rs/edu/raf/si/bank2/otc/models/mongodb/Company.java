package rs.edu.raf.si.bank2.otc.models.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Document("company")
public class Company {

    public Company(String name, String registrationNumber, String taxNumber, String activityCode, String address) {
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.taxNumber = taxNumber;
        this.activityCode = activityCode;
        this.address = address;
    }

    @Id
    private Long id;
    private String name;
    private String registrationNumber;
    private String taxNumber;
    private String activityCode;
    private String address;
    @DBRef(lazy = true)
    private Collection<ContactPerson> contactPersons;
    @DBRef(lazy = true)
    private Collection<CompanyBankAccount> bankAccounts;

    public Company(String name, String registrationNumber, String taxNumber, String activityCode, String address, Collection<ContactPerson> contactPersons, Collection<CompanyBankAccount> bankAccounts) {
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.taxNumber = taxNumber;
        this.activityCode = activityCode;
        this.address = address;
        this.contactPersons = new ArrayList<>(contactPersons);
        this.bankAccounts = new ArrayList<>(bankAccounts);

    }
}

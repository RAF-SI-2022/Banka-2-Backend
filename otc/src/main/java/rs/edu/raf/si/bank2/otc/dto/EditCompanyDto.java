package rs.edu.raf.si.bank2.otc.dto;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import rs.edu.raf.si.bank2.otc.models.mongodb.CompanyBankAccount;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContactPerson;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class EditCompanyDto {
    private String id;
    private String name;
    private String registrationNumber;
    private String taxNumber;
    private String activityCode;
    private String address;
    private Collection<ContactPerson> contactPersons;
    private Collection<CompanyBankAccount> bankAccounts;
}

package rs.edu.raf.si.bank2.otc.dto;

import java.util.Collection;
import lombok.Data;
import rs.edu.raf.si.bank2.otc.models.mongodb.CompanyBankAccount;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContactPerson;

@Data
public class ContactsBankAccountsDto {

    String id;
    Collection<ContactPerson> contactPeople;
    Collection<CompanyBankAccount> companyBankAccounts;
}

package rs.edu.raf.si.bank2.client.dto;

import lombok.Data;
import rs.edu.raf.si.bank2.client.models.mongodb.CompanyBankAccount;
import rs.edu.raf.si.bank2.client.models.mongodb.ContactPerson;

import java.util.Collection;

@Data
public class ContactsBankAccountsDto {

    String id;
    Collection<ContactPerson> contactPeople;
    Collection<CompanyBankAccount> companyBankAccounts;
}

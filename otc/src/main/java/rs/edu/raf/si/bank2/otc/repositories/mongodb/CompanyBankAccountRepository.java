package rs.edu.raf.si.bank2.otc.repositories.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import rs.edu.raf.si.bank2.otc.models.mongodb.CompanyBankAccount;

public interface CompanyBankAccountRepository extends MongoRepository<CompanyBankAccount, Long> {

}

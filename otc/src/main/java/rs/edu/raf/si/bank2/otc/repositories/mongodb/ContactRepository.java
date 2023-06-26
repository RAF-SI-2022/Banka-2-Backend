package rs.edu.raf.si.bank2.otc.repositories.mongodb;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.otc.models.mongodb.Contract;

@Repository
public interface ContactRepository extends MongoRepository<Contract, String> {

    List<Contract> findByUserId(Long userId);

    List<Contract> findByUserIdAndContractStatus(Long userId, String contractStatus);

    List<Contract> findAllByContractStatus(String contractStatus);

    List<Contract> findByCompanyId(String companyId);

    List<Contract> findByUserIdAndCompanyId(Long userId, String companyId);
}

package rs.edu.raf.si.bank2.otc.repositories.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.si.bank2.otc.models.mongodb.ListingGroup;
import rs.edu.raf.si.bank2.otc.models.mongodb.MarginBalance;

import java.util.Optional;

@Repository
public interface MarginBalanceRepository extends MongoRepository<MarginBalance,String> {

    Optional<MarginBalance> findMarginBalanceByListingGroup(ListingGroup listingGroup);
}

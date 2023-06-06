package rs.edu.raf.si.bank2.otc.models.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Document("marginsAccount")
public class MarginBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String id;
    AccountType accountType;
    String currencyCode;
    ListingGroup listingGroup;
    Double investedResources;
    Double loanedResources;
    Double maintenanceMargin;
    boolean marginCall;

}

package rs.edu.raf.si.bank2.client.models.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Document("marginTransaction")
public class MarginTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String id;
    AccountType accountType;
    LocalDateTime dateTime;
//    ListingGroup listingGroup;//Long orderId;// ovo smo se dogovorili da fejkujemo?
    // jer nam je pretesko da namestimo komunikaciju i sve do prekosutra

    Long orderId;
    String userEmail;
    String transactionComment;
    String currencyCode;
    TransactionType transactionType;
    Double initialMargin;
    Double loanValue;
    Double maintenanceMargin;
    Double interest;
}

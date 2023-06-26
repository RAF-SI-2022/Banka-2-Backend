package rs.edu.raf.si.bank2.otc.models.mongodb;

import java.time.LocalDateTime;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    Long orderId;
    String userEmail;
    String transactionComment;
    String currencyCode;
    TransactionType transactionType;
    Double initialMargin;
    Double loanValue;
    Double maintenanceMargin;
    Double interest;
    String orderType;
}

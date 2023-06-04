package rs.edu.raf.si.bank2.otc.models.mongodb;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Document("marginTransaction")
public class MarginTransaction {

    @Id
    private Long id;

    private Date transactionTime;

    private String order;

    private String user;

    private String description;

    private String currency;

    private String type;

    private Double stake;

    private Double loanValue;

    private Double maintenanceMargin;

    // iznos kamate
    private Double interestRate;




}

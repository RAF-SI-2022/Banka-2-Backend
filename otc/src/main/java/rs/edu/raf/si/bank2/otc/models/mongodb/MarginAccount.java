package rs.edu.raf.si.bank2.otc.models.mongodb;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Currency;

@Getter
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Document("marginAccount")
public class MarginAccount {

    @Id
    private Long id;

    private String currency;

    private String type;

    private Double currentAmount;

    private Double loanValue;

    private Double maintenanceMargin;

    private boolean marginCall;

}

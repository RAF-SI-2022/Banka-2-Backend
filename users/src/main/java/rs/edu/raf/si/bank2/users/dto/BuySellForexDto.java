package rs.edu.raf.si.bank2.users.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BuySellForexDto {
    /**
     * base currency
     */
    @NotNull
    String fromCurrencyCode;
    /**
     * quote currency
     */
    @NotNull
    String toCurrencyCode;

    @NotNull
    Integer amount;
}

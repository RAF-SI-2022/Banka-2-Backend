package rs.edu.raf.si.bank2.securities.models.mariadb.orders;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import lombok.Data;
import rs.edu.raf.si.bank2.securities.models.mariadb.User;

@Data
@Entity
@DiscriminatorValue("OPTION_ORDER")
public class OptionOrder extends Order {
    public OptionOrder(
            Long id,
            @NotNull OrderType orderType,
            @NotNull OrderTradeType tradeType,
            @NotNull OrderStatus status,
            @NotNull String symbol,
            @NotNull int amount,
            @NotNull double price,
            @NotNull String lastModified,
            @NotNull User user) {
        super(id, orderType, tradeType, status, symbol, amount, price, lastModified, user);
    }

    public OptionOrder() {}
}

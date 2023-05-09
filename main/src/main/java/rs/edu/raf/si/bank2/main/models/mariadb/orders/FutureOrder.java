package rs.edu.raf.si.bank2.main.models.mariadb.orders;

import rs.edu.raf.si.bank2.main.models.mariadb.User;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@DiscriminatorValue("FUTURE_ORDER")
public class FutureOrder extends Order {
    String futureName;
    Integer stop;

    public FutureOrder(
            Long id,
            @NotNull OrderType orderType,
            @NotNull OrderTradeType tradeType,
            @NotNull OrderStatus status,
            @NotNull String symbol,
            @NotNull int amount,
            @NotNull double price,
            @NotNull String lastModified,
            @NotNull User user,
            String futureName,
            Integer stop) {
        super(id, orderType, tradeType, status, symbol, amount, price, lastModified, user);
        this.futureName = futureName;
        this.stop = stop;
    }

    public FutureOrder() {}
}

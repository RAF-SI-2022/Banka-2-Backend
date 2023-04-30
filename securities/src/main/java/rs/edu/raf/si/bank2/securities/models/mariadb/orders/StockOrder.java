package rs.edu.raf.si.bank2.securities.models.mariadb.orders;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import lombok.Data;
import rs.edu.raf.si.bank2.securities.models.mariadb.User;

@Data
@Entity
@DiscriminatorValue("STOCK_ORDER")
public class StockOrder extends Order {
    Integer stockLimit;
    Integer stop;
    boolean allOrNone;
    boolean margin;
    String currencyCode;

    public StockOrder(
            Long id,
            @NotNull OrderType orderType,
            @NotNull OrderTradeType tradeType,
            @NotNull OrderStatus status,
            @NotNull String symbol,
            @NotNull int amount,
            @NotNull double price,
            @NotNull String lastModified,
            @NotNull User user,
            Integer stockLimit,
            Integer stop,
            boolean allOrNone,
            boolean margin,
            String currencyCode) {
        super(id, orderType, tradeType, status, symbol, amount, price, lastModified, user);
        this.stockLimit = stockLimit;
        this.stop = stop;
        this.allOrNone = allOrNone;
        this.margin = margin;
        this.currencyCode = currencyCode;
    }

    public StockOrder() {}
}

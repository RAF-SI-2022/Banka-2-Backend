package com.raf.si.Banka2Backend.models.mariadb.orders;

import com.raf.si.Banka2Backend.models.mariadb.User;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@DiscriminatorValue("FOREX_ORDER")
public class ForexOrder extends Order {

    public ForexOrder(
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

    public ForexOrder() {}
}

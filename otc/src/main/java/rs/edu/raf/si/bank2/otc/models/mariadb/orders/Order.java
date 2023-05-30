package rs.edu.raf.si.bank2.otc.models.mariadb.orders;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import rs.edu.raf.si.bank2.otc.models.mariadb.User;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "orders")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderTradeType tradeType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @NotNull
    private String symbol;

    @NotNull
    private int amount;

    @NotNull
    private double price;

    @NotNull
    private String lastModified;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;
}

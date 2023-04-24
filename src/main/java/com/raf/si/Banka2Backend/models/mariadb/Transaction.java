package com.raf.si.Banka2Backend.models.mariadb;

import com.raf.si.Banka2Backend.models.mariadb.orders.Order;
import java.sql.Timestamp;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(
        name = "transactions",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "balance_id")
    @NotNull
    private Balance balance;

    @NotNull
    private Timestamp timestamp;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @NotNull
    private Order order;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @NotNull
    private String description;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    @NotNull
    private Currency currency;
    /**
     * Koliko kosta transakcija (npr 4 od 10 hartija)
     * */
    @NotNull
    private Float amount;
    /**
     * Koliko je ukupno rezervisano para za kupovinu(za svih 10 hartija) kojoj ova transakcija pripada
     * */
    @NotNull
    private Float reserved;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
}

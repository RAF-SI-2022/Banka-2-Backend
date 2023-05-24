package rs.edu.raf.si.bank2.main.models.mariadb;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "forex")
public class Forex implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String fromCurrencyCode;

    @NotNull
    private String fromCurrencyName;

    @NotNull
    private String toCurrencyCode;

    @NotNull
    private String toCurrencyName;

    @NotNull
    private String exchangeRate;

    @NotNull
    private String lastRefreshed;

    @NotNull
    private String timeZone;

    @NotNull
    private String bidPrice;

    @NotNull
    private String askPrice;
}

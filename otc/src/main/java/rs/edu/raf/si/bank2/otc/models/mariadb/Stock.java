package rs.edu.raf.si.bank2.otc.models.mariadb;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "stocks",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"symbol"})})
public class Stock implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String symbol;

    @NotBlank
    private String companyName;

    @NotNull
    private Long outstandingShares;

    @NotNull
    private BigDecimal dividendYield;

    private BigDecimal priceValue;
    private BigDecimal openValue;
    private BigDecimal lowValue;
    private BigDecimal highValue;
    private BigDecimal changeValue;
    private BigDecimal previousClose;
    private Long volumeValue;
    private LocalDate lastUpdated;
    private String changePercent;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "exchange_id", referencedColumnName = "id")
    private Exchange exchange;

    private String websiteUrl;
}

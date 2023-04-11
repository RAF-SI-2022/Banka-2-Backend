package com.raf.si.Banka2Backend.models.mariadb;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stock_history")
public class StockHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal openValue;
    private BigDecimal highValue;
    private BigDecimal lowValue;
    private BigDecimal closeValue;
    private Long volumeValue;
    private LocalDateTime onDate;

    @Enumerated(EnumType.STRING)
    private StockHistoryType type;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "stock_id", referencedColumnName = "id")
    private Stock stock;
}

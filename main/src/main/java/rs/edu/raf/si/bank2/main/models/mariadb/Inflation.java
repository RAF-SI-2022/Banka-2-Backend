package rs.edu.raf.si.bank2.main.models.mariadb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
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
@Table(
        name = "inflations",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})}) // dodaj posle i currency?
public class Inflation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer year;
    private Float inflationRate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;
}

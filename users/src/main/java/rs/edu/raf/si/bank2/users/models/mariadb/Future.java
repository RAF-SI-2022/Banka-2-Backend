package rs.edu.raf.si.bank2.users.models.mariadb;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "user")
@Builder
@AllArgsConstructor
// @RequiredArgsConstructor
@Entity
@Table(name = "futureTable")
public class Future implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Future() {}

    public Future(
            String futureName,
            Integer contractSize,
            String contractUnit,
            Integer maintenanceMargin,
            String type,
            String settlementDate,
            boolean forSale) { // open contract
        this.futureName = futureName;
        this.contractSize = contractSize;
        this.contractUnit = contractUnit;
        this.maintenanceMargin = maintenanceMargin;
        this.type = type;
        this.settlementDate = settlementDate;
        this.forSale = forSale;
    }

    public Future(Future future) { // za randomizaciju u bootstrap
        this.futureName = future.futureName;
        this.contractSize = future.contractSize;
        this.contractUnit = future.contractUnit;
        this.maintenanceMargin = future.maintenanceMargin;
        this.type = future.type;
        this.settlementDate = future.settlementDate;
        this.forSale = future.forSale;
    }

    @NotNull
    private String futureName;

    @NotNull
    private Integer contractSize;

    @NotNull
    private String contractUnit;

    @NotNull
    private Integer maintenanceMargin;

    @NotNull
    private String type;

    private String settlementDate;

    private boolean forSale;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user; // owner of this future
}

package rs.edu.raf.si.bank2.securities.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InflationDto {
    @NotNull
    private Integer year;

    @NotNull
    private Float inflationRate;

    @NotNull
    private Long currencyId;
}

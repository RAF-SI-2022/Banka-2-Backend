package rs.edu.raf.si.bank2.securities.dto;

import lombok.Data;

@Data
public class OptionSellDto {
    private Long userOptionId;
    private double premium;
}

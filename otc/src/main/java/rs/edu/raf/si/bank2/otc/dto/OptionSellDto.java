package rs.edu.raf.si.bank2.otc.dto;

import lombok.Data;

@Data
public class OptionSellDto {
    private Long userOptionId;
    private double premium;
}

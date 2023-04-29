package rs.edu.raf.si.bank2.Bank2Backend.dto;

import lombok.Data;

@Data
public class OptionBuyDto {

    private Long optionId;
    private int amount;
    private double premium;
}

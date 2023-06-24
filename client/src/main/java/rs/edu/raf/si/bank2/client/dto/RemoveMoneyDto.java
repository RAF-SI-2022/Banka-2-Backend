package rs.edu.raf.si.bank2.client.dto;

import lombok.Data;

@Data
public class RemoveMoneyDto {

    private String balanceRegNum;
    private Double amount;

    public RemoveMoneyDto(String balanceRegNum, Double amount) {
        this.balanceRegNum = balanceRegNum;
        this.amount = amount;
    }
}

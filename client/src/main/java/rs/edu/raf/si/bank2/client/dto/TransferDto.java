package rs.edu.raf.si.bank2.client.dto;

import lombok.Data;

@Data
public class TransferDto {

    private String fromBalanceRegNum;
    private String toBalanceRegNum;
    private Double amount;

    public TransferDto(String fromBalanceRegNum, String toBalanceRegNum, Double amount) {
        this.fromBalanceRegNum = fromBalanceRegNum;
        this.toBalanceRegNum = toBalanceRegNum;
        this.amount = amount;
    }

}

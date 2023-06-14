package rs.edu.raf.si.bank2.client.dto;

import lombok.Data;

@Data
public class TransferDto {

    private String fromBalanceRegNum;
    private String toBalanceRegNum;
    private String currency;
    private Double amount;

    public TransferDto(String fromBalanceRegNum, String toBalanceRegNum, String currency, Double amount) {
        this.fromBalanceRegNum = fromBalanceRegNum;
        this.toBalanceRegNum = toBalanceRegNum;
        this.currency = currency;
        this.amount = amount;
    }

}

package rs.edu.raf.si.bank2.client.dto;

import lombok.Data;

@Data
public class ExchangeDto {

    private String fromBalanceRegNum;
    private String toBalanceRegNum;
    private String exchange;
    private Double amount;

    public ExchangeDto(String fromBalanceRegNum, String toBalanceRegNum, String exchange, Double amount) {
        this.fromBalanceRegNum = fromBalanceRegNum;
        this.toBalanceRegNum = toBalanceRegNum;
        this.exchange = exchange;
        this.amount = amount;
    }

}

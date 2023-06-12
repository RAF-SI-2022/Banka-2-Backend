package rs.edu.raf.si.bank2.client.dto;

import lombok.Data;

@Data
public class BalanceDto {
    int responseCode;
    String responseMsg;

    public BalanceDto(int responseCode, String responseMsg) {
        this.responseCode = responseCode;
        this.responseMsg = responseMsg;
    }
}

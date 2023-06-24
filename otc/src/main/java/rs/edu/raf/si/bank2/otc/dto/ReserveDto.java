package rs.edu.raf.si.bank2.otc.dto;

import lombok.Data;

@Data
public class ReserveDto {

    public ReserveDto(Long userId, Long hartijaId, Integer amount) {
        this.userId = userId;
        this.hartijaId = hartijaId;
        this.amount = amount;
    }

    Long userId;
    Long hartijaId; // :)
    Integer amount;
    String futureStorage;
}

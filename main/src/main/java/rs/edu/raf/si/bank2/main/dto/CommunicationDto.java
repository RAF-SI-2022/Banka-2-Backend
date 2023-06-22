package rs.edu.raf.si.bank2.main.dto;

import lombok.Data;

@Data
public class CommunicationDto {
    int responseCode;
    String responseMsg;

    public CommunicationDto(int responseCode, String responseMsg) {
        this.responseCode = responseCode;
        this.responseMsg = responseMsg;
    }
}

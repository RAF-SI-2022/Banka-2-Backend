package rs.edu.raf.si.bank2.main.dto;

import lombok.Data;

import java.net.HttpURLConnection;

@Data
public class CommunicationDto {
    int responseCode;
    String responseMsg;

    public CommunicationDto(int responseCode, String responseMsg) {
        System.out.println("NAPRAVILI DTO SA " + responseCode + " " + responseMsg);
        this.responseCode = responseCode;
        this.responseMsg = responseMsg;
    }
}

package rs.edu.raf.si.bank2.client.responses;

import lombok.Data;

@Data
public class ClientLoginResponse {
    private String token;

    public ClientLoginResponse(String token) {
        this.token = token;
    }
}

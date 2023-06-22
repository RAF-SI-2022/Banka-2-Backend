package rs.edu.raf.si.bank2.client.responses;

import lombok.Data;
import rs.edu.raf.si.bank2.client.models.mariadb.Permission;

import java.util.List;

@Data
public class ClientLoginResponse {
    private String token;

    public ClientLoginResponse(String token) {
        this.token = token;
    }
}

package rs.edu.raf.si.bank2.main.responses;

import rs.edu.raf.si.bank2.main.models.mariadb.Permission;
import java.util.List;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private List<Permission> permissions;

    public LoginResponse(String token, List<Permission> permissions) {
        this.token = token;
        this.permissions = permissions;
    }
}

package rs.edu.raf.si.bank2.main.requests;

import rs.edu.raf.si.bank2.main.models.mariadb.PermissionName;
import java.util.List;
import lombok.Data;

@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<PermissionName> permissions;
    private String jobPosition;
    private boolean active;
    private String jmbg;
    private Double dailyLimit;
    private String phone;
}

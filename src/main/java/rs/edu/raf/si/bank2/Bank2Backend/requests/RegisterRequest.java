package rs.edu.raf.si.bank2.Bank2Backend.requests;

import java.util.List;
import lombok.Data;
import rs.edu.raf.si.bank2.Bank2Backend.models.mariadb.PermissionName;

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

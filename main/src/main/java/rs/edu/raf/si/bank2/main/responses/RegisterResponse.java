package rs.edu.raf.si.bank2.main.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import rs.edu.raf.si.bank2.main.models.mariadb.Permission;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    @JsonIgnore
    private String password;

    private List<Permission> permissions;
    private String jobPosition;
    private boolean active;
    private String jmbg;
    private Double dailyLimit;
    private Double defaultDailyLimit;
    private String phone;
}

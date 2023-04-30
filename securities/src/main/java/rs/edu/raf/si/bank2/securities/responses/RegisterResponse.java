package rs.edu.raf.si.bank2.securities.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import rs.edu.raf.si.bank2.securities.models.mariadb.Permission;

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

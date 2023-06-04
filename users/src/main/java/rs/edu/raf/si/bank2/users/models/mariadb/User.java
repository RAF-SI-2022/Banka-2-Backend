package rs.edu.raf.si.bank2.users.models.mariadb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(
        name = "users",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"email", "jmbg"})})
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 320657560973525070L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "This field is required.")
    @Size(max = 50, message = "Input too long, cannot contain more than 50 characters.")
    @Email
    private String email;

    @NotNull(message = "This field is required.")
    private String password;

    @NotNull(message = "This field is required.")
    @Size(max = 20, message = "Input too long, cannot contain more than 20 characters.")
    private String firstName;

    @NotNull(message = "This field is required.")
    @Size(max = 20, message = "Input too long, cannot contain more than 20 characters.")
    private String lastName;

    @NotNull(message = "This field is required.")
    @Size(max = 13, message = "Input too long, cannot contain more than 13 characters.")
    private String jmbg;

    @NotNull(message = "This field is required.")
    @Size(max = 20, message = "Input too long, cannot contain more than 20 characters.")
    private String phone;

    @NotNull(message = "This field is required.")
    @Size(max = 20, message = "Input too long, cannot contain more than 20 characters.")
    private String jobPosition;

    private boolean active;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_permissions",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id", referencedColumnName = "id")})
    private List<Permission> permissions;

    private Double dailyLimit;

    private Double defaultDailyLimit;
}

package rs.edu.raf.si.bank2.users.dto;

import lombok.Data;
import rs.edu.raf.si.bank2.users.models.mariadb.User;

@Data
public class ChangePassDto {
    User user;
    String newPass;
    String passResetToken;
}

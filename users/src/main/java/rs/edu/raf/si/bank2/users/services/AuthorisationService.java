package rs.edu.raf.si.bank2.users.services;

import java.util.Calendar;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.users.models.mariadb.PasswordResetToken;
import rs.edu.raf.si.bank2.users.models.mariadb.Permission;
import rs.edu.raf.si.bank2.users.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.users.models.mariadb.User;
import rs.edu.raf.si.bank2.users.repositories.mariadb.PasswordResetTokenRepository;
import rs.edu.raf.si.bank2.users.repositories.mariadb.PermissionRepository;
import rs.edu.raf.si.bank2.users.repositories.mariadb.UserRepository;

@Service
public class AuthorisationService {

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    public AuthorisationService(
            PermissionRepository permissionRepository,
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository) {
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public boolean isAuthorised(PermissionName permissionRequired, String userEmail) {
        Optional<User> userOptional = this.userRepository.findUserByEmail(userEmail);
        if (!userOptional.isPresent()) {
            return false;
        }
        User user = userOptional.get();
        for (Permission p : user.getPermissions()) {
            if (p.getPermissionName().equals(permissionRequired)
                    || p.getPermissionName().equals(PermissionName.ADMIN_USER)) {
                return true;
            }
        }
        return false;
    }

    public String validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> passwordResetToken =
                passwordResetTokenRepository.findPasswordResetTokenByToken(token);
        if (passwordResetToken.isEmpty()) return "Token not found";

        Calendar cal = Calendar.getInstance();
        if (passwordResetToken.get().getExpirationDate().before(cal.getTime())) {
            this.passwordResetTokenRepository.delete(passwordResetToken.get());
            return "Token expired";
        }

        return "";
    }
}

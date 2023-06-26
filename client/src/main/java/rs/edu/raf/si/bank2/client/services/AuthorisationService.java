package rs.edu.raf.si.bank2.client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.client.models.mariadb.PasswordResetToken;
import rs.edu.raf.si.bank2.client.models.mariadb.Permission;
import rs.edu.raf.si.bank2.client.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.client.models.mariadb.User;
import rs.edu.raf.si.bank2.client.services.interfaces.AuthorisationServiceInterface;
import rs.edu.raf.si.bank2.client.services.interfaces.MailingServiceInterface;
import rs.edu.raf.si.bank2.client.utils.JwtUtil;
import rs.edu.raf.si.bank2.client.repositories.mariadb.PasswordResetTokenRepository;
import rs.edu.raf.si.bank2.client.repositories.mariadb.PermissionRepository;
import rs.edu.raf.si.bank2.client.repositories.mariadb.UserRepository;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthorisationService implements AuthorisationServiceInterface {

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MailingServiceInterface mailingService;

    @Autowired
    public AuthorisationService(
            AuthenticationManager authenticationManager,
            PermissionRepository permissionRepository,
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            JwtUtil jwtUtil,
            MailingServiceInterface mailingService) {
        this.authenticationManager = authenticationManager;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.jwtUtil = jwtUtil;
        this.mailingService = mailingService;
    }

    @Override
    public boolean isAuthorised(PermissionName permissionRequired, String userEmail) {
        Optional<User> userOptional = this.userRepository.findUserByEmail(userEmail);
        if (userOptional.isEmpty()) {
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



}

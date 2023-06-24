package rs.edu.raf.si.bank2.users.services;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.users.models.mariadb.PasswordResetToken;
import rs.edu.raf.si.bank2.users.models.mariadb.Permission;
import rs.edu.raf.si.bank2.users.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.users.models.mariadb.User;
import rs.edu.raf.si.bank2.users.repositories.mariadb.PasswordResetTokenRepository;
import rs.edu.raf.si.bank2.users.repositories.mariadb.PermissionRepository;
import rs.edu.raf.si.bank2.users.repositories.mariadb.UserRepository;
import rs.edu.raf.si.bank2.users.services.interfaces.AuthorisationServiceInterface;
import rs.edu.raf.si.bank2.users.services.interfaces.MailingServiceInterface;
import rs.edu.raf.si.bank2.users.utils.JwtUtil;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AuthorisationService implements AuthorisationServiceInterface {

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MailingServiceInterface mailingService;

    /**
     * Monitoring. Counts the number of JWT tokens generated.
     */
    private Counter tokensCount;

    /**
     * Monitoring. Counts the number of reset password tokens generated.
     */
    private Counter resetPasswordCount;

    /**
     * Default constructor.
     *
     * @param authenticationManager
     * @param permissionRepository
     * @param userRepository
     * @param passwordResetTokenRepository
     * @param jwtUtil
     * @param mailingService
     * @param meterRegistry
     */
    @Autowired
    public AuthorisationService(
            AuthenticationManager authenticationManager,
            PermissionRepository permissionRepository,
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            JwtUtil jwtUtil,
            MailingServiceInterface mailingService,
            CompositeMeterRegistry meterRegistry
    ) {
        this.authenticationManager = authenticationManager;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.jwtUtil = jwtUtil;
        this.mailingService = mailingService;
        tokensCount = meterRegistry.counter("services.authorisation.tokens");
        resetPasswordCount = meterRegistry.counter("services.authorisation.resetPassword");
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

    @Override
    public Optional<String> login(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (Exception e) {
            return Optional.empty();
        }

        String token = jwtUtil.generateToken(email);
        tokensCount.increment();
        return Optional.of(token);
    }

    @Override
    public boolean requestPasswordResetToken(String email) {
        Optional<User> user = userRepository.findUserByEmail(email);
        if (user.isEmpty()) {
            return false;
        }

        User client = user.get();
        // TODO random enough?
        String token = UUID.randomUUID().toString();
        passwordResetTokenRepository.save(new PasswordResetToken(client, token));
        resetPasswordCount.increment();
        mailingService.sendResetPasswordEmail(email, token);
        return true;
    }

    @Override
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

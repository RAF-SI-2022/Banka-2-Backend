package rs.edu.raf.si.bank2.otc.services.interfaces;

import java.util.Optional;
import rs.edu.raf.si.bank2.otc.models.mariadb.PermissionName;

/**
 * Service for dealing with authentication-related jobs, where there is no
 * currently logged-in user.
 */
public interface AuthorisationServiceInterface {

    /**
     * Logs in the user with the specified email and password. If successful,
     * returns a JWT token for further requests; otherwise, returns empty.
     *
     * @param email    user email
     * @param password user password
     * @return JWT token or empty
     */
    Optional<String> login(String email, String password);

    /**
     * Requests a password reset for the user with the given email.
     *
     * @param email user email
     * @return true if successful, false otherwise
     */
    boolean requestPasswordResetToken(String email);

    boolean isAuthorised(PermissionName permissionRequired, String userEmail);

    String validatePasswordResetToken(String token);
}

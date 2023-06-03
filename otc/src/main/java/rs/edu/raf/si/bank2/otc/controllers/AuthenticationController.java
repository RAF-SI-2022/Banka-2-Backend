package rs.edu.raf.si.bank2.otc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.otc.dto.PasswordRecoveryDto;
import rs.edu.raf.si.bank2.otc.models.mariadb.User;
import rs.edu.raf.si.bank2.otc.requests.LoginRequest;
import rs.edu.raf.si.bank2.otc.requests.PasswordResetRequest;
import rs.edu.raf.si.bank2.otc.responses.LoginResponse;
import rs.edu.raf.si.bank2.otc.services.UserService;
import rs.edu.raf.si.bank2.otc.services.interfaces.AuthorisationServiceInterface;
import rs.edu.raf.si.bank2.otc.utils.JwtUtil;

import java.util.Optional;

/**
 * Controller for requests by users that are NOT logged in. Accessible by
 * anyone.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final JwtUtil jwtUtil;
    private final AuthorisationServiceInterface authorisationService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationController(
            JwtUtil jwtUtil,
            AuthorisationServiceInterface authorisationService,
            UserService userService,
            PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.authorisationService = authorisationService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * API endpoint for user login via traditional email-password combination.
     * See {@link LoginRequest} for request body details. If successful, the
     * response contains a JWT token for further user requests and a list
     * of the user's permissions.
     *
     * @param loginRequest request body
     * @return 200 if ok with JWT token and user permissions, 401 otherwise
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<String> token = authorisationService.login(loginRequest.getEmail(), loginRequest.getPassword());

        if (token.isEmpty()) return ResponseEntity.status(401).body("Pogresni kredencijali.");

        LoginResponse responseDto = new LoginResponse(
                jwtUtil.generateToken(loginRequest.getEmail()),
                userService.getUserPermissions(loginRequest.getEmail()));
        return ResponseEntity.ok(responseDto);
    }

    /**
     * API endpoint for requesting a password reset token. Creates a new
     * password reset token that is sent to the user via an external
     * communication method (NOT returned in the response). See
     * {@link PasswordResetRequest} for request details.
     *
     * @param passwordResetRequest request body
     * @return 200 if ok, 401 if bad credentials
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest) {
        boolean success = authorisationService.requestPasswordResetToken(passwordResetRequest.getEmail());

        // TODO ovde treba vratiti 200 uvek, jer inace ovo predstavlja metod
        //  za trazenje emailova u nasoj bazi
        if (!success) return ResponseEntity.status(401).body("Pogresni kredencijali.");

        return ResponseEntity.status(200).build();
    }

    /**
     * API endpoint for changing the user password, based on a valid token
     * and new password. See {@link PasswordRecoveryDto} for request body
     * details.
     *
     * @param passwordRecoveryDto request body
     * @return 200 if ok, 401 if not authorized or token expired or bad user
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordRecoveryDto passwordRecoveryDto) {
        String result = this.authorisationService.validatePasswordResetToken(passwordRecoveryDto.getToken());

        if (result.equals("Token not found")) return ResponseEntity.status(401).body("Token nije pronadjen.");
        if (result.equals("Token expired")) return ResponseEntity.status(401).body("Token je istekao.");

        Optional<User> user = this.userService.getUserByPasswordResetToken(passwordRecoveryDto.getToken());
        if (user.isEmpty()) return ResponseEntity.status(401).body("Nije moguce promeniti " + "sifru.");

        this.userService.changePassword(
                user.get(),
                this.passwordEncoder.encode(passwordRecoveryDto.getNewPassword()),
                passwordRecoveryDto.getToken());

        return ResponseEntity.ok().build();
    }
}

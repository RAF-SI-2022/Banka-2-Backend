package rs.edu.raf.si.bank2.otc.controllers;

import java.util.HashMap;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.otc.dto.PasswordRecoveryDto;
import rs.edu.raf.si.bank2.otc.models.mariadb.User;
import rs.edu.raf.si.bank2.otc.requests.LoginRequest;
import rs.edu.raf.si.bank2.otc.responses.LoginResponse;
import rs.edu.raf.si.bank2.otc.services.AuthorisationService;
import rs.edu.raf.si.bank2.otc.services.MailingService;
import rs.edu.raf.si.bank2.otc.services.UserService;
import rs.edu.raf.si.bank2.otc.utils.JwtUtil;

/**
 * TODO THIS CLASS SHOULD BE REMOVED. ITS FUNCTIONALITY HAS BEEN REPLACED BY
 *   THE USERS SERVICE.
 *
 * @deprecated this class should be removed, all authorisation-related things
 * should be moved over to the users service
 */
@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MailingService mailingService;
    private final AuthorisationService authorisationService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            MailingService mailingService,
            AuthorisationService authorisationService,
            UserService userService,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.mailingService = mailingService;
        this.jwtUtil = jwtUtil;
        this.authorisationService = authorisationService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body("Pogresni kredencijali.");
        }
        LoginResponse responseDto = new LoginResponse(
                jwtUtil.generateToken(loginRequest.getEmail()),
                userService.getUserPermissions(loginRequest.getEmail()));
        // TokenResponseDto responseDto = new
        // TokenResponseDto(jwtUtil.generateToken(tokenRequestDto.getEmail()),
        // userService.getUserPermissions(tokenRequestDto.getEmail()));
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody HashMap<String, String> email) {
        this.mailingService.sendResetPasswordMail(email.get("email"));
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String token) {
        String result = this.authorisationService.validatePasswordResetToken(token);
        if (result.equals("Token not found")) return ResponseEntity.status(405).body("Token nije pronadjen.");
        if (result.equals("Token expired")) return ResponseEntity.status(405).body("Token je istekao.");

        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-user-password")
    public ResponseEntity<?> changeUserPassword(@RequestBody PasswordRecoveryDto passwordRecoveryDto) {
        String result = this.authorisationService.validatePasswordResetToken(passwordRecoveryDto.getToken());
        if (result.equals("Token not found")) return ResponseEntity.status(405).body("Token nije pronadjen.");
        if (result.equals("Token expired")) return ResponseEntity.status(405).body("Token je istekao.");

        Optional<User> user = this.userService.getUserByPasswordResetToken(passwordRecoveryDto.getToken());
        if (user.isEmpty()) return ResponseEntity.status(405).body("Nije moguce promeniti sifru.");

        this.userService.changePassword(
                user.get(),
                this.passwordEncoder.encode(passwordRecoveryDto.getNewPassword()),
                passwordRecoveryDto.getToken());

        return ResponseEntity.ok().build();
    }
}

package rs.edu.raf.si.bank2.users.controllers;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.users.dto.PasswordRecoveryDto;
import rs.edu.raf.si.bank2.users.models.mariadb.User;
import rs.edu.raf.si.bank2.users.requests.LoginRequest;
import rs.edu.raf.si.bank2.users.responses.LoginResponse;
import rs.edu.raf.si.bank2.users.services.AuthorisationService;
import rs.edu.raf.si.bank2.users.services.UserService;
import rs.edu.raf.si.bank2.users.utils.JwtUtil;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthorisationService authorisationService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            AuthorisationService authorisationService,
            UserService userService,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
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
        return ResponseEntity.ok(responseDto);
    }

}

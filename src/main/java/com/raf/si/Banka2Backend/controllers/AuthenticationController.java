package com.raf.si.Banka2Backend.controllers;

import com.raf.si.Banka2Backend.dto.PasswordRecoveryDto;
import com.raf.si.Banka2Backend.models.User;
import com.raf.si.Banka2Backend.requests.LoginRequest;
import com.raf.si.Banka2Backend.responses.LoginResponse;
import com.raf.si.Banka2Backend.services.AuthorisationService;
import com.raf.si.Banka2Backend.services.MailingService;
import com.raf.si.Banka2Backend.services.UserService;
import com.raf.si.Banka2Backend.utils.JwtUtil;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
          new UsernamePasswordAuthenticationToken(
              loginRequest.getEmail(), loginRequest.getPassword()));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(401).body("Bad credentials.");
    }
    LoginResponse responseDto =
        new LoginResponse(
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
    if (result.equals("Token not found")) return ResponseEntity.status(405).body("Token not found");
    if (result.equals("Token expired")) return ResponseEntity.status(405).body("Token expired");

    return ResponseEntity.ok("Token valid");
  }

  @PostMapping("/change-user-password")
  public ResponseEntity<?> changeUserPassword(
      @RequestBody PasswordRecoveryDto passwordRecoveryDto) {
    String result =
        this.authorisationService.validatePasswordResetToken(passwordRecoveryDto.getToken());
    if (result.equals("Token not found")) return ResponseEntity.status(405).body("Token not found");
    if (result.equals("Token expired")) return ResponseEntity.status(405).body("Token expired");

    Optional<User> user =
        this.userService.getUserByPasswordResetToken(passwordRecoveryDto.getToken());
    if (user.isEmpty()) return ResponseEntity.status(405).body("Could not change user password");

    this.userService.changePassword(
        user.get(),
        this.passwordEncoder.encode(passwordRecoveryDto.getNewPassword()),
        passwordRecoveryDto.getToken());

    return ResponseEntity.ok().build();
  }
}

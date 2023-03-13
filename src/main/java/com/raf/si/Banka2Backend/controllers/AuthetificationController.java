package com.raf.si.Banka2Backend.controllers;

import com.raf.si.Banka2Backend.requests.LoginRequest;
import com.raf.si.Banka2Backend.responses.LoginResponse;
import com.raf.si.Banka2Backend.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthetificationController {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

  public AuthetificationController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
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
    return ResponseEntity.ok(new LoginResponse(jwtUtil.generateToken(loginRequest.getEmail())));
  }
}

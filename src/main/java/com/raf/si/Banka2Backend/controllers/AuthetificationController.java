package com.raf.si.Banka2Backend.controllers;
import com.raf.si.Banka2Backend.requests.LoginRequest;
import com.raf.si.Banka2Backend.responses.LoginResponse;
import com.raf.si.Banka2Backend.services.UserService;
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
    private final UserService userService;


    public AuthetificationController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(401).body("Bad credentials.");
        }
        LoginResponse responseDto = new LoginResponse(jwtUtil.generateToken(loginRequest.getEmail()), userService.getUserPermissions(loginRequest.getEmail()));
        // TokenResponseDto responseDto = new TokenResponseDto(jwtUtil.generateToken(tokenRequestDto.getEmail()), userService.getUserPermissions(tokenRequestDto.getEmail()));
        return ResponseEntity.ok(responseDto);

    }
}

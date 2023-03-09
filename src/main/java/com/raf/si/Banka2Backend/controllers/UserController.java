package com.raf.si.Banka2Backend.controllers;

import com.raf.si.Banka2Backend.models.PermissionName;
import com.raf.si.Banka2Backend.models.User;
import com.raf.si.Banka2Backend.requests.RegisterRequest;
import com.raf.si.Banka2Backend.responses.RegisterResponse;
import com.raf.si.Banka2Backend.services.AuthorisationService;
import com.raf.si.Banka2Backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthorisationService authorisationService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, AuthorisationService authorisationService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.authorisationService = authorisationService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value="/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest user) {


        Optional<User> existingUser = userService.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            return ResponseEntity.status(400).body("User with that email already exists");
        }
        String signedInUserEmail = getContext().getAuthentication().getName();
        if(!authorisationService.isAuthorised(PermissionName.CREATE_USERS, signedInUserEmail)){
            return ResponseEntity.status(401).build();
        }

        User newUser = User.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password( this.passwordEncoder.encode(user.getPassword()))
                .jmbg(user.getJmbg())
                .phone(user.getPhone())
                .jobPosition(user.getJobPosition())
                .active(user.isActive())
                .permissions(user.getPermissions())
                .build();

        userService.save(newUser);

        RegisterResponse response = RegisterResponse.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password( this.passwordEncoder.encode(user.getPassword()))
                .jmbg(user.getJmbg())
                .phone(user.getPhone())
                .jobPosition(user.getJobPosition())
                .active(user.isActive())
                .permissions(user.getPermissions())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<List<User>> findAll(){
        String signedInUserEmail = getContext().getAuthentication().getName();
        if(!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)){
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok().body(userService.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> findById(@PathVariable(name = "id") Long id){
        String signedInUserEmail = getContext().getAuthentication().getName();
        if(!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)){
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok().body(Optional.ofNullable(userService.findById(id)).orElse(null));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable(name = "id") Long id){
        String signedInUserEmail = getContext().getAuthentication().getName();
        if(!authorisationService.isAuthorised(PermissionName.DELETE_USERS, signedInUserEmail)){
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok().body(Optional.ofNullable(userService.findById(id)).orElse(null));
    }
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@RequestBody User user){

        Optional<User> updatedUser = userService.findById(user.getId());
        if(updatedUser.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        String signedInUserEmail = getContext().getAuthentication().getName();
        if(!authorisationService.isAuthorised(PermissionName.UPDATE_USERS, signedInUserEmail)){
            return ResponseEntity.status(401).build();
        }
        updatedUser = Optional.ofNullable(User.builder()

                .id(updatedUser.get().getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password(this.passwordEncoder.encode(user.getPassword()))
                .jmbg(user.getJmbg())
                .jobPosition(user.getJobPosition())
                .active(user.isActive())
                .permissions(user.getPermissions())
                .build());

        return ResponseEntity.ok().body(userService.save(updatedUser.get()));
    }
}

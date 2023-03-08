package com.raf.si.Banka2Backend.controllers;

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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest user) {


        Optional<User> existingUser = userService.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            return ResponseEntity.status(400).body("User with that email already exists");
        }

        User newUser = User.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password( this.passwordEncoder.encode(user.getPassword()))
                .jmbg(user.getJmbg())
                .pozicija(user.getPozicija())
                .aktivan(user.isAktivan())
                .permissions(user.getPermissions())
                .build();

        userService.save(newUser);

        RegisterResponse response = RegisterResponse.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password( this.passwordEncoder.encode(user.getPassword()))
                .jmbg(user.getJmbg())
                .pozicija(user.getPozicija())
                .aktivan(user.isAktivan())
                .permissions(user.getPermissions())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public List<User> findAll(){
        return userService.findAll();
    }
    @PostMapping
    public User save(@RequestBody User user){
        return userService.save(user);
    }
    @GetMapping("/{id}")
    public Optional<User> findById(@PathVariable(name = "id") Long id){
        return Optional.ofNullable(userService.findById(id)).orElse(null);
    }
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable(name = "id") Long id){
        userService.deleteById(id);
    }
    @PutMapping
    public User updateUser(@RequestBody User user){
        Optional<User> updatedUser = findById(user.getId());
        if(updatedUser.isEmpty()){
            // logger goes here instead of sout and exception goes instead of null value
            System.out.println("Wrong id!");
            return null;
        }
        updatedUser = Optional.ofNullable(User.builder()

                .id(updatedUser.get().getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password(this.passwordEncoder.encode(user.getPassword()))
                .jmbg(user.getJmbg())
                .pozicija(user.getPozicija())
                .aktivan(user.isAktivan())
                .permissions(user.getPermissions())
                .build());


        return save(updatedUser.get());
    }
}

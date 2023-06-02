package rs.edu.raf.si.bank2.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.users.dto.ChangePassDto;
import rs.edu.raf.si.bank2.users.dto.UserDto;
import rs.edu.raf.si.bank2.users.models.mariadb.User;
import rs.edu.raf.si.bank2.users.services.UserService;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;


@RestController
@CrossOrigin
@RequestMapping("/api/userService")
public class UserServiceController {

    private final UserService userService;


    @Autowired
    public UserServiceController(UserService userService) {
        this.userService = userService;
    }



    @GetMapping(value = "/loadUserByUsername/{username}")
    public ResponseEntity<?> loadUserByUsername(@PathVariable(name = "username") String username) {
        return ResponseEntity.ok().body(userService.loadUserByUsername(username));
    }

    @GetMapping(value = "/findByEmail")
    public ResponseEntity<?> findByEmail() {
        String userEmail = getContext().getAuthentication().getName();
        return ResponseEntity.ok().body(userService.findByEmail(userEmail));
    }

    @GetMapping(value = "/findAll")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok().body(userService.findAll());
    }

    @PostMapping(value = "/save")
    public ResponseEntity<?> save(@RequestBody User user) {
        return  ResponseEntity.ok().body(userService.save(user));
    }

    @GetMapping(value = "/getUserPermissions")
    public ResponseEntity<?> getUserPermissions() {
        String userEmail = getContext().getAuthentication().getName();
        return  ResponseEntity.ok().body(userService.getUserPermissions(userEmail));
    }

    @GetMapping(value = "/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok().body(userService.findById(id));
    }

    @DeleteMapping(value = "/deleteById/{id}")
    public ResponseEntity<?> deleteById(@PathVariable(name = "id") Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok().body("User deleted");
    }

    @GetMapping(value = "/getUserByPasswordResetToken/{token}")
    public ResponseEntity<?> getUserByPasswordResetToken(@PathVariable(name = "token") String token) {
        return  ResponseEntity.ok().body(userService.getUserByPasswordResetToken(token));
    }

    @PatchMapping(value = "/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePassDto dto) {
        userService.changePassword(dto.getUser(), dto.getNewPass(), dto.getPassResetToken());
        return  ResponseEntity.ok().body("Password changed");
    }

    @PatchMapping(value = "/changeUsersDailyLimit/{limit}")
    public ResponseEntity<?> changeUsersDailyLimit(@PathVariable(name = "limit") Double limit) {
        String userEmail = getContext().getAuthentication().getName();
        return ResponseEntity.ok().body(userService.changeUsersDailyLimit(userEmail, limit));
    }

    @GetMapping(value = "/limit")
    public ResponseEntity<?> getUsersDailyLimit() {
        String userEmail = getContext().getAuthentication().getName();
        return ResponseEntity.ok().body(userService.getUsersDailyLimit(userEmail));
    }

}

package rs.edu.raf.si.bank2.users.controllers;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.edu.raf.si.bank2.users.exceptions.CurrencyNotFoundException;
import rs.edu.raf.si.bank2.users.exceptions.UserNotFoundException;
import rs.edu.raf.si.bank2.users.models.mariadb.*;
import rs.edu.raf.si.bank2.users.requests.ChangePasswordRequest;
import rs.edu.raf.si.bank2.users.requests.RegisterRequest;
import rs.edu.raf.si.bank2.users.requests.UpdateProfileRequest;
import rs.edu.raf.si.bank2.users.requests.UpdateUserRequest;
import rs.edu.raf.si.bank2.users.responses.RegisterResponse;
import rs.edu.raf.si.bank2.users.services.*;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final PermissionService permissionService;
    private final AuthorisationService authorisationService;
    private final PasswordEncoder passwordEncoder;
    private final CurrencyService currencyService;
    private final BalanceService balanceService;

    @Autowired
    public UserController(
            UserService userService,
            PermissionService permissionService,
            AuthorisationService authorisationService,
            PasswordEncoder passwordEncoder,
            CurrencyService currencyService,
            BalanceService balanceService) {
        this.userService = userService;
        this.permissionService = permissionService;
        this.authorisationService = authorisationService;
        this.passwordEncoder = passwordEncoder;
        this.currencyService = currencyService;
        this.balanceService = balanceService;
    }

    @GetMapping(value = "/permissions")
    public ResponseEntity<?> getAllPermissions() {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.ADMIN_USER, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }
        return ResponseEntity.ok(this.permissionService.findAll());
    }

    @GetMapping(value = "/permissions/{id}")
    public ResponseEntity<?> getAllUserPermissions(@PathVariable(name = "id") Long id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }
        Optional<User> userOptional = this.userService.findById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(400).body("User sa id-em " + id + " nije pronadjen.");
        }
        return ResponseEntity.ok(userOptional.get().getPermissions());
    }

    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest user) {

        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.CREATE_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da kreirate korisnike.");
        }
        Optional<User> existingUser = userService.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(400).body("Korisnik sa email-om " + user.getEmail() + " vec postoji.");
        }

        List<Permission> permissions = this.permissionService.findByPermissionNames(user.getPermissions());

        User newUser = User.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password(this.passwordEncoder.encode(user.getPassword()))
                .jmbg(user.getJmbg())
                .phone(user.getPhone())
                .jobPosition(user.getJobPosition())
                .active(user.isActive())
                .permissions(permissions)
                .dailyLimit(
                        user.getDailyLimit()
                        //                                user.getDailyLimit() == -1D ? null : user.getDailyLimit()
                        )
                .defaultDailyLimit(user.getDailyLimit())
                .build();

        userService.save(newUser); // mora duplo zbog balansa
        setInitialUserBalance(newUser);
        userService.save(newUser);

        RegisterResponse response = RegisterResponse.builder()
                .id(newUser.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .jmbg(user.getJmbg())
                .phone(user.getPhone())
                .jobPosition(user.getJobPosition())
                .active(user.isActive())
                .permissions(permissions)
                .dailyLimit(user.getDailyLimit())
                .defaultDailyLimit(user.getDailyLimit())
                .build();

        return ResponseEntity.ok(response);
    }

    private void setInitialUserBalance(User user) { // todo ovo promeni kasnije da nemaju odmah 100.000 $
        Balance balance = new Balance();
        balance.setUser(user);
        Optional<Currency> rsd = this.currencyService.findByCurrencyCode("RSD");
        if (rsd.isEmpty()) throw new CurrencyNotFoundException("RSD");
        balance.setCurrency(rsd.get());
        balance.setFree(100000f);
        balance.setType(BalanceType.CASH);
        balance.setReserved(0f);
        balance.setAmount(100000f);

        Balance balance2 = new Balance();
        balance2.setUser(user);
        Optional<Currency> usd = this.currencyService.findByCurrencyCode("USD");
        if (usd.isEmpty()) throw new CurrencyNotFoundException("USD");
        balance2.setCurrency(usd.get());
        balance2.setAmount(100000f);
        balance2.setFree(100000f);
        balance2.setType(BalanceType.CASH);
        balance2.setReserved(0f);

        List<Balance> balances = new ArrayList<>();
        balances.add(balance);
        balances.add(balance2);
        user.setBalances(balances);
        this.balanceService.save(balance);
        this.balanceService.save(balance2);
    }

    @GetMapping()
    public ResponseEntity<?> findAll() {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da pristupite korisnicima.");
        }
        return ResponseEntity.ok().body(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable(name = "id") Long id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da pristupite korisnicima.");
        }
        return ResponseEntity.ok().body(userService.findById(id));
    }

    @GetMapping("/email")
    public ResponseEntity<?> findByEmail() {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da pristupite korisnicima.");
        }
        return ResponseEntity.ok().body(userService.findByEmail(signedInUserEmail));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable(name = "id") Long id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.DELETE_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da brisete korisnike.");
        }
        try {
            userService.deleteById(id);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/reactivate/{id}")
    public ResponseEntity<?> reactivateUser(@PathVariable(name = "id") Long id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.ADMIN_USER, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da aktivirate korisnika.");
        }
        Optional<User> userOptional = this.userService.findById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(400)
                    .body("Ne mozete aktivirati korisnika sa id-om  " + id + ", zato sto ne postoji");
        }
        if (userOptional.get().isActive()) {
            return ResponseEntity.status(400)
                    .body("Ne mozete aktivirati korisnika sa id-om " + id + ", zato sto je vec aktiviran");
        }
        // Reactivating user after delete - setting active to true
        User user = userOptional.get();
        user.setActive(true);
        return ResponseEntity.ok().body(this.userService.save(user));
    }

    @PostMapping("/deactivate/{id}")
    public ResponseEntity<?> deactivateUser(@PathVariable(name = "id") Long id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.ADMIN_USER, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da deaktivirate korisnika.");
        }
        Optional<User> userOptional = this.userService.findById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(400)
                    .body("Ne mozete deaktivirati korisnika sa id-om  " + id + ", zato sto ne postoji");
        }
        if (!userOptional.get().isActive()) {
            return ResponseEntity.status(400)
                    .body("Ne mozete deaktivirati korisnika sa id-om " + id + ", zato sto je vec deaktiviran");
        }
        User user = userOptional.get();
        user.setActive(false);
        return ResponseEntity.ok().body(this.userService.save(user));
    }

    @PutMapping("/edit-profile/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable(name = "id") Long id, @RequestBody UpdateProfileRequest user) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        Optional<User> logovan = userService.findByEmail(signedInUserEmail);

        if (logovan.isPresent()) {
            if (!logovan.get().getId().equals(id)) {
                return ResponseEntity.status(401).body("Nemate dozvolu da modifikujete korisnika.");
            }
        } else {
            return ResponseEntity.status(401).body("Doslo je do neocekivane greske.");
        }
        Optional<User> updatedUser = userService.findById(id);
        if (updatedUser.isEmpty()) {
            return ResponseEntity.status(400).body("Doslo je do greske pri trazenju korisnika sa id-em " + id);
        }

        updatedUser = Optional.ofNullable(User.builder()
                .id(updatedUser.get().getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .active(updatedUser.get().isActive())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .password(updatedUser.get().getPassword())
                .jmbg(updatedUser.get().getJmbg())
                .jobPosition(updatedUser.get().getJobPosition())
                .permissions(updatedUser.get().getPermissions())
                .dailyLimit(updatedUser.get().getDailyLimit())
                .defaultDailyLimit(updatedUser.get().getDefaultDailyLimit())
                .build());
        return ResponseEntity.ok().body(userService.save(updatedUser.get()));
    }

    @PutMapping("/password/{id}")
    public ResponseEntity<?> changePassword(
            @PathVariable(name = "id") Long id, @RequestBody ChangePasswordRequest user) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        Optional<User> logovan = userService.findByEmail(signedInUserEmail);

        if (logovan.isPresent()) {
            if (!logovan.get().getId().equals(id)) {
                return ResponseEntity.status(401).body("Nemate dozvolu da mofidikujete korisnika.");
            }
        }
        Optional<User> updatedUser = userService.findById(id);
        if (updatedUser.isEmpty()) {
            return ResponseEntity.status(400).body("Doslo je do greske pri trazenju korisnika sa id-em " + id);
        }

        updatedUser = Optional.ofNullable(User.builder()
                .id(updatedUser.get().getId())
                .firstName(updatedUser.get().getFirstName())
                .lastName(updatedUser.get().getLastName())
                .password(this.passwordEncoder.encode(user.getPassword()))
                .email(updatedUser.get().getEmail())
                .jmbg(updatedUser.get().getJmbg())
                .active(updatedUser.get().isActive())
                .jobPosition(updatedUser.get().getJobPosition())
                .permissions(updatedUser.get().getPermissions())
                .phone(updatedUser.get().getPhone())
                .dailyLimit(updatedUser.get().getDailyLimit())
                .defaultDailyLimit(updatedUser.get().getDefaultDailyLimit())
                .build());
        return ResponseEntity.ok().body(userService.save(updatedUser.get()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable(name = "id") Long id, @RequestBody UpdateUserRequest user) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.UPDATE_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da mofidikujete korisnika.");
        }
        Optional<User> updatedUser = userService.findById(id);
        if (updatedUser.isEmpty()) {
            return ResponseEntity.status(400).body("Doslo je do greske pri trazenju korisnika sa id-em  " + id);
        }

        List<Permission> permissions = this.permissionService.findByPermissionNames(user.getPermissions());

        updatedUser = Optional.ofNullable(User.builder()
                .id(updatedUser.get().getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password(updatedUser.get().getPassword())
                .jmbg(updatedUser.get().getJmbg())
                .phone(user.getPhone())
                .jobPosition(user.getJobPosition())
                .active(user.isActive())
                .permissions(permissions)
                //                                .dailyLimit(user.getDailyLimit())
                .dailyLimit(
                        user.getDailyLimit() == null
                                ? null
                                : user.getDailyLimit() < updatedUser.get().getDailyLimit()
                                        ? user.getDailyLimit()
                                        : updatedUser.get().getDailyLimit())
                .defaultDailyLimit(user.getDailyLimit())
                .build());
        return ResponseEntity.ok().body(userService.save(updatedUser.get()));
    }

    @GetMapping(value = "/limit")
    public ResponseEntity<?> getUserDailyLimit() {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da pristupite korisnicima.");
        }
        return ResponseEntity.ok().body(userService.getUsersDailyLimit(signedInUserEmail));
    }

    @PatchMapping(value = "/reset-limit/{id}")
    public ResponseEntity<?> resetDailyLimit(@PathVariable(name = "id") Long id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da resetujete limit korisnika.");
        }
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            userOptional.get().setDailyLimit(userOptional.get().getDefaultDailyLimit());
            return ResponseEntity.ok().body(userService.save(userOptional.get()));
        } else {
            return ResponseEntity.status(400).body("Korisnik ne postoji");
        }
    }

    @PatchMapping(value = "change-limit/{id}/{limit}")
    public ResponseEntity<?> changeUserDefaultDailyLimit(
            @PathVariable(name = "id") Long id, @PathVariable(name = "limit") Double limit) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.UPDATE_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da resetujete limit korisnika.");
        }

        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            userOptional.get().setDefaultDailyLimit(limit);
            userOptional
                    .get()
                    .setDailyLimit(
                            userOptional.get().getDailyLimit()
                                            > userOptional.get().getDefaultDailyLimit()
                                    ? userOptional.get().getDefaultDailyLimit()
                                    : userOptional.get().getDailyLimit());
            return ResponseEntity.ok().body(userService.save(userOptional.get()));
        } else {
            return ResponseEntity.status(400).body("Korisnik ne postoji");
        }
    }
}

package rs.edu.raf.si.bank2.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.users.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.users.requests.CheckPermissionRequest;
import rs.edu.raf.si.bank2.users.services.*;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RestController
@CrossOrigin
@RequestMapping("/api/userService")
public class UserServiceCommunicationController {

    private final UserService userService;
    private final AuthorisationService authorisationService;


    @Autowired
    public UserServiceCommunicationController(UserService userService, AuthorisationService authorisationService) {
        this.userService = userService;
        this.authorisationService = authorisationService;
    }

    @GetMapping("/testMethod")
    public ResponseEntity<?> test(){
        return ResponseEntity.ok().body("this is a test msg");
    }

    @GetMapping("/isAuth/{role}")
    public ResponseEntity<?> checkUserPermission(@PathVariable(name = "role") PermissionName role){

        String signedInUserEmail = getContext().getAuthentication().getName();

        if (!authorisationService.isAuthorised(role, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        return ResponseEntity.ok().body("All good");
    }

}

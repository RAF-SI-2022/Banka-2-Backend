package rs.edu.raf.si.bank2.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.edu.raf.si.bank2.users.services.*;

@RestController
@CrossOrigin
@RequestMapping("/api/userService")
public class UserServiceCommunicationController {

    private final UserService userService;


    @Autowired
    public UserServiceCommunicationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/testMethod")
    public ResponseEntity<?> test(){
        return ResponseEntity.ok().body("this is a test msg");
    }

}

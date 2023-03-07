package com.raf.si.Banka2Backend.controllers;

import com.raf.si.Banka2Backend.services.AuthorisationService;
import com.raf.si.Banka2Backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final AuthorisationService authorisationService;

    @Autowired
    public UserController(UserService userService, AuthorisationService authorisationService) {
        this.userService = userService;
        this.authorisationService = authorisationService;
    }

    //TODO: create endpoints for CRUD operations - create (register), read(getById, getAllUsers), update(changeUserData), delete (deleteUser)

}

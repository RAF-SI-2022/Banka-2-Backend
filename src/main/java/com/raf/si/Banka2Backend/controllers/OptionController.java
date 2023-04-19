package com.raf.si.Banka2Backend.controllers;

import com.raf.si.Banka2Backend.exceptions.OptionNotFoundException;
import com.raf.si.Banka2Backend.exceptions.UserNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.User;
import com.raf.si.Banka2Backend.services.OptionService;
import com.raf.si.Banka2Backend.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RestController
@CrossOrigin
@RequestMapping("/api/options")
public class OptionController {

    private final OptionService optionService;
    private final UserService userService;

    public OptionController(OptionService optionService, UserService userService) {
        this.optionService = optionService;
        this.userService = userService;
    }

    //TODO Izdvojiti vlasnika option-a u novi model (UserOption)

    @GetMapping("/{id}/sell")
    public ResponseEntity<?> sellOption(@PathVariable Long id) {

        try{
            return ResponseEntity.ok().body(optionService.sellOption(id));
        } catch(UserNotFoundException | OptionNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping("/{id}/buy")
    public ResponseEntity<?> buyOption(@PathVariable Long id) {

        String signedInUserEmail = getContext().getAuthentication().getName();
        try{
            Optional<User> userOptional = userService.findByEmail(signedInUserEmail);

            return ResponseEntity.ok().body(optionService.buyOption(id, userOptional.get().getId()));
        } catch(UserNotFoundException | OptionNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}

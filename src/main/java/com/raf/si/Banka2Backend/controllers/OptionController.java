package com.raf.si.Banka2Backend.controllers;

import com.raf.si.Banka2Backend.dto.OptionBuyDto;
import com.raf.si.Banka2Backend.exceptions.AmountTooHighForOptionOpenInterestException;
import com.raf.si.Banka2Backend.services.OptionService;
import com.raf.si.Banka2Backend.utils.OptionDateScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;

import com.raf.si.Banka2Backend.exceptions.OptionNotFoundException;
import com.raf.si.Banka2Backend.exceptions.UserNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.User;
import com.raf.si.Banka2Backend.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RestController
@CrossOrigin
@RequestMapping("/api/options")
public class OptionController {

    private OptionService optionService;
    private final UserService userService;
    OptionDateScraper optionDateScraper;

    @Autowired
    public OptionController(OptionService optionService, UserService userService) {
        this.optionService = optionService;
        this.userService = userService;
        this.optionDateScraper = new OptionDateScraper();
    }

    @GetMapping("/{symbol}/{dateString}")
    public ResponseEntity<?> getStockBySymbol(@PathVariable String symbol, @PathVariable String dateString) throws ParseException {
        return ResponseEntity.ok().body(optionService.findByStockAndDate(symbol, dateString));
    }
    @GetMapping("/{symbol}")
    public ResponseEntity<?> getStockBySymbol(@PathVariable String symbol) throws ParseException {
        return ResponseEntity.ok().body(optionService.findByStock(symbol));
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

    @PostMapping("/buy")
    public ResponseEntity<?> buyOption(@RequestBody OptionBuyDto optionBuyDto) {

        String signedInUserEmail = getContext().getAuthentication().getName();
        try{
            Optional<User> userOptional = userService.findByEmail(signedInUserEmail);

            return ResponseEntity.ok().body(optionService.buyOption(optionBuyDto.getOptionId(), userOptional.get().getId(), optionBuyDto.getAmount(), optionBuyDto.getPremium()));
        } catch(UserNotFoundException | OptionNotFoundException | AmountTooHighForOptionOpenInterestException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping(value = "/dates")
    public ResponseEntity<?> getDates(){
        String signedInUserEmail = getContext().getAuthentication().getName();
        List<LocalDate> dates;

        try {
            Optional<User> userOptional = userService.findByEmail(signedInUserEmail);
            if(userOptional.isPresent()){
                dates = this.optionDateScraper.scrape();
            }
            else{
                return ResponseEntity.status(400).body("Internal error");
            }
        } catch(UserNotFoundException | OptionNotFoundException | AmountTooHighForOptionOpenInterestException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        return ResponseEntity.ok().body(dates);
    }
}
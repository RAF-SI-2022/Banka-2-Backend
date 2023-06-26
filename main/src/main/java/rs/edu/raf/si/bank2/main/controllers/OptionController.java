package rs.edu.raf.si.bank2.main.controllers;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.edu.raf.si.bank2.main.dto.OptionBuyDto;
import rs.edu.raf.si.bank2.main.dto.OptionSellDto;
import rs.edu.raf.si.bank2.main.dto.SellStockUsingOptionDto;
import rs.edu.raf.si.bank2.main.exceptions.OptionNotFoundException;
import rs.edu.raf.si.bank2.main.exceptions.StockNotFoundException;
import rs.edu.raf.si.bank2.main.exceptions.TooLateToBuyOptionException;
import rs.edu.raf.si.bank2.main.exceptions.UserNotFoundException;
import rs.edu.raf.si.bank2.main.models.mariadb.User;
import rs.edu.raf.si.bank2.main.services.OptionService;
import rs.edu.raf.si.bank2.main.services.UserCommunicationService;
import rs.edu.raf.si.bank2.main.services.UserService;
import rs.edu.raf.si.bank2.main.services.interfaces.UserCommunicationInterface;
import rs.edu.raf.si.bank2.main.utils.OptionDateScraper;

@RestController
@CrossOrigin
@RequestMapping("/api/options")
public class OptionController {

    private OptionService optionService;
    private final UserService userService;
    private final OptionDateScraper optionDateScraper;
    private final UserCommunicationInterface userCommunicationInterface;

    @Autowired
    public OptionController(
            OptionService optionService, UserService userService, UserCommunicationService communicationService) {
        this.userCommunicationInterface = communicationService;
        this.optionService = optionService;
        this.userService = userService;
        this.optionDateScraper = new OptionDateScraper();
    }

    @GetMapping("/{symbol}/{dateString}")
    public ResponseEntity<?> getStockBySymbol(@PathVariable String symbol, @PathVariable String dateString)
            throws ParseException {
        return ResponseEntity.ok().body(optionService.findByStockAndDate(symbol, dateString));
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<?> getStockBySymbol(@PathVariable String symbol) throws ParseException {
        return ResponseEntity.ok().body(optionService.findByStock(symbol));
    }

    @PostMapping("/sell")
    public ResponseEntity<?> sellOption(@RequestBody OptionSellDto optionSellDto) {

        try {
            return ResponseEntity.ok()
                    .body(optionService.sellOption(optionSellDto.getUserOptionId(), optionSellDto.getPremium()));
        } catch (UserNotFoundException | OptionNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PostMapping("/buy")
    public ResponseEntity<?> buyOption(@RequestBody OptionBuyDto optionBuyDto) {

        String signedInUserEmail = getContext().getAuthentication().getName();
        try {
            Optional<User> userOptional = userService.findByEmail(signedInUserEmail);

            return ResponseEntity.ok()
                    .body(optionService.buyOption(
                            optionBuyDto.getOptionId(),
                            userOptional.get().getId(),
                            optionBuyDto.getAmount(),
                            optionBuyDto.getPremium()));
        } catch (UserNotFoundException | OptionNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping(value = "/dates")
    public ResponseEntity<?> getDates() {
        String signedInUserEmail = getContext().getAuthentication().getName();
        List<LocalDate> dates;

        try {
            Optional<User> userOptional = userService.findByEmail(signedInUserEmail);
            if (userOptional.isPresent()) {
                dates = this.optionDateScraper.scrape();
            } else {
                return ResponseEntity.status(400).body("Doslo je do neocekivane greske.");
            }
        } catch (UserNotFoundException | OptionNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        return ResponseEntity.ok().body(dates);
    }

    @GetMapping("/buy-stocks/{userOptionId}")
    public ResponseEntity<?> buyStocksByOption(@PathVariable Long userOptionId) {

        String signedInUserEmail = getContext().getAuthentication().getName();

        try {
            Optional<User> userOptional = userService.findByEmail(signedInUserEmail);
            return ResponseEntity.ok()
                    .body(optionService.buyStockUsingOption(
                            userOptionId, userOptional.get().getId()));
        } catch (UserNotFoundException
                | OptionNotFoundException
                | StockNotFoundException
                | TooLateToBuyOptionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping("/user-options")
    public ResponseEntity<?> getUserOptions() {
        String signedInUserEmail = getContext().getAuthentication().getName();
        return ResponseEntity.ok()
                .body(optionService.getUserOptions(
                        userService.findByEmail(signedInUserEmail).get().getId()));
    }

    @GetMapping("/user-options/{stockSymbol}")
    public ResponseEntity<?> getUserOptions(@PathVariable String stockSymbol) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        return ResponseEntity.ok()
                .body(optionService.getUserOptionsByIdAndStockSymbol(
                        userService.findByEmail(signedInUserEmail).get().getId(), stockSymbol));
    }

    @PostMapping("/sell-stocks")
    public ResponseEntity<?> sellStocksByOption(@RequestBody SellStockUsingOptionDto sellStockUsingOptionDto) {

        String signedInUserEmail = getContext().getAuthentication().getName();

        // Problem na koji sam naisao je to sto treba da se kreira UserOption, ali nemam podatak o optionId-ju
        // Moguce resenje je dozvoliti null vrednosti za optionId u UserOption modelu i migracionoj skripti
        // Znaci setovati samo userId - id onoga ko kreira SellStockUsingOption, a da optionId ostane null

        //        try{
        //            Optional<User> userOptional = userService.findByEmail(signedInUserEmail);
        //            return ResponseEntity.ok().body(optionService.sellStockUsingOption();
        //        } catch(UserNotFoundException | OptionNotFoundException | StockNotFoundException |
        // TooLateToBuyOptionException e){
        //            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        //        }
        return null;
    }
}

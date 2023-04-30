package rs.edu.raf.si.bank2.Bank2Backend.controllers;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.Bank2Backend.dto.BalanceDto;
import rs.edu.raf.si.bank2.Bank2Backend.exceptions.*;
import rs.edu.raf.si.bank2.Bank2Backend.services.BalanceService;

@RestController
@CrossOrigin
@RequestMapping("/api/balances")
public class BalanceController {
    private final BalanceService balanceService;

    @Autowired
    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findBalancesByUserId(@PathVariable(name = "id") Long userId) {
        return ResponseEntity.ok(this.balanceService.findAllByUserId(userId));
    }

    @PostMapping(value = "/increase")
    public ResponseEntity<?> increaseBalance(@RequestBody @Valid BalanceDto dto) {
        try {
            return ResponseEntity.ok(
                    this.balanceService.increaseBalance(dto.getUserEmail(), dto.getCurrencyCode(), dto.getAmount()));
        } catch (CurrencyNotFoundException e1) {
            return ResponseEntity.badRequest()
                    .body("Currency with code " + dto.getCurrencyCode() + " could not be found.");
        } catch (UserNotFoundException e2) {
            return ResponseEntity.badRequest().body("User with email " + dto.getUserEmail() + " could not be found.");
        } catch (Exception e3) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred.");
        }
    }

    @PostMapping(value = "/decrease")
    public ResponseEntity<?> decreaseBalance(@RequestBody @Valid BalanceDto dto) {
        try {
            return ResponseEntity.ok(
                    this.balanceService.decreaseBalance(dto.getUserEmail(), dto.getCurrencyCode(), dto.getAmount()));
        } catch (BalanceNotFoundException e1) {
            return ResponseEntity.badRequest()
                    .body("Balance for user with email "
                            + dto.getUserEmail()
                            + " and currency code "
                            + dto.getCurrencyCode()
                            + "could not be found.");
        } catch (NotEnoughMoneyException e2) {
            return ResponseEntity.badRequest().body("You don't have enough money for this operation.");
        } catch (NotEnoughReservedMoneyException e3) {
            return ResponseEntity.badRequest().body("You don't have enough reserved money for this operation.");
        } catch (Exception e4) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred.");
        }
    }
}

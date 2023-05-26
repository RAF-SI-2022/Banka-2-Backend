package rs.edu.raf.si.bank2.users.controllers;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.users.dto.BalanceDto;
import rs.edu.raf.si.bank2.users.exceptions.*;
import rs.edu.raf.si.bank2.users.services.BalanceService;

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
            return ResponseEntity.badRequest().body("Valuta sa kodom " + dto.getCurrencyCode() + " nije pronadjena.");
        } catch (UserNotFoundException e2) {
            return ResponseEntity.badRequest().body("Korisnik sa emial-om " + dto.getUserEmail() + " nije pronadjen.");
        } catch (Exception e3) {
            return ResponseEntity.internalServerError().body("Doslo je do neocekivane greske.");
        }
    }

    @PostMapping(value = "/decrease")
    public ResponseEntity<?> decreaseBalance(@RequestBody @Valid BalanceDto dto) {
        try {
            return ResponseEntity.ok(
                    this.balanceService.decreaseBalance(dto.getUserEmail(), dto.getCurrencyCode(), dto.getAmount()));
        } catch (BalanceNotFoundException e1) {
            return ResponseEntity.badRequest()
                    .body("Balans za korisnika sa email-om "
                            + dto.getUserEmail()
                            + " i valuta sa kodom "
                            + dto.getCurrencyCode()
                            + " nisu pronadjeni.");
        } catch (NotEnoughMoneyException e2) {
            return ResponseEntity.badRequest().body("Nemate dovoljno novca da biste izvrsili ovu operaciju.");
        } catch (NotEnoughReservedMoneyException e3) {
            return ResponseEntity.badRequest()
                    .body("Nemate dovoljno rezervisanog novca da biste izvrsili ovu operaciju.");
        } catch (Exception e4) {
            return ResponseEntity.internalServerError().body("Doslo je do neocekivane greske.");
        }
    }
}

package rs.edu.raf.si.bank2.otc.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.otc.dto.CompanyBankAccountDto;
import rs.edu.raf.si.bank2.otc.models.mongodb.CompanyBankAccount;
import rs.edu.raf.si.bank2.otc.services.CompanyBankAccountService;

@RestController
@CrossOrigin
@RequestMapping("/api/bankaccount")
public class BankAccountController {

    private CompanyBankAccountService bankAccountService;

    @Autowired
    public BankAccountController(CompanyBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getBankAccount(@RequestHeader("Authorization") String token, @PathVariable String id) {
        return ResponseEntity.ok(bankAccountService.getBankAccountById(id));
    }

    @PostMapping(value = "/")
    public ResponseEntity<?> createBankAccount(@RequestHeader("Authorization") String token, @RequestBody CompanyBankAccount bankAccount) {
        return ResponseEntity.ok(bankAccountService.createBankAccount(bankAccount));
    }

    @PostMapping(value = "/edit")
    public ResponseEntity<?> editContactPerson(@RequestHeader("Authorization") String token, @RequestBody CompanyBankAccountDto bankAccountRequest) {
        return ResponseEntity.ok(bankAccountService.updateBankAccount(bankAccountRequest));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteBankAccount(@RequestHeader("Authorization") String token, @PathVariable String id) {
        bankAccountService.deleteBankAccount(id);
        return ResponseEntity.noContent().build();
    }
}

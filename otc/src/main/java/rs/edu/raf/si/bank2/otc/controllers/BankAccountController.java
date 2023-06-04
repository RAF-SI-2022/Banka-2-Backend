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
    public ResponseEntity<?> getBankAccount(@PathVariable String id) {
        return ResponseEntity.ok(bankAccountService.getBankAccountById(id));
    }

    @GetMapping(value = "/company/{companyId}")
    public ResponseEntity<?> getAccountsForCompany(@PathVariable String companyId) {
        return ResponseEntity.ok(bankAccountService.getAccountsForCompany(companyId));
    }

    @PostMapping(value = "/{companyId}")
    public ResponseEntity<?> createBankAccount(@RequestBody CompanyBankAccountDto bankAccount, @PathVariable(name = "companyId") String companyId) {
        return ResponseEntity.ok(bankAccountService.createBankAccount(companyId, bankAccount));
    }

    @PostMapping(value = "/edit")
    public ResponseEntity<?> editContactPerson(@RequestBody CompanyBankAccountDto bankAccountRequest) {
        return ResponseEntity.ok(bankAccountService.updateBankAccount(bankAccountRequest));
    }

    @DeleteMapping(value = "/{id}/{companyId}")
    public ResponseEntity<?> deleteBankAccount(@PathVariable(name = "id") String id, @PathVariable(name = "companyId") String companyId) {
        bankAccountService.deleteBankAccount(id, companyId);
        return ResponseEntity.noContent().build();
    }
}

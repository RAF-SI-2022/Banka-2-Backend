package rs.edu.raf.si.bank2.otc.controllers;


import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.otc.dto.CompanyBankAccountDto;
import rs.edu.raf.si.bank2.otc.services.CompanyBankAccountService;

@RestController
@CrossOrigin
@RequestMapping("/api/bankaccount")
@Timed
public class BankAccountController {

    private CompanyBankAccountService bankAccountService;

    @Autowired
    public BankAccountController(CompanyBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @Timed("controllers.bankAccount.getBankAccount")
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getBankAccount(@PathVariable String id) {
        return ResponseEntity.ok(bankAccountService.getBankAccountById(id));
    }

    @Timed("controllers.bankAccount.getAccountsForCompany")
    @GetMapping(value = "/company/{companyId}")
    public ResponseEntity<?> getAccountsForCompany(@PathVariable String companyId) {
        return ResponseEntity.ok(bankAccountService.getAccountsForCompany(companyId));
    }

    @Timed("controllers.bankAccount.createBankAccount")
    @PostMapping(value = "/{companyId}")
    public ResponseEntity<?> createBankAccount(@RequestBody CompanyBankAccountDto bankAccount, @PathVariable(name = "companyId") String companyId) {
        return ResponseEntity.ok(bankAccountService.createBankAccount(companyId, bankAccount));
    }

    @Timed("controllers.bankAccount.editBankAccount")
    @PostMapping(value = "/edit")
    public ResponseEntity<?> editBankAccount(@RequestBody CompanyBankAccountDto bankAccountRequest) {
        return ResponseEntity.ok(bankAccountService.updateBankAccount(bankAccountRequest));
    }

    @Timed("controllers.bankAccount.deleteBankAccount")
    @DeleteMapping(value = "/{id}/{companyId}")
    public ResponseEntity<?> deleteBankAccount(@PathVariable(name = "id") String id, @PathVariable(name = "companyId") String companyId) {
        bankAccountService.deleteBankAccount(id, companyId);
        return ResponseEntity.noContent().build();
    }
}

package rs.edu.raf.si.bank2.otc.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.otc.dto.CompanyBankAccountDto;
import rs.edu.raf.si.bank2.otc.exceptions.BankAccountNotFoundException;
import rs.edu.raf.si.bank2.otc.exceptions.CompanyNotFoundException;
import rs.edu.raf.si.bank2.otc.models.mongodb.Company;
import rs.edu.raf.si.bank2.otc.models.mongodb.CompanyBankAccount;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.CompanyBankAccountRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.CompanyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyBankAccountService {

    @Autowired
    private CompanyBankAccountRepository bankAccountRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public List<CompanyBankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAll();
    }

    public CompanyBankAccount getBankAccountById(String id) {
        Optional<CompanyBankAccount> bankAccount = bankAccountRepository.findById(id);
        if (bankAccount.isPresent()) {
            return bankAccount.get();
        } else {
            throw new BankAccountNotFoundException("Bank account not found with id: " + id);
        }
    }

    public List<CompanyBankAccount> getAccountsForCompany(String companyId){
        Optional<Company> companyOptional = companyRepository.findById(companyId);
        if(companyOptional.isEmpty()) {
            throw new CompanyNotFoundException("There is no company with this ID.");
        }
        return new ArrayList<>(companyOptional.get().getBankAccounts());
    }

    public CompanyBankAccount createBankAccount(String companyId, CompanyBankAccountDto accountDto) {
        Optional<Company> companyOptional = companyRepository.findById(companyId);
        if(companyOptional.isEmpty()) {
            throw new CompanyNotFoundException("There is no company with this ID.");
        }

        CompanyBankAccount companyBankAccount = new CompanyBankAccount();
            companyBankAccount.setAccountNumber(accountDto.getAccountNumber());
            companyBankAccount.setBankName(accountDto.getBankName());
            companyBankAccount.setCurrency(accountDto.getCurrency());
        bankAccountRepository.save(companyBankAccount);
        companyOptional.get().getBankAccounts().add(companyBankAccount);
        companyRepository.save(companyOptional.get());

        return companyBankAccount;
    }

    public CompanyBankAccount updateBankAccount(CompanyBankAccountDto bankAccountDto) {
        Optional<CompanyBankAccount> existingBankAccount = bankAccountRepository.findById(bankAccountDto.getId());
        if (existingBankAccount.isPresent()) {
            CompanyBankAccount companyBankAccount = existingBankAccount.get();
            companyBankAccount.setAccountNumber(bankAccountDto.getAccountNumber());
            companyBankAccount.setBankName(bankAccountDto.getBankName());
            return bankAccountRepository.save(companyBankAccount);
        } else {
            throw new BankAccountNotFoundException("Bank account not found with id: " + bankAccountDto.getId());
        }
    }

    public void deleteBankAccount(String id, String companyId) {
        Optional<Company> companyOptional = companyRepository.findById(companyId);
        if(companyOptional.isEmpty()) {
            throw new CompanyNotFoundException("There is no company with this ID.");
        }
        Optional<CompanyBankAccount> bankAccount = bankAccountRepository.findById(id);
        if (bankAccount.isPresent()) {
            companyOptional.get().getBankAccounts().remove(bankAccount.get());
            companyRepository.save(companyOptional.get());
            bankAccountRepository.deleteById(id);
        }
        else {
            throw new BankAccountNotFoundException("Bank account not found with id: " + id);
        }
    }
    public CompanyBankAccount save(CompanyBankAccount account) {
        return this.bankAccountRepository.save(account);
    }
}

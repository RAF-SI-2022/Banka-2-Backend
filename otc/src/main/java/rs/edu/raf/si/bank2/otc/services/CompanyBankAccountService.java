package rs.edu.raf.si.bank2.otc.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.otc.dto.CompanyBankAccountDto;
import rs.edu.raf.si.bank2.otc.exceptions.BankAccountNotFoundException;
import rs.edu.raf.si.bank2.otc.models.mongodb.CompanyBankAccount;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.CompanyBankAccountRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyBankAccountService {

    @Autowired
    private CompanyBankAccountRepository bankAccountRepository;

    public List<CompanyBankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAll();
    }

    public CompanyBankAccount getBankAccountById(Long id) {
        Optional<CompanyBankAccount> bankAccount = bankAccountRepository.findById(id);
        if (bankAccount.isPresent()) {
            return bankAccount.get();
        } else {
            throw new BankAccountNotFoundException("Bank account not found with id: " + id);
        }
    }

    public CompanyBankAccount createBankAccount(CompanyBankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

    public CompanyBankAccount updateBankAccount(CompanyBankAccountDto bankAccountDto) {
        Optional<CompanyBankAccount> existingBankAccount = bankAccountRepository.findById(bankAccountDto.getId());
        if (existingBankAccount.isPresent()) {
            CompanyBankAccount companyBankAccount = existingBankAccount.get();
            companyBankAccount.setAccountNumber(bankAccountDto.getAccountNumber());
            companyBankAccount.setBankName(bankAccountDto.getBankName());
            companyBankAccount.setAccountType(bankAccountDto.getAccountType());
            return bankAccountRepository.save(companyBankAccount);
        } else {
            throw new BankAccountNotFoundException("Bank account not found with id: " + bankAccountDto.getId());
        }
    }

    public void deleteBankAccount(Long id) {
        Optional<CompanyBankAccount> bankAccount = bankAccountRepository.findById(id);
        if (bankAccount.isPresent()) {
            bankAccountRepository.deleteById(id);
        } else {
            throw new BankAccountNotFoundException("Bank account not found with id: " + id);
        }
    }
}

package rs.edu.raf.si.bank2.client.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.si.bank2.client.dto.CompanyBankAccountDto;
import rs.edu.raf.si.bank2.client.exceptions.BankAccountNotFoundException;
import rs.edu.raf.si.bank2.client.exceptions.CompanyNotFoundException;
import rs.edu.raf.si.bank2.client.models.mongodb.Company;
import rs.edu.raf.si.bank2.client.models.mongodb.CompanyBankAccount;
import rs.edu.raf.si.bank2.client.repositories.mongodb.CompanyBankAccountRepository;
import rs.edu.raf.si.bank2.client.repositories.mongodb.CompanyRepository;
import rs.edu.raf.si.bank2.client.services.CompanyBankAccountService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyBankAccountServiceTest {

    @Mock
    private CompanyBankAccountRepository bankAccountRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyBankAccountService bankAccountService;

    @Test
    public void testGetAllBankAccounts() {
        List<CompanyBankAccount> mockBankAccounts = new ArrayList<>();
        when(bankAccountRepository.findAll()).thenReturn(mockBankAccounts);

        List<CompanyBankAccount> result = bankAccountService.getAllBankAccounts();

        assertEquals(mockBankAccounts, result);
        verify(bankAccountRepository, times(1)).findAll();
    }

    @Test
    public void testGetBankAccountById_ValidId() {
        String accountId = "1";
        CompanyBankAccount mockBankAccount = new CompanyBankAccount();
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(mockBankAccount));

        CompanyBankAccount result = bankAccountService.getBankAccountById(accountId);

        assertEquals(mockBankAccount, result);
        verify(bankAccountRepository, times(1)).findById(accountId);
    }

    @Test
    public void testGetBankAccountById_InvalidId() {
        String accountId = "1";
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.getBankAccountById(accountId));
        verify(bankAccountRepository, times(1)).findById(accountId);
    }

    @Test
    public void testGetAccountsForCompany_ValidCompanyId() {
        String companyId = "1";
        Company mockCompany = new Company();
        mockCompany.setBankAccounts(new ArrayList<>());
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(mockCompany));

        List<CompanyBankAccount> result = bankAccountService.getAccountsForCompany(companyId);

        assertEquals(mockCompany.getBankAccounts(), result);
        verify(companyRepository, times(1)).findById(companyId);
    }

    @Test
    public void testGetAccountsForCompany_InvalidCompanyId() {
        String companyId = "1";
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () -> bankAccountService.getAccountsForCompany(companyId));
        verify(companyRepository, times(1)).findById(companyId);
    }

    @Test
    public void testCreateBankAccount_ValidData() {
        String companyId = "1";
        CompanyBankAccountDto accountDto = new CompanyBankAccountDto();
        accountDto.setAccountNumber("123456789");
        accountDto.setBankName("Bank Name");
        accountDto.setCurrency("USD");

        Company mockCompany = Company.builder().bankAccounts(new ArrayList<>()).build();
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(mockCompany));
        when(bankAccountRepository.save(any(CompanyBankAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyBankAccount result = bankAccountService.createBankAccount(companyId, accountDto);

        assertEquals(accountDto.getAccountNumber(), result.getAccountNumber());
        assertEquals(accountDto.getBankName(), result.getBankName());
        assertEquals(accountDto.getCurrency(), result.getCurrency());
        assertTrue(mockCompany.getBankAccounts().contains(result));
        verify(companyRepository, times(1)).findById(companyId);
        verify(bankAccountRepository, times(1)).save(any(CompanyBankAccount.class));
        verify(companyRepository, times(1)).save(mockCompany);
    }

    @Test
    public void testCreateBankAccount_InvalidCompanyId() {
        String companyId = "1";
        CompanyBankAccountDto accountDto = new CompanyBankAccountDto();
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () -> bankAccountService.createBankAccount(companyId, accountDto));
        verify(companyRepository, times(1)).findById(companyId);
        verify(bankAccountRepository, never()).save(any(CompanyBankAccount.class));
    }

    @Test
    public void testUpdateBankAccount_ValidData() {
        String bankAccountId = "1";
        CompanyBankAccountDto bankAccountDto = new CompanyBankAccountDto();
        bankAccountDto.setId(bankAccountId);
        bankAccountDto.setAccountNumber("123456789");
        bankAccountDto.setBankName("Updated Bank Name");

        CompanyBankAccount mockBankAccount = new CompanyBankAccount();
        when(bankAccountRepository.findById(bankAccountId)).thenReturn(Optional.of(mockBankAccount));
        when(bankAccountRepository.save(any(CompanyBankAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyBankAccount result = bankAccountService.updateBankAccount(bankAccountDto);

        assertEquals(bankAccountDto.getAccountNumber(), result.getAccountNumber());
        assertEquals(bankAccountDto.getBankName(), result.getBankName());
        verify(bankAccountRepository, times(1)).findById(bankAccountId);
        verify(bankAccountRepository, times(1)).save(mockBankAccount);
    }

    @Test
    public void testUpdateBankAccount_InvalidBankAccountId() {
        String bankAccountId = "1";
        CompanyBankAccountDto bankAccountDto = new CompanyBankAccountDto();
        bankAccountDto.setId(bankAccountId);
        when(bankAccountRepository.findById(bankAccountId)).thenReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.updateBankAccount(bankAccountDto));
        verify(bankAccountRepository, times(1)).findById(bankAccountId);
        verify(bankAccountRepository, never()).save(any(CompanyBankAccount.class));
    }

    @Test
    public void testDeleteBankAccount_ValidData() {
        String bankAccountId = "1";
        String companyId = "1";
        Company mockCompany =Company.builder().bankAccounts(new ArrayList<>()).build();
        CompanyBankAccount mockBankAccount = new CompanyBankAccount();
        mockCompany.getBankAccounts().add(mockBankAccount);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(mockCompany));
        when(bankAccountRepository.findById(bankAccountId)).thenReturn(Optional.of(mockBankAccount));

        bankAccountService.deleteBankAccount(bankAccountId, companyId);

        assertFalse(mockCompany.getBankAccounts().contains(mockBankAccount));
        verify(companyRepository, times(1)).findById(companyId);
        verify(bankAccountRepository, times(1)).findById(bankAccountId);
        verify(companyRepository, times(1)).save(mockCompany);
        verify(bankAccountRepository, times(1)).deleteById(bankAccountId);
    }

    @Test
    public void testDeleteBankAccount_InvalidCompanyId() {
        String bankAccountId = "1";
        String companyId = "1";
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () -> bankAccountService.deleteBankAccount(bankAccountId, companyId));
        verify(companyRepository, times(1)).findById(companyId);
        verify(bankAccountRepository, never()).findById(bankAccountId);
        verify(bankAccountRepository, never()).deleteById(bankAccountId);
    }

    @Test
    public void testDeleteBankAccount_InvalidBankAccountId() {
        String bankAccountId = "1";
        String companyId = "1";
        Company mockCompany = new Company();
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(mockCompany));
        when(bankAccountRepository.findById(bankAccountId)).thenReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.deleteBankAccount(bankAccountId, companyId));
        verify(companyRepository, times(1)).findById(companyId);
        verify(bankAccountRepository, times(1)).findById(bankAccountId);
        verify(companyRepository, never()).save(any(Company.class));
        verify(bankAccountRepository, never()).deleteById(bankAccountId);
    }
}

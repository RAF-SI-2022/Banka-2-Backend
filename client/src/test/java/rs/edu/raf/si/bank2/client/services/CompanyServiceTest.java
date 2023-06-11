package rs.edu.raf.si.bank2.client.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.si.bank2.client.dto.ContactsBankAccountsDto;
import rs.edu.raf.si.bank2.client.dto.EditCompanyDto;
import rs.edu.raf.si.bank2.client.exceptions.CompanyIdNotProvidedException;
import rs.edu.raf.si.bank2.client.exceptions.CompanyIdProvidedException;
import rs.edu.raf.si.bank2.client.exceptions.CompanyNotFoundException;
import rs.edu.raf.si.bank2.client.models.mongodb.Company;
import rs.edu.raf.si.bank2.client.repositories.mongodb.CompanyRepository;
import rs.edu.raf.si.bank2.client.services.CompanyService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    @Test
    public void createCompany_ValidCompany_ReturnsSavedCompany() {
        Company company = new Company();
        company.setName("Test Company");
        company.setAddress("Test Address");
        company.setRegistrationNumber("123456");
        company.setTaxNumber("78901234");

        when(companyRepository.save(company)).thenReturn(company);

        Company createdCompany = companyService.createCompany(company);

        assertNotNull(createdCompany);
        assertEquals("Test Company", createdCompany.getName());
        assertEquals("Test Address", createdCompany.getAddress());
        assertEquals("123456", createdCompany.getRegistrationNumber());
        assertEquals("78901234", createdCompany.getTaxNumber());

        verify(companyRepository, times(1)).save(company);
    }

    @Test
    public void createCompany_CompanyIdIsNotValid() {
        Company company = new Company();
        company.setId("1");
        company.setName("Test Company");
        company.setAddress("Test Address");
        company.setRegistrationNumber("123456");
        company.setTaxNumber("78901234");

        assertThrows(CompanyIdProvidedException.class, () -> companyService.createCompany(company));
    }

    @Test
    public void addContactsAndBankAccounts_ValidDto_ReturnsUpdatedCompany() {
        Company existingCompany = new Company();
        existingCompany.setId("1");

        ContactsBankAccountsDto dto = new ContactsBankAccountsDto();
        dto.setId("1");
        dto.setContactPeople(new ArrayList<>());
        dto.setCompanyBankAccounts(new ArrayList<>());

        when(companyRepository.findById("1")).thenReturn(Optional.of(existingCompany));
        when(companyRepository.save(existingCompany)).thenReturn(existingCompany);

        Company updatedCompany = companyService.addContactsAndBankAccounts(dto);

        assertNotNull(updatedCompany);
        assertEquals("1", updatedCompany.getId());

        verify(companyRepository, times(1)).findById("1");
        verify(companyRepository, times(1)).save(existingCompany);
    }

    @Test
    public void addContactsAndBankAccounts_CompanyNotFoundExceptionThrown() {

        ContactsBankAccountsDto dto = new ContactsBankAccountsDto();
        dto.setId("1");
        dto.setContactPeople(new ArrayList<>());
        dto.setCompanyBankAccounts(new ArrayList<>());

        when(companyRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () -> companyService.addContactsAndBankAccounts(dto));
    }

    @Test
    public void updateCompany_ValidDto_ReturnsUpdatedCompany() {
        Company existingCompany = new Company();
        existingCompany.setId("1");

        EditCompanyDto dto = new EditCompanyDto();
        dto.setId("1");
        dto.setName("Updated Company");
        dto.setAddress("Updated Address");
        dto.setRegistrationNumber("987654");
        dto.setTaxNumber("43210987");

        when(companyRepository.findById("1")).thenReturn(Optional.of(existingCompany));
        when(companyRepository.save(existingCompany)).thenReturn(existingCompany);

        Company updatedCompany = companyService.updateCompany(dto);

        assertNotNull(updatedCompany);
        assertEquals("1", updatedCompany.getId());
        assertEquals("Updated Company", updatedCompany.getName());
        assertEquals("Updated Address", updatedCompany.getAddress());
        assertEquals("987654", updatedCompany.getRegistrationNumber());
        assertEquals("43210987", updatedCompany.getTaxNumber());

        verify(companyRepository, times(1)).findById("1");
        verify(companyRepository, times(1)).save(existingCompany);
    }

    @Test
    public void updateCompany_CompanyIdNotProvidedExceptionThrown() {

        EditCompanyDto editCompanyDto = new EditCompanyDto();

        assertThrows(CompanyIdNotProvidedException.class, () -> companyService.updateCompany(editCompanyDto));
    }

    @Test
    public void updateCompany_CompanyNotFoundExceptionThrown() {

        EditCompanyDto editCompanyDto = EditCompanyDto.builder().id("1").build();

        when(companyRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () -> companyService.updateCompany(editCompanyDto));
    }

    @Test
    public void getCompanyById_ExistingId_ReturnsCompanyOptional() {
        Company existingCompany = new Company();
        existingCompany.setId("1");

        when(companyRepository.findById("1")).thenReturn(Optional.of(existingCompany));

        Optional<Company> companyOptional = companyService.getCompanyById("1");

        assertTrue(companyOptional.isPresent());
        assertEquals("1", companyOptional.get().getId());

        verify(companyRepository, times(1)).findById("1");
    }

    @Test
    public void getCompanyById_NonExistingId_ReturnsEmptyOptional() {
        when(companyRepository.findById("1")).thenReturn(Optional.empty());

        Optional<Company> companyOptional = companyService.getCompanyById("1");

        assertTrue(companyOptional.isEmpty());

        verify(companyRepository, times(1)).findById("1");
    }

    @Test
    public void getCompanies_ReturnsListOfCompanies() {
        List<Company> companyList = new ArrayList<>();
        companyList.add(new Company());
        companyList.add(new Company());

        when(companyRepository.findAll()).thenReturn(companyList);

        List<Company> retrievedCompanies = companyService.getCompanies();

        assertEquals(2, retrievedCompanies.size());

        verify(companyRepository, times(1)).findAll();
    }

    @Test
    public void getCompanyByName_ExistingCompany_ReturnsCompany() {
        String companyName = "ABC Company";
        Company company = new Company();
        company.setName(companyName);

        when(companyRepository.findCompanyByName(companyName)).thenReturn(Optional.of(company));

        Company result = companyService.getCompanyByName(companyName);

        assertNotNull(result);
        assertEquals(companyName, result.getName());

        verify(companyRepository, times(1)).findCompanyByName(companyName);
    }

    @Test
    public void getCompanyByName_NonexistentCompany_ThrowsException() {
        String companyName = "Nonexistent Company";

        when(companyRepository.findCompanyByName(companyName)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () -> {
            companyService.getCompanyByName(companyName);
        });

        verify(companyRepository, times(1)).findCompanyByName(companyName);
    }

    @Test
    public void getCompanyByRegistrationNumber_ExistingCompany_ReturnsCompany() {
        String registrationNumber = "123456789";
        Company company = new Company();
        company.setRegistrationNumber(registrationNumber);

        when(companyRepository.findCompanyByRegistrationNumber(registrationNumber)).thenReturn(Optional.of(company));

        Company result = companyService.getCompanyByRegistrationNumber(registrationNumber);

        assertNotNull(result);
        assertEquals(registrationNumber, result.getRegistrationNumber());

        verify(companyRepository, times(1)).findCompanyByRegistrationNumber(registrationNumber);
    }

    @Test
    public void getCompanyByRegistrationNumber_NonexistentCompany_ThrowsException() {
        String registrationNumber = "987654321";

        when(companyRepository.findCompanyByRegistrationNumber(registrationNumber)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () -> {
            companyService.getCompanyByRegistrationNumber(registrationNumber);
        });

        verify(companyRepository, times(1)).findCompanyByRegistrationNumber(registrationNumber);
    }

    @Test
    public void getCompanyByTaxNumber_ExistingCompany_ReturnsCompany() {
        String taxNumber = "987654321";
        Company company = new Company();
        company.setTaxNumber(taxNumber);

        when(companyRepository.findCompanyByTaxNumber(taxNumber)).thenReturn(Optional.of(company));

        Company result = companyService.getCompanyByTaxNumber(taxNumber);

        assertNotNull(result);
        assertEquals(taxNumber, result.getTaxNumber());

        verify(companyRepository, times(1)).findCompanyByTaxNumber(taxNumber);
    }

    @Test
    public void getCompanyByTaxNumber_NonexistentCompany_ThrowsException() {
        String taxNumber = "123456789";

        when(companyRepository.findCompanyByTaxNumber(taxNumber)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () -> {
            companyService.getCompanyByTaxNumber(taxNumber);
        });

        verify(companyRepository, times(1)).findCompanyByTaxNumber(taxNumber);
    }
}

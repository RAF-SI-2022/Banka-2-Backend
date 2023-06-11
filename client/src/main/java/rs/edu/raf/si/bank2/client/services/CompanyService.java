package rs.edu.raf.si.bank2.client.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.client.dto.ContactsBankAccountsDto;
import rs.edu.raf.si.bank2.client.models.mongodb.Company;
import rs.edu.raf.si.bank2.client.models.mongodb.CompanyBankAccount;
import rs.edu.raf.si.bank2.client.models.mongodb.ContactPerson;
import rs.edu.raf.si.bank2.client.dto.EditCompanyDto;
import rs.edu.raf.si.bank2.client.exceptions.CompanyIdNotProvidedException;
import rs.edu.raf.si.bank2.client.exceptions.CompanyIdProvidedException;
import rs.edu.raf.si.bank2.client.exceptions.CompanyNotFoundException;
import rs.edu.raf.si.bank2.client.repositories.mongodb.CompanyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company createCompany(Company company) {
        if(company.getId() != null) {
            throw new CompanyIdProvidedException("ID can't be manually set.");
        }

//        Company companyDbEntry = Company.builder().name(company.getName()).address(company.getAddress()).
//                registrationNumber(company.getRegistrationNumber()).taxNumber(company.getTaxNumber()).
//                activityCode(company.getActivityCode()).contactPersons(company.getContactPersons()).bankAccounts(company.getBankAccounts()).build();

        return companyRepository.save(company);
    }

    public Company addContactsAndBankAccounts(ContactsBankAccountsDto contactsBankAccountsDto){
        Optional<Company> companyOptional = companyRepository.findById(contactsBankAccountsDto.getId());
        if(companyOptional.isEmpty()) {
            throw new CompanyNotFoundException("There is no company with this ID.");
        }
        Company company = companyOptional.get();
        ArrayList<ContactPerson> contactPeople = new ArrayList<>(contactsBankAccountsDto.getContactPeople());
        ArrayList<CompanyBankAccount> bankAccounts = new ArrayList<>(contactsBankAccountsDto.getCompanyBankAccounts());
        company.setContactPersons(contactPeople);
        company.setBankAccounts(bankAccounts);
        companyRepository.save(company);
        return company;
    }

    public Company updateCompany(EditCompanyDto editCompanyDto) {
        if(editCompanyDto.getId() == null) {
            throw new CompanyIdNotProvidedException("Id must be present for you to edit the company.");
        }

        Optional<Company> companyOptional = companyRepository.findById(editCompanyDto.getId());
        if(companyOptional.isEmpty()) {
            throw new CompanyNotFoundException("There is no company with this ID.");
        }

        Company company = companyOptional.get();
        company.setName(editCompanyDto.getName());
        company.setAddress(editCompanyDto.getAddress());
        company.setRegistrationNumber(editCompanyDto.getRegistrationNumber());
        company.setTaxNumber(editCompanyDto.getTaxNumber());
        company.setActivityCode(editCompanyDto.getActivityCode());
        company.setContactPersons(editCompanyDto.getContactPersons());
        company.setBankAccounts(editCompanyDto.getBankAccounts());
        return companyRepository.save(company);
    }

    public Optional<Company> getCompanyById(String id) {
        return companyRepository.findById(id);
    }

    public List<Company> getCompanies() {
        return companyRepository.findAll();
    }

    public Company getCompanyByName(String name) {
        Optional<Company> companyRet = companyRepository.findCompanyByName(name);
        if(companyRet.isEmpty()){
            throw new CompanyNotFoundException("There is no company with this name.");
        }
        return companyRet.get();

    }

    public Company getCompanyByRegistrationNumber(String registrationNumber) {
        Optional<Company> companyRet = companyRepository.findCompanyByRegistrationNumber(registrationNumber);
        if(companyRet.isEmpty()){
            throw new CompanyNotFoundException("There is no company with this registration number.");
        }
        return companyRet.get();
    }

    public Company getCompanyByTaxNumber(String taxNo) {
        Optional<Company> companyRet = companyRepository.findCompanyByTaxNumber(taxNo);
        if(companyRet.isEmpty()){
            throw new CompanyNotFoundException("There is no company with this tax number.");
        }
        return companyRet.get();
    }
}

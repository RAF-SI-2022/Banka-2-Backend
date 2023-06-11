package rs.edu.raf.si.bank2.client.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.client.dto.ContactsBankAccountsDto;
import rs.edu.raf.si.bank2.client.dto.CreateCompanyDto;
import rs.edu.raf.si.bank2.client.models.mongodb.Company;
import rs.edu.raf.si.bank2.client.services.CompanyService;
import rs.edu.raf.si.bank2.client.utils.JwtUtil;
import rs.edu.raf.si.bank2.client.dto.EditCompanyDto;
//import rs.edu.raf.si.bank2.client.listener.MessageHelper;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/company")
public class CompanyController {

//    private JmsTemplate jmsTemplate;
    private CompanyService companyService;

//    private MessageHelper messageHelper;
    private final JwtUtil jwtUtil;

//    private String createCompanyDestination;

    @Autowired
    public CompanyController(CompanyService companyService,/*MessageHelper messageHelper /*JmsTemplate jmsTemplate*/JwtUtil jwtUtil/*,@Value("${destination.createCompany}") String createCompanyDestination*/ ) {
        this.companyService = companyService;
//        this.messageHelper = messageHelper;
//        this.jmsTemplate = jmsTemplate;
        this.jwtUtil = jwtUtil;
//        this.createCompanyDestination = createCompanyDestination;
    }

    @GetMapping(value = "")
    public ResponseEntity<?> getCompanies(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(companyService.getCompanies());
    }

    @GetMapping(value = "/accounts/{companyId}")
    public ResponseEntity<?> getBankAccountsForCompany(@PathVariable(name = "companyId") String companyId) {
        Optional<Company> company = companyService.getCompanyById(companyId);
        if (company.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(company.get().getBankAccounts());
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getCompanyById(@PathVariable String id) {
        Optional<Company> company = companyService.getCompanyById(id);
        if (company.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(company.get());
    }

    @GetMapping(value = "/name/{name}")
    public ResponseEntity<?> getCompanyByNaziv(@PathVariable String name) {
        return ResponseEntity.ok(companyService.getCompanyByName(name));
    }

    @GetMapping(value = "/registrationNumber/{registrationNumber}")
    public ResponseEntity<?> getCompanyByMaticniBroj(@PathVariable String registrationNumber) {
        return ResponseEntity.ok(companyService.getCompanyByRegistrationNumber(registrationNumber));
    }

    @GetMapping(value = "/taxNumber/{taxNumber}")
    public ResponseEntity<?> getCompanyByPib(@PathVariable String taxNumber) {
        return ResponseEntity.ok(companyService.getCompanyByTaxNumber(taxNumber));
    }

    @PostMapping(value = "/create")
    public ResponseEntity<?> createCompany(@RequestBody CreateCompanyDto companyDto) {

        System.out.println("TU SMO PLZIC");
//        System.out.println(messageHelper.createTextMessage(companyDto));
        System.out.println(companyDto);
        companyService.createCompany(new Company(companyDto.getName(),companyDto.getAddress(),companyDto.getTaxNumber(),companyDto.getActivityCode(),companyDto.getRegistrationNumber()));
//        jmsTemplate.convertAndSend(createCompanyDestination,messageHelper.createTextMessage(companyDto));
//        List<ContactPerson> employeeList = new ArrayList<>(company.getContactPersons());
//        for(ContactPerson cp: employeeList){
//
//        }
        return ResponseEntity.ok().build();
    }
    @PostMapping(value = "/add")
    public ResponseEntity<?> addContactsAndBankAccounts(@RequestBody ContactsBankAccountsDto contactsBankAccountsDto ){
        return ResponseEntity.ok(companyService.addContactsAndBankAccounts(contactsBankAccountsDto));
    }

    @PostMapping(value = "/edit")
    public ResponseEntity<?> editCompany(@RequestHeader("Authorization") String token, @RequestBody EditCompanyDto editCompanyDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        companyService.updateCompany(editCompanyDto);
        return ResponseEntity.ok().build();
    }

}

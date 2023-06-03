package rs.edu.raf.si.bank2.otc.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
//import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.otc.dto.CreateCompanyDto;
import rs.edu.raf.si.bank2.otc.dto.EditCompanyDto;
//import rs.edu.raf.si.bank2.otc.listener.MessageHelper;
import rs.edu.raf.si.bank2.otc.models.mongodb.Company;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContactPerson;
import rs.edu.raf.si.bank2.otc.services.CompanyService;
import rs.edu.raf.si.bank2.otc.utils.JwtUtil;

import java.util.ArrayList;
import java.util.List;
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

    @GetMapping(value = "/")
    public ResponseEntity<?> getCompanies(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(companyService.getCompanies());
    }

    @GetMapping(value = "/test")
    public void test(){
        System.out.println("ZZZZZZZ");
    }
    @GetMapping(value = "/id/{id}")
    public ResponseEntity<?> getCompanyById(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        Optional<Company> company = companyService.getCompanyById(id);
        if (company.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(company.get());
    }

    @GetMapping(value = "/name/{name}")
    public ResponseEntity<?> getCompanyByNaziv(@RequestHeader("Authorization") String token, @PathVariable String name) {
        return ResponseEntity.ok(companyService.getCompanyByName(name));
    }

    @GetMapping(value = "/registrationNumber/{registrationNumber}")
    public ResponseEntity<?> getCompanyByMaticniBroj(@RequestHeader("Authorization") String token, @PathVariable String registrationNumber) {
        return ResponseEntity.ok(companyService.getCompanyByRegistrationNumber(registrationNumber));
    }

    @GetMapping(value = "/taxNumber/{taxNumber}")
    public ResponseEntity<?> getCompanyByPib(@RequestHeader("Authorization") String token, @PathVariable String taxNumber) {
        return ResponseEntity.ok(companyService.getCompanyByTaxNumber(taxNumber));
    }

    @PostMapping(value = "/create")
    public ResponseEntity<?> createCompanija(@RequestBody CreateCompanyDto companyDto) {

        System.out.println("TU SMO PLZIC");
//        System.out.println(messageHelper.createTextMessage(companyDto));
        System.out.println(companyDto);
//        jmsTemplate.convertAndSend(createCompanyDestination,messageHelper.createTextMessage(companyDto));
//        List<ContactPerson> employeeList = new ArrayList<>(company.getContactPersons());
//        for(ContactPerson cp: employeeList){
//
//        }
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }

    @PostMapping(value = "/edit")
    public ResponseEntity<?> editCompany(@RequestHeader("Authorization") String token, @RequestBody EditCompanyDto editCompanyDto) {
        return ResponseEntity.ok(companyService.updateCompany(editCompanyDto));
    }

}

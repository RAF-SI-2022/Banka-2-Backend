package rs.edu.raf.si.bank2.client.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.client.dto.ContactPersonDto;
import rs.edu.raf.si.bank2.client.models.mongodb.ContactPerson;
import rs.edu.raf.si.bank2.client.services.CompanyService;
import rs.edu.raf.si.bank2.client.services.ContactPersonService;


@RestController
@CrossOrigin
@RequestMapping("/api/contact")
public class ContactPersonController {

    private CompanyService companyService;
    private ContactPersonService contactPersonService;

    @Autowired
    public ContactPersonController(CompanyService companyService, ContactPersonService contactPersonService) {
        this.companyService = companyService;
        this.contactPersonService = contactPersonService;
    }

    @GetMapping(value = "")
    public ResponseEntity<?> getContactPersons() {
        return ResponseEntity.ok(contactPersonService.getAllContactPersons());
    }

    @GetMapping(value = "/id/{id}")
    public ResponseEntity<?> getContactPerson(@PathVariable String id) {
        return ResponseEntity.ok(contactPersonService.getContactPersonById(id));
    }

    @PostMapping(value = "")
    public ResponseEntity<?> createContactPerson(@RequestBody ContactPerson contactPerson) {
        return ResponseEntity.ok(contactPersonService.addContactPerson(contactPerson));
    }

    @PostMapping(value = "/edit")
    public ResponseEntity<?> editContactPerson(@RequestBody ContactPersonDto contactPersonDto) {
        return ResponseEntity.ok(contactPersonService.updateContactPerson(contactPersonDto));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteContactPerson(@PathVariable String id) {
        contactPersonService.deleteContactPerson(id);
        return ResponseEntity.noContent().build();
    }
}

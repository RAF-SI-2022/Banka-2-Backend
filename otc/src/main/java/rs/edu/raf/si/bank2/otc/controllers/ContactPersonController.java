package rs.edu.raf.si.bank2.otc.controllers;


import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.otc.dto.ContactPersonDto;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContactPerson;
import rs.edu.raf.si.bank2.otc.services.CompanyService;
import rs.edu.raf.si.bank2.otc.services.ContactPersonService;


@RestController
@CrossOrigin
@RequestMapping("/api/contact")
@Timed
public class ContactPersonController {

    private CompanyService companyService;
    private ContactPersonService contactPersonService;

    @Autowired
    public ContactPersonController(CompanyService companyService, ContactPersonService contactPersonService) {
        this.companyService = companyService;
        this.contactPersonService = contactPersonService;
    }

    @Timed("controllers.contactPerson.getContactPersons")
    @GetMapping(value = "")
    public ResponseEntity<?> getContactPersons() {
        return ResponseEntity.ok(contactPersonService.getAllContactPersons());
    }

    @Timed("controllers.contactPerson.getContactPerson")
    @GetMapping(value = "/id/{id}")
    public ResponseEntity<?> getContactPerson(@PathVariable String id) {
        return ResponseEntity.ok(contactPersonService.getContactPersonById(id));
    }

    @Timed("controllers.contactPerson.createContactPerson")
    @PostMapping(value = "")
    public ResponseEntity<?> createContactPerson(@RequestBody ContactPerson contactPerson) {
        return ResponseEntity.ok(contactPersonService.addContactPerson(contactPerson));
    }

    @Timed("controllers.contactPerson.editContactPerson")
    @PostMapping(value = "/edit")
    public ResponseEntity<?> editContactPerson(@RequestBody ContactPersonDto contactPersonDto) {
        return ResponseEntity.ok(contactPersonService.updateContactPerson(contactPersonDto));
    }

    @Timed("controllers.contactPerson.deleteContactPerson")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteContactPerson(@PathVariable String id) {
        contactPersonService.deleteContactPerson(id);
        return ResponseEntity.noContent().build();
    }
}

package rs.edu.raf.si.bank2.client.services;

import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.client.dto.ContactPersonDto;
import rs.edu.raf.si.bank2.client.models.mongodb.ContactPerson;
import rs.edu.raf.si.bank2.client.exceptions.ContactPersonNotFoundException;
import rs.edu.raf.si.bank2.client.repositories.mongodb.ContactPersonRepository;

import java.util.List;

@Service
public class ContactPersonService {

    private final ContactPersonRepository contactPersonRepository;

    public ContactPersonService(ContactPersonRepository contactPersonRepository) {
        this.contactPersonRepository = contactPersonRepository;
    }

    public List<ContactPerson> getAllContactPersons() {
        return contactPersonRepository.findAll();
    }

    public ContactPerson getContactPersonById(String id) {
        return contactPersonRepository.findById(id).orElseThrow(() -> new ContactPersonNotFoundException("ContactPerson not found."));
    }

    public ContactPerson addContactPerson(ContactPerson contactPerson) {
        return contactPersonRepository.save(contactPerson);
    }

    public ContactPerson updateContactPerson(ContactPersonDto updatedContactPerson) {
        ContactPerson contactPerson = getContactPersonById(updatedContactPerson.getId());

        contactPerson.setFirstName(updatedContactPerson.getFirstName());
        contactPerson.setLastName(updatedContactPerson.getLastName());
        contactPerson.setPhoneNumber(updatedContactPerson.getPhoneNumber());
        contactPerson.setEmail(updatedContactPerson.getEmail());
        contactPerson.setPosition(updatedContactPerson.getPosition());
        contactPerson.setNote(updatedContactPerson.getNote());

        return contactPersonRepository.save(contactPerson);
    }

    public void deleteContactPerson(String id) {
        ContactPerson contactPerson = getContactPersonById(id);
        contactPersonRepository.delete(contactPerson);
    }
}

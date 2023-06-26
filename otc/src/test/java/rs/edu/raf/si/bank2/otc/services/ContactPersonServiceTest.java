package rs.edu.raf.si.bank2.otc.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.si.bank2.otc.dto.ContactPersonDto;
import rs.edu.raf.si.bank2.otc.exceptions.ContactPersonNotFoundException;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContactPerson;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.ContactPersonRepository;

@ExtendWith(MockitoExtension.class)
public class ContactPersonServiceTest {

    @Mock
    private ContactPersonRepository contactPersonRepository;

    @InjectMocks
    private ContactPersonService contactPersonService;

    @Test
    public void getAllContactPersons_ReturnsListOfContactPersons() {
        List<ContactPerson> contactPersonList = new ArrayList<>();
        contactPersonList.add(new ContactPerson());
        contactPersonList.add(new ContactPerson());

        when(contactPersonRepository.findAll()).thenReturn(contactPersonList);

        List<ContactPerson> retrievedContactPersons = contactPersonService.getAllContactPersons();

        assertEquals(2, retrievedContactPersons.size());

        verify(contactPersonRepository, times(1)).findAll();
    }

    @Test
    public void getContactPersonById_ExistingId_ReturnsContactPerson() {
        ContactPerson existingContactPerson = new ContactPerson();
        existingContactPerson.setId("1");

        when(contactPersonRepository.findById("1")).thenReturn(Optional.of(existingContactPerson));

        ContactPerson retrievedContactPerson = contactPersonService.getContactPersonById("1");

        assertNotNull(retrievedContactPerson);
        assertEquals("1", retrievedContactPerson.getId());

        verify(contactPersonRepository, times(1)).findById("1");
    }

    @Test
    public void getContactPersonById_NonExistingId_ThrowsContactPersonNotFoundException() {
        when(contactPersonRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(ContactPersonNotFoundException.class, () -> {
            contactPersonService.getContactPersonById("1");
        });

        verify(contactPersonRepository, times(1)).findById("1");
    }

    @Test
    public void addContactPerson_ValidContactPerson_ReturnsSavedContactPerson() {
        ContactPerson contactPerson = new ContactPerson();
        contactPerson.setFirstName("John");
        contactPerson.setLastName("Doe");

        when(contactPersonRepository.save(contactPerson)).thenReturn(contactPerson);

        ContactPerson savedContactPerson = contactPersonService.addContactPerson(contactPerson);

        assertNotNull(savedContactPerson);
        assertEquals("John", savedContactPerson.getFirstName());
        assertEquals("Doe", savedContactPerson.getLastName());

        verify(contactPersonRepository, times(1)).save(contactPerson);
    }

    @Test
    public void updateContactPerson_ValidDto_ReturnsUpdatedContactPerson() {
        ContactPerson existingContactPerson = new ContactPerson();
        existingContactPerson.setId("1");

        ContactPersonDto updatedContactPersonDto = new ContactPersonDto();
        updatedContactPersonDto.setId("1");
        updatedContactPersonDto.setFirstName("Updated John");
        updatedContactPersonDto.setLastName("Updated Doe");

        when(contactPersonRepository.findById("1")).thenReturn(Optional.of(existingContactPerson));
        when(contactPersonRepository.save(existingContactPerson)).thenReturn(existingContactPerson);

        ContactPerson updatedContactPerson = contactPersonService.updateContactPerson(updatedContactPersonDto);

        assertNotNull(updatedContactPerson);
        assertEquals("1", updatedContactPerson.getId());
        assertEquals("Updated John", updatedContactPerson.getFirstName());
        assertEquals("Updated Doe", updatedContactPerson.getLastName());

        verify(contactPersonRepository, times(1)).findById("1");
        verify(contactPersonRepository, times(1)).save(existingContactPerson);
    }

    @Test
    public void deleteContactPerson_ExistingId_DeletesContactPerson() {
        ContactPerson existingContactPerson = new ContactPerson();
        existingContactPerson.setId("1");

        when(contactPersonRepository.findById("1")).thenReturn(Optional.of(existingContactPerson));

        contactPersonService.deleteContactPerson("1");

        verify(contactPersonRepository, times(1)).findById("1");
        verify(contactPersonRepository, times(1)).delete(existingContactPerson);
    }

    @Test
    public void deleteContactPerson_NonExistingId_ThrowsContactPersonNotFoundException() {
        when(contactPersonRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(ContactPersonNotFoundException.class, () -> {
            contactPersonService.deleteContactPerson("1");
        });

        verify(contactPersonRepository, times(1)).findById("1");
        verify(contactPersonRepository, never()).delete(any());
    }
}

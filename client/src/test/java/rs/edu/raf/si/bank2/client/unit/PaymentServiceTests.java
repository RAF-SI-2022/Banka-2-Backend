package rs.edu.raf.si.bank2.client.unit;

import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.si.bank2.client.models.mongodb.DevizniRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.PoslovniRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.TekuciRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.Balance;
import rs.edu.raf.si.bank2.client.repositories.mongodb.DevizniRacunRepository;
import rs.edu.raf.si.bank2.client.repositories.mongodb.PoslovniRacunRepository;
import rs.edu.raf.si.bank2.client.repositories.mongodb.TekuciRacunRepository;
import rs.edu.raf.si.bank2.client.services.PaymentService;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTests {

    @Mock
    private TekuciRacunRepository tekuciRacunRepository;

    @Mock
    private DevizniRacunRepository devizniRacunRepository;

    @Mock
    private PoslovniRacunRepository poslovniRacunRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void testSubtractTekuci() {
        // Create a sample TekuciRacun
        TekuciRacun tekuciRacun = new TekuciRacun();
        tekuciRacun.setBalance(100.0);
        tekuciRacun.setAvailableBalance(100.0);

        // Mock the repository method
        when(tekuciRacunRepository.findTekuciRacunByRegistrationNumber("regNum"))
                .thenReturn(Optional.of(tekuciRacun));

        // Perform the method call
        paymentService.subtract(Balance.TEKUCI, "regNum", 50.0);

        // Verify the behavior
        Assertions.assertEquals(50.0, tekuciRacun.getBalance());
        Assertions.assertEquals(50.0, tekuciRacun.getAvailableBalance());
        verify(tekuciRacunRepository, times(1)).save(tekuciRacun);
    }

    // Similar test methods can be written for the remaining cases (DEVIZNI and POSLOVNI)

    @Test
    void testAddTekuci() {
        // Create a sample TekuciRacun
        TekuciRacun tekuciRacun = new TekuciRacun();
        tekuciRacun.setBalance(100.0);
        tekuciRacun.setAvailableBalance(100.0);

        // Mock the repository method
        when(tekuciRacunRepository.findTekuciRacunByRegistrationNumber("regNum"))
                .thenReturn(Optional.of(tekuciRacun));

        // Perform the method call
        paymentService.add(Balance.TEKUCI, "regNum", 50.0);

        // Verify the behavior
        Assertions.assertEquals(150.0, tekuciRacun.getBalance());
        Assertions.assertEquals(150.0, tekuciRacun.getAvailableBalance());
        verify(tekuciRacunRepository, times(1)).save(tekuciRacun);
    }

    @Test
    void testSubtractDevizni() {
        // Create a sample TekuciRacun
        DevizniRacun devizniRacun = new DevizniRacun();
        devizniRacun.setBalance(100.0);
        devizniRacun.setAvailableBalance(100.0);

        // Mock the repository method
        when(devizniRacunRepository.findDevizniRacunByRegistrationNumber("regNum"))
                .thenReturn(Optional.of(devizniRacun));

        // Perform the method call
        paymentService.subtract(Balance.DEVIZNI, "regNum", 50.0);

        // Verify the behavior
        Assertions.assertEquals(50.0, devizniRacun.getBalance());
        Assertions.assertEquals(50.0, devizniRacun.getAvailableBalance());
        verify(devizniRacunRepository, times(1)).save(devizniRacun);
    }

    // Similar test methods can be written for the remaining cases (DEVIZNI and POSLOVNI)

    @Test
    void testAddDevizni() {
        // Create a sample TekuciRacun
        DevizniRacun devizniRacun = new DevizniRacun();
        devizniRacun.setBalance(100.0);
        devizniRacun.setAvailableBalance(100.0);

        // Mock the repository method
        when(devizniRacunRepository.findDevizniRacunByRegistrationNumber("regNum"))
                .thenReturn(Optional.of(devizniRacun));

        // Perform the method call
        paymentService.add(Balance.DEVIZNI, "regNum", 50.0);

        // Verify the behavior
        Assertions.assertEquals(150.0, devizniRacun.getBalance());
        Assertions.assertEquals(150.0, devizniRacun.getAvailableBalance());
        verify(devizniRacunRepository, times(1)).save(devizniRacun);
    }

    @Test
    void testSubtractPoslovni() {
        // Create a sample TekuciRacun
        PoslovniRacun poslovniRacun = new PoslovniRacun();
        poslovniRacun.setBalance(100.0);
        poslovniRacun.setAvailableBalance(100.0);

        // Mock the repository method
        when(poslovniRacunRepository.findPoslovniRacunByRegistrationNumber("regNum"))
                .thenReturn(Optional.of(poslovniRacun));

        // Perform the method call
        paymentService.subtract(Balance.POSLOVNI, "regNum", 50.0);

        // Verify the behavior
        Assertions.assertEquals(50.0, poslovniRacun.getBalance());
        Assertions.assertEquals(50.0, poslovniRacun.getAvailableBalance());
        verify(poslovniRacunRepository, times(1)).save(poslovniRacun);
    }

    // Similar test methods can be written for the remaining cases (DEVIZNI and POSLOVNI)

    @Test
    void testAddPoslovni() {
        // Create a sample TekuciRacun
        PoslovniRacun poslovniRacun = new PoslovniRacun();
        poslovniRacun.setBalance(100.0);
        poslovniRacun.setAvailableBalance(100.0);

        // Mock the repository method
        when(poslovniRacunRepository.findPoslovniRacunByRegistrationNumber("regNum"))
                .thenReturn(Optional.of(poslovniRacun));

        // Perform the method call
        paymentService.add(Balance.POSLOVNI, "regNum", 50.0);

        // Verify the behavior
        Assertions.assertEquals(150.0, poslovniRacun.getBalance());
        Assertions.assertEquals(150.0, poslovniRacun.getAvailableBalance());
        verify(poslovniRacunRepository, times(1)).save(poslovniRacun);
    }
}

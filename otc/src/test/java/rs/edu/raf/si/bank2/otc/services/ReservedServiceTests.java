package rs.edu.raf.si.bank2.otc.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import rs.edu.raf.si.bank2.otc.dto.CommunicationDto;
import rs.edu.raf.si.bank2.otc.dto.ReserveDto;
import rs.edu.raf.si.bank2.otc.dto.TransactionElementDto;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContractElements;
import rs.edu.raf.si.bank2.otc.models.mongodb.TransactionElement;
import rs.edu.raf.si.bank2.otc.models.mongodb.TransactionElements;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.MarginBalanceRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.MarginTransactionRepository;
import rs.edu.raf.si.bank2.otc.utils.JwtUtil;

import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@TestPropertySource(locations = "classpath:application-local.properties")
public class ReservedServiceTests {

    @Mock
    private JwtUtil jwtUtil;


    @MockBean
    private RestTemplate restTemplate;

    @InjectMocks
    private ReservedService reservedService;

    /*
    assert Throws dodje do linije 167 u reservedService i tu se baca exception, to znaci da se sve do tu
    izvrsilo kako treba, a onda se baca exception, sto znaci da je test prosao
     */
    @Test
    public void testSendReservationBuyCashStock() {


        TransactionElementDto teDto = new TransactionElementDto();

        teDto.setContractId("4440");
        teDto.setElementId("");
        teDto.setBuyOrSell(ContractElements.BUY);
        teDto.setTransactionElement(TransactionElements.STOCK);
        teDto.setBalance(ContractElements.CASH);
        teDto.setCurrency("USD");
        teDto.setAmount(100);
        teDto.setPriceOfOneElement(10.0);
        teDto.setUserId(123L);
        teDto.setMariaDbId(456L);
        teDto.setFutureStorageField("");


        assertThrows(RuntimeException.class, () -> reservedService.sendReservation(teDto));

    }

    @Test
    public void testSendReservationBuyCashFuture() {
        TransactionElementDto teDto = new TransactionElementDto();

        teDto.setContractId("4441");
        teDto.setElementId("");
        teDto.setBuyOrSell(ContractElements.BUY);
        teDto.setTransactionElement(TransactionElements.FUTURE);
        teDto.setBalance(ContractElements.CASH);
        teDto.setCurrency("USD");
        teDto.setAmount(100);
        teDto.setPriceOfOneElement(10.0);
        teDto.setUserId(123L);
        teDto.setMariaDbId(456L);
        teDto.setFutureStorageField("");

        assertThrows(RuntimeException.class, () -> reservedService.sendReservation(teDto));
    }

    @Test
    public void testSendReservationBuyCashOption() {
        TransactionElementDto teDto = new TransactionElementDto();

        teDto.setContractId("4442");
        teDto.setElementId("");
        teDto.setBuyOrSell(ContractElements.BUY);
        teDto.setTransactionElement(TransactionElements.OPTION);
        teDto.setBalance(ContractElements.CASH);
        teDto.setCurrency("USD");
        teDto.setAmount(100);
        teDto.setPriceOfOneElement(10.0);
        teDto.setUserId(123L);
        teDto.setMariaDbId(456L);
        teDto.setFutureStorageField("");

        assertThrows(RuntimeException.class, () -> reservedService.sendReservation(teDto));
    }

    @Test
    public void testSendReservationBuyMargin() {
        TransactionElementDto teDto = new TransactionElementDto();

        teDto.setContractId("4443");
        teDto.setElementId("");
        teDto.setBuyOrSell(ContractElements.BUY);
        teDto.setTransactionElement(TransactionElements.STOCK);
        teDto.setBalance(ContractElements.MARGIN);
        teDto.setCurrency("USD");
        teDto.setAmount(100);
        teDto.setPriceOfOneElement(10.0);
        teDto.setUserId(123L);
        teDto.setMariaDbId(456L);
        teDto.setFutureStorageField("");

        assertThrows(RuntimeException.class, () -> reservedService.sendReservation(teDto));
    }


    @Test
    public void testSendReservationSellStock() {
        TransactionElementDto teDto = new TransactionElementDto();

        teDto.setContractId("4443");
        teDto.setElementId("");
        teDto.setBuyOrSell(ContractElements.SELL);
        teDto.setTransactionElement(TransactionElements.STOCK);
        teDto.setCurrency("USD");
        teDto.setAmount(100);
        teDto.setPriceOfOneElement(10.0);
        teDto.setUserId(123L);
        teDto.setMariaDbId(456L);
        teDto.setFutureStorageField("");

        assertThrows(RuntimeException.class, () -> reservedService.sendReservation(teDto));
    }

    @Test
    public void testSendReservationSellOption() {
        TransactionElementDto teDto = new TransactionElementDto();

        teDto.setContractId("4444");
        teDto.setElementId("");
        teDto.setBuyOrSell(ContractElements.SELL);
        teDto.setTransactionElement(TransactionElements.OPTION);
        teDto.setCurrency("USD");
        teDto.setAmount(100);
        teDto.setPriceOfOneElement(10.0);
        teDto.setUserId(123L);
        teDto.setMariaDbId(456L);
        teDto.setFutureStorageField("");

        assertThrows(RuntimeException.class, () -> reservedService.sendReservation(teDto));
    }

    @Test
    public void testSendReservationSellFuture() {
        TransactionElementDto teDto = new TransactionElementDto();

        teDto.setContractId("4445");
        teDto.setElementId("");
        teDto.setBuyOrSell(ContractElements.SELL);
        teDto.setTransactionElement(TransactionElements.FUTURE);
        teDto.setCurrency("USD");
        teDto.setAmount(100);
        teDto.setPriceOfOneElement(10.0);
        teDto.setUserId(123L);
        teDto.setMariaDbId(456L);
        teDto.setFutureStorageField("");

        assertThrows(RuntimeException.class, () -> reservedService.sendReservation(teDto));
    }

}

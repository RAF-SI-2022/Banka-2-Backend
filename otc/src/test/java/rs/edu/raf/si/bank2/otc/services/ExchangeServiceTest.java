package rs.edu.raf.si.bank2.otc.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import rs.edu.raf.si.bank2.otc.configuration.RedisTestConfiguration;
import rs.edu.raf.si.bank2.otc.exceptions.ExchangeNotFoundException;
import rs.edu.raf.si.bank2.otc.models.mariadb.Exchange;
import rs.edu.raf.si.bank2.otc.repositories.mariadb.ExchangeRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
@Import(RedisTestConfiguration.class)
class ExchangeServiceTest {

    //    @Autowired
    //    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ExchangeRepository exchangeRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ExchangeService exchangeService;

    @Test
    void testFindAll() {
        List<Exchange> exchangeList = new ArrayList<>();
        Exchange exchange1 = new Exchange();
        exchange1.setId(1L);
        exchange1.setExchangeName("Exchange 1");
        exchangeList.add(exchange1);

        Exchange exchange2 = new Exchange();
        exchange2.setId(2L);
        exchange2.setExchangeName("Exchange 2");
        exchangeList.add(exchange2);

        when(exchangeRepository.findAll()).thenReturn(exchangeList);

        List<Exchange> result = exchangeService.findAll();

        assertEquals(2, result.size());
        assertEquals(exchange1, result.get(0));
        assertEquals(exchange2, result.get(1));
    }

    @Test
    void testFindById() {
        Exchange exchange = new Exchange();
        exchange.setId(6L);
        exchange.setExchangeName("Exchange 1");

        when(exchangeRepository.findById(6L)).thenReturn(Optional.of(exchange));

        Exchange result = exchangeService.findById(6L);

        assertEquals(exchange, result);
    }

    @Test
    void testFindByMicCode() {
        Exchange exchange = new Exchange();
        exchange.setId(1L);
        exchange.setExchangeName("Exchange 1");
        exchange.setMicCode("MIC");

        when(exchangeRepository.findExchangeByMicCode("MIC")).thenReturn(Optional.of(exchange));

        Exchange result = exchangeService.findByMicCode("MIC");

        assertEquals(exchange, result);
    }

    @Test
    void testFindByMicCodeWrongCode() {
        when(exchangeRepository.findExchangeByMicCode("MIC1")).thenReturn(Optional.empty());

        assertThrows(ExchangeNotFoundException.class, () -> exchangeService.findByMicCode("MIC1"));
    }

    @Test
    void testFindByAcronym() {
        Exchange exchange = new Exchange();
        exchange.setId(1L);
        exchange.setExchangeName("Exchange 1");
        exchange.setAcronym("EX1");

        when(exchangeRepository.findExchangeByAcronym("EX1")).thenReturn(Optional.of(exchange));

        Exchange result = exchangeService.findByAcronym("EX1");

        assertEquals(exchange, result);
    }

    @Test
    void testIsExchangeActiveEU() {
        Exchange exchange = new Exchange();
        exchange.setId(1L);
        exchange.setExchangeName("Exchange 1");
        exchange.setMicCode("MIC");
        exchange.setOpenTime(" 08:00");
        exchange.setCloseTime(" 16:00");
        exchange.setTimeZone("Europe/Belgrade");

        when(exchangeRepository.findExchangeByMicCode("MIC")).thenReturn(Optional.of(exchange));

        // ima mnogo boljih nacina ali me mrzi da refaktorisem ceo kod
        LocalTime now = LocalTime.now(ZoneId.of("Europe/Belgrade"));

        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(16, 0);

        boolean isExchangeActive = now.isAfter(startTime) && now.isBefore(endTime);
        assertEquals(isExchangeActive, exchangeService.isExchangeActive("MIC"));
    }

    @Test
    void testIsExchangeActiveNA() {
        Exchange exchange = new Exchange();
        exchange.setId(1L);
        exchange.setExchangeName("Exchange 1");
        exchange.setMicCode("AAAA");
        exchange.setOpenTime(" 08:00");
        exchange.setCloseTime(" 16:00");
        exchange.setTimeZone("America/New_York");

        when(exchangeRepository.findExchangeByMicCode("AAAA")).thenReturn(Optional.of(exchange));

        LocalTime now = LocalTime.now(ZoneId.of("America/New_York"));

        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(16, 0);

        boolean isExchangeActive = now.isAfter(startTime) && now.isBefore(endTime);
        assertEquals(isExchangeActive, exchangeService.isExchangeActive("AAAA"));
    }

    @Test
    void isExchangeActive_ExchangeNotFound() {

        when(exchangeRepository.findExchangeByMicCode("AAAA")).thenReturn(Optional.empty());
        assertThrows(ExchangeNotFoundException.class, () -> exchangeService.isExchangeActive("AAAA"));
    }
}

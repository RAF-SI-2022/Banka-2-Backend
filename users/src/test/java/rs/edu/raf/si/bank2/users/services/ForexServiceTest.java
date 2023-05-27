package rs.edu.raf.si.bank2.users.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.si.bank2.users.models.mariadb.Forex;
import rs.edu.raf.si.bank2.users.repositories.mariadb.ForexRepository;

@ExtendWith(MockitoExtension.class)
class ForexServiceTest {

    @Mock
    private ForexRepository forexRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ForexService forexService;

    @Test
    public void testFindAll() {
        List<Forex> forexList = new ArrayList<>();
        Forex forex = Forex.builder()
                .id(1L)
                .fromCurrencyName("Argentine Peso")
                .toCurrencyName("Australian Dollar")
                .fromCurrencyCode("ARS")
                .toCurrencyCode("AUD")
                .bidPrice("30")
                .askPrice("50")
                .exchangeRate("1.85")
                .build();
        Forex forex1 = Forex.builder()
                .id(1L)
                .fromCurrencyName("Hungarian Forint")
                .toCurrencyName("North Korean Won")
                .fromCurrencyCode("HUF")
                .toCurrencyCode("KPW")
                .bidPrice("15")
                .askPrice("45")
                .exchangeRate("3.3")
                .build();
        forexList.add(forex);
        forexList.add(forex1);
        when(forexRepository.findAll()).thenReturn(forexList);
        List<Forex> result = forexService.findAll();
        assertEquals(2, result.size());
        assertEquals(forex, result.get(0));
        assertEquals(forex1, result.get(1));
    }

    @Test
    public void testGetForexForCurrenciesInDb() {
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String systemTime = dateFormat.format(new Date());

        Forex forex = Forex.builder()
                .fromCurrencyCode(fromCurrency)
                .toCurrencyCode(toCurrency)
                .fromCurrencyName("United States Dollar")
                .toCurrencyName("Euro")
                .askPrice("0.845")
                .bidPrice("0.855")
                .exchangeRate("0.85")
                .lastRefreshed(systemTime)
                .timeZone("UTC")
                .build();

        when(forexRepository.findForexByFromCurrencyCodeAndToCurrencyCode(fromCurrency, toCurrency))
                .thenReturn(Optional.of(forex));

        Forex returnedForex = forexService.getForexForCurrencies(fromCurrency, toCurrency);

        assertEquals(forex, returnedForex);
    }

    @Test
    public void testGetForexForInvalidCurrenciesInDbAndApi() {
        String fromCurrency = "ABC";
        String toCurrency = "XYZ";

        when(forexRepository.findForexByFromCurrencyCodeAndToCurrencyCode(fromCurrency, toCurrency))
                .thenReturn(Optional.empty());

        assertNull(forexService.getForexForCurrencies(fromCurrency, toCurrency));
    }

    @Test
    public void testGetForexForCurrenciesNotInDb() {
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        Forex forex = forexService.getForexForCurrencies(fromCurrency, toCurrency);
        assertNotNull(forex);
        assertEquals(fromCurrency, forex.getFromCurrencyCode());
        assertEquals(toCurrency, forex.getToCurrencyCode());
    }

    @Test
    public void testGetForexForInvalidCurrenciesNotInDb() {
        String fromCurrency = "ABC";
        String toCurrency = "XYZ";
        Forex forex = forexService.getForexForCurrencies(fromCurrency, toCurrency);
        assertNull(forex);
    }

    @Test
    public void forexFromApi_NotNull() {

        String fromCurrency = "USD";
        String toCurrency = "EUR";
        LocalDateTime lastRefreshedBad = LocalDateTime.now().minus(Duration.ofMinutes(45));
        Date date = Date.from(lastRefreshedBad.atZone(ZoneId.systemDefault()).toInstant());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String systemTimeWithDecrement = dateFormat.format(date);

        Forex forex = Forex.builder()
                .fromCurrencyCode(fromCurrency)
                .toCurrencyCode(toCurrency)
                .fromCurrencyName("United States Dollar")
                .toCurrencyName("Euro")
                .askPrice("0.845")
                .bidPrice("0.855")
                .exchangeRate("0.85")
                .lastRefreshed(systemTimeWithDecrement)
                .timeZone("UTC")
                .build();

        when(forexRepository.findForexByFromCurrencyCodeAndToCurrencyCode(fromCurrency, toCurrency))
                .thenReturn(Optional.of(forex));

        Forex returnedForex = forexService.getForexForCurrencies(fromCurrency, toCurrency);

        assertEquals(forex.getFromCurrencyCode(), returnedForex.getFromCurrencyCode());
        assertEquals(forex.getToCurrencyCode(), returnedForex.getToCurrencyCode());
        // posto testiram api ovde testiram za vrednosti za koje znam da su fiksirane(ne menjaju se u zavisnosti od
        // vremena pozivanja)
    }

    @Test
    public void forexFromApi_BadDateParse() {

        String fromCurrency = "USD";
        String toCurrency = "EUR";

        Forex forex = Forex.builder()
                .fromCurrencyCode(fromCurrency)
                .toCurrencyCode(toCurrency)
                .fromCurrencyName("United States Dollar")
                .toCurrencyName("Euro")
                .askPrice("0.845")
                .bidPrice("0.855")
                .exchangeRate("0.85")
                .lastRefreshed("01-111-4234")
                .timeZone("UTC")
                .build();

        when(forexRepository.findForexByFromCurrencyCodeAndToCurrencyCode(fromCurrency, toCurrency))
                .thenReturn(Optional.of(forex));

        Forex result = forexService.getForexForCurrencies(fromCurrency, toCurrency);
        assertEquals(forex, result);
    }
}

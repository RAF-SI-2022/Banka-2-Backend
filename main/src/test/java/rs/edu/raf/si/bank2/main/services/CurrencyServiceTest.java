package rs.edu.raf.si.bank2.main.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import rs.edu.raf.si.bank2.main.exceptions.CurrencyNotFoundException;
import rs.edu.raf.si.bank2.main.models.mariadb.Currency;
import rs.edu.raf.si.bank2.main.repositories.mariadb.CurrencyRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.si.bank2.main.services.CurrencyService;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {
    @Mock
    CurrencyRepository currencyRepository;

    @InjectMocks
    CurrencyService currencyService;

    @Test
    public void findAll_success() {

        long id = 1L;

        List<Currency> currencyList = Arrays.asList(
                Currency.builder()
                        .id(id++)
                        .currencyName("Euro")
                        .currencyCode("EUR")
                        .currencySymbol("€")
                        .polity("European Union")
                        .inflations(null)
                        .build(),
                Currency.builder()
                        .id(id++)
                        .currencyName("Serbian Dinar")
                        .currencyCode("RSD")
                        .currencySymbol("RSD")
                        .polity("Serbia")
                        .inflations(null)
                        .build());

        when(currencyRepository.findAll()).thenReturn(currencyList);

        List<Currency> result = currencyService.findAll();
        assertEquals(currencyList, result);
    }

    @Test
    public void findById_success() {

        long id = 1L;

        Currency currency = Currency.builder()
                .id(id++)
                .currencyName("Euro")
                .currencyCode("EUR")
                .currencySymbol("€")
                .polity("European Union")
                .inflations(null)
                .build();

        when(currencyRepository.findById(id)).thenReturn(Optional.of(currency));

        Optional<Currency> result = currencyService.findById(id);

        assertEquals(currency, result.get());
    }

    @Test
    public void findById_throwsCurrencyNotFoundException() {

        long currencyId = 1l;

        when(currencyRepository.findById(currencyId)).thenReturn(Optional.empty());

        assertThrows(CurrencyNotFoundException.class, () -> currencyService.findById(currencyId));
    }
}

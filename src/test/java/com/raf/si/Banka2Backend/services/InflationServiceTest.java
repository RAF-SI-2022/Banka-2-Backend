package com.raf.si.Banka2Backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.raf.si.Banka2Backend.models.mariadb.Currency;
import com.raf.si.Banka2Backend.models.mariadb.Inflation;
import com.raf.si.Banka2Backend.repositories.mariadb.InflationRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InflationServiceTest {
    @Mock
    InflationRepository inflationRepository;

    @InjectMocks
    InflationService inflationService;

    @Test
    public void findAllByCurrencyId_success() {
        long id = 1L;
        long currencyId = id;
        Currency c = Currency.builder()
                .id(currencyId)
                .currencyName("Euro")
                .currencyCode("EUR")
                .currencySymbol("€")
                .polity("European Union")
                .inflations(null)
                .build();

        List<Inflation> inflationList = Arrays.asList(
                Inflation.builder()
                        .id(id++)
                        .year(1970)
                        .inflationRate(3.45f)
                        .currency(c)
                        .build(),
                Inflation.builder()
                        .id(id++)
                        .year(1971)
                        .inflationRate(5.24f)
                        .currency(c)
                        .build());

        when(inflationRepository.findAllByCurrencyId(c.getId())).thenReturn(inflationList);

        Optional<List<Inflation>> result = inflationService.findAllByCurrencyId(c.getId());
        assertEquals(inflationList, result.get());
    }

    @Test
    public void findAllByCurrencyIdAndYear_success() {
        long id = 1L;
        long currencyId = id;
        int year = 1970;
        Currency c = Currency.builder()
                .id(currencyId)
                .currencyName("Euro")
                .currencyCode("EUR")
                .currencySymbol("€")
                .polity("European Union")
                .inflations(null)
                .build();

        List<Inflation> inflationList = Arrays.asList(
                Inflation.builder()
                        .id(id++)
                        .year(year)
                        .inflationRate(3.45f)
                        .currency(c)
                        .build(),
                Inflation.builder()
                        .id(id++)
                        .year(year)
                        .inflationRate(5.24f)
                        .currency(c)
                        .build());

        when(inflationRepository.findAllByCurrencyIdAndYear(c.getId(), year)).thenReturn(inflationList);

        Optional<List<Inflation>> result = inflationService.findByYear(c.getId(), year);
        assertEquals(inflationList, result.get());
    }

    @Test
    public void save_success() {

        long id = 1L;

        Currency c = Currency.builder()
                .id(id)
                .currencyName("Euro")
                .currencyCode("EUR")
                .currencySymbol("€")
                .polity("European Union")
                .inflations(null)
                .build();

        Inflation newInflation = Inflation.builder()
                .id(id)
                .year(1970)
                .inflationRate(3.45f)
                .currency(c)
                .build();

        when(inflationRepository.save(newInflation)).thenReturn(newInflation);

        inflationService.save(newInflation);

        verify(inflationRepository).save(newInflation);
    }
}

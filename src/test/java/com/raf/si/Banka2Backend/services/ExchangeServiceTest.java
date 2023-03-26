package com.raf.si.Banka2Backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.raf.si.Banka2Backend.models.mariadb.Exchange;
import com.raf.si.Banka2Backend.repositories.mariadb.ExchangeRepository;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExchangeServiceTest {

  @Mock private ExchangeRepository exchangeRepository;
  @Mock private UserService userService;

  @InjectMocks private ExchangeService exchangeService;

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
    exchange.setId(1L);
    exchange.setExchangeName("Exchange 1");

    when(exchangeRepository.findById(1L)).thenReturn(Optional.of(exchange));

    Optional<Exchange> result = exchangeService.findById(1L);

    assertTrue(result.isPresent());
    assertEquals(exchange, result.get());
  }

  @Test
  void testFindByMicCode() {
    Exchange exchange = new Exchange();
    exchange.setId(1L);
    exchange.setExchangeName("Exchange 1");
    exchange.setMicCode("MIC");

    when(exchangeRepository.findExchangeByMicCode("MIC")).thenReturn(Optional.of(exchange));

    Optional<Exchange> result = exchangeService.findByMicCode("MIC");

    assertEquals(exchange, result.get());
  }

  @Test
  void testFindByMicCodeWrongCode() {
    Exchange exchange = new Exchange();
    exchange.setId(1L);
    exchange.setExchangeName("Exchange 1");
    exchange.setMicCode("MIC");

    when(exchangeRepository.findExchangeByMicCode("MIC1")).thenReturn(Optional.of(exchange));

    Optional<Exchange> result = exchangeService.findByMicCode("MIC1");

    assertEquals(exchange, result.get());
  }

  @Test
  void testFindByAcronym() {
    Exchange exchange = new Exchange();
    exchange.setId(1L);
    exchange.setExchangeName("Exchange 1");
    exchange.setAcronym("EX1");

    when(exchangeRepository.findExchangeByAcronym("EX1")).thenReturn(Optional.of(exchange));

    Optional<Exchange> result = exchangeService.findByAcronym("EX1");

    assertEquals(exchange, result.get());
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

    if (now.isAfter(startTime) && now.isBefore(endTime)) {
      assertTrue(exchangeService.isExchangeActive("MIC"));
    } else {
      assertFalse(exchangeService.isExchangeActive("MIC"));
    }
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

    // ima mnogo boljih nacina ali me mrzi da refaktorisem ceo kod
    LocalTime now = LocalTime.now(ZoneId.of("America/New_York"));

    LocalTime startTime = LocalTime.of(8, 0);
    LocalTime endTime = LocalTime.of(16, 0);

    if (now.isAfter(startTime) && now.isBefore(endTime)) {
      assertTrue(exchangeService.isExchangeActive("AAAA"));
    } else {
      assertFalse(exchangeService.isExchangeActive("AAAA"));
    }
  }
}

package com.raf.si.Banka2Backend.controllers;

import com.raf.si.Banka2Backend.models.mariadb.Forex;
import com.raf.si.Banka2Backend.services.ForexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/forex")
public class ForexController {

  private final ForexService forexService;

  @Autowired
  public ForexController(ForexService forexService) {
    this.forexService = forexService;
  }

  @GetMapping
  public ResponseEntity<?> getAll() {
    return ResponseEntity.ok().body(forexService.findAll());
  }

  @GetMapping("/{fromCurrency}/{toCurrency}")
  public Forex test(
      @PathVariable(name = "fromCurrency") String fromCurrency,
      @PathVariable(name = "toCurrency") String toCurrency) {
    return forexService.getForexForCurrencies(fromCurrency, toCurrency);
  }
}

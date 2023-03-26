package com.raf.si.Banka2Backend.controllers;

import com.raf.si.Banka2Backend.services.AuthorisationService;
import com.raf.si.Banka2Backend.services.ExchangeService;
import com.raf.si.Banka2Backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/exchange")
public class ExchangeController {
  private final AuthorisationService authorisationService;
  private final ExchangeService exchangeService;
  private final UserService userService;

  @Autowired
  public ExchangeController(
      AuthorisationService authorisationService,
      ExchangeService exchangeService,
      UserService userService) {
    this.authorisationService = authorisationService;
    this.exchangeService = exchangeService;
    this.userService = userService;
  }

  @GetMapping()
  public ResponseEntity<?> findAll() {
    return ResponseEntity.ok().body(exchangeService.findAll());
  }

  @GetMapping(value = "/id/{id}")
  public ResponseEntity<?> findById(@PathVariable(name = "id") Long id) {
    return ResponseEntity.ok().body(exchangeService.findById(id));
  }

  @GetMapping(value = "/status/{micCode}")
  public ResponseEntity<?> isExchangeActive(@PathVariable(name = "micCode") String micCode) {
    if (exchangeService.findByMicCode(micCode) != null) {
      return ResponseEntity.ok().body(exchangeService.isExchangeActive(micCode));
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping(value = "/acronym/{acronym}")
  public ResponseEntity<?> findByAcronym(@PathVariable(name = "acronym") String acronym) {
    return ResponseEntity.ok().body(exchangeService.findByAcronym(acronym));
  }
}

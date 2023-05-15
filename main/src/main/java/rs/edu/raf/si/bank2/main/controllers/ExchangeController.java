package rs.edu.raf.si.bank2.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.main.services.AuthorisationService;
import rs.edu.raf.si.bank2.main.services.ExchangeService;
import rs.edu.raf.si.bank2.main.services.UserService;

@RestController
@CrossOrigin
@RequestMapping("/api/exchange")
public class ExchangeController {
    private final AuthorisationService authorisationService;
    private final ExchangeService exchangeService;
    private final UserService userService;

    @Autowired
    public ExchangeController(
            AuthorisationService authorisationService, ExchangeService exchangeService, UserService userService) {
        this.authorisationService = authorisationService;
        this.exchangeService = exchangeService;
        this.userService = userService;
    }

    @GetMapping()
    @Cacheable(value = "exchanges")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok().body(exchangeService.findAll());
    }

    @GetMapping(value = "/id/{id}")
    @Cacheable(value = "exchanges", key = "#id")
    public ResponseEntity<?> findById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok().body(exchangeService.findById(id));
    }

    @GetMapping(value = "/status/{micCode}")
    @Cacheable(value = "exchanges", key = "#micCode")
    public ResponseEntity<?> isExchangeActive(@PathVariable(name = "micCode") String micCode) {
        if (exchangeService.findByMicCode(micCode).isPresent()) {
            return ResponseEntity.ok().body(exchangeService.isExchangeActive(micCode));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/acronym/{acronym}")
    @Cacheable(value = "exchanges", key = "#acronym")
    public ResponseEntity<?> findByAcronym(@PathVariable(name = "acronym") String acronym) {
        return ResponseEntity.ok().body(exchangeService.findByAcronym(acronym));
    }
}

package com.raf.si.Banka2Backend.controllers;

import com.raf.si.Banka2Backend.dto.InflationDto;
import com.raf.si.Banka2Backend.exceptions.CurrencyNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.Currency;
import com.raf.si.Banka2Backend.models.mariadb.Inflation;
import com.raf.si.Banka2Backend.services.CurrencyService;
import com.raf.si.Banka2Backend.services.InflationService;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/currencies")
public class CurrencyController {
    private final CurrencyService currencyService;
    private final InflationService inflationService;

    @Autowired
    public CurrencyController(CurrencyService currencyService, InflationService inflationService) {
        this.currencyService = currencyService;
        this.inflationService = inflationService;
    }

    @GetMapping()
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(this.currencyService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable(name = "id") Long id) {
        try {
            return ResponseEntity.ok(this.currencyService.findById(id));
        } catch (CurrencyNotFoundException currencyNotFoundException) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/code/{code}")
    public ResponseEntity<?> findByCurrencyCode(@PathVariable(name = "code") String currencyCode) {
        try {
            return ResponseEntity.ok(this.currencyService.findByCurrencyCode(currencyCode));
        } catch (CurrencyNotFoundException currencyNotFoundException) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/{id}/inflation")
    public ResponseEntity<?> findInflationByCurrencyId(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(this.inflationService.findAllByCurrencyId(id));
    }

    @GetMapping(value = "/{id}/inflation/{year}")
    public ResponseEntity<?> findInflationByCurrencyIdAndYear(
            @PathVariable(name = "id") Long id, @PathVariable(name = "year") Integer year) {
        return ResponseEntity.ok(this.inflationService.findByYear(id, year));
    }

    @PostMapping(value = "/inflation/add")
    public ResponseEntity<?> addInflation(@RequestBody @Valid InflationDto inflationDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Body is not valid. Should have inflationRate, year, currencyId");
        }
        Optional<Currency> currency;
        try {
            currency = this.currencyService.findById(inflationDto.getCurrencyId());
        } catch (CurrencyNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body("Currency with id: " + inflationDto.getCurrencyId() + " can not be found");
        }
        Inflation inflation = Inflation.builder()
                .inflationRate(inflationDto.getInflationRate())
                .year(inflationDto.getYear())
                .currency(currency.get())
                .build();
        this.inflationService.save(inflation);
        return ResponseEntity.ok(inflation);
    }
}

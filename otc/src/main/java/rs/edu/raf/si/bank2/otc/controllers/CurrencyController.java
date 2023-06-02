package rs.edu.raf.si.bank2.otc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.otc.exceptions.CurrencyNotFoundException;
import rs.edu.raf.si.bank2.otc.services.CommunicationService;
import rs.edu.raf.si.bank2.otc.services.CurrencyService;
import rs.edu.raf.si.bank2.otc.services.InflationService;
import rs.edu.raf.si.bank2.otc.services.interfaces.CommunicationInterface;

@RestController
@CrossOrigin
@RequestMapping("/api/currencies")
public class CurrencyController {
    private final CurrencyService currencyService;
    private final InflationService inflationService;
    private final CommunicationInterface communicationInterface;

    @Autowired
    public CurrencyController(CurrencyService currencyService, InflationService inflationService,
            CommunicationService communicationService) {
        this.communicationInterface = communicationService;
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

    // todo addInflation odkomentarisati ako bude zatrebalo :)
    //    @PostMapping(value = "/inflation/add")
    //    public ResponseEntity<?> addInflation(@RequestBody @Valid InflationDto inflationDto, BindingResult result) {
    //        if (result.hasErrors()) {
    //            return ResponseEntity.badRequest()
    //                    .body("Nepravilno uneti podaci. Potrebni su inflationRate, godina, currencyId");
    //        }
    //        Optional<Currency> currency;
    //        try {
    //            currency = this.currencyService.findById(inflationDto.getCurrencyId());
    //        } catch (CurrencyNotFoundException e) {
    //            return ResponseEntity.badRequest()
    //                    .body("Valuta sa id-em: " + inflationDto.getCurrencyId() + " nije pronadjena");
    //        }
    //        Inflation inflation = Inflation.builder()
    //                .inflationRate(inflationDto.getInflationRate())
    //                .year(inflationDto.getYear())
    //                .currency(currency.get())
    //                .build();
    //        this.inflationService.save(inflation);
    //        return ResponseEntity.ok(inflation);
    //    }

}
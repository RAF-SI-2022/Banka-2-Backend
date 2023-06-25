package rs.edu.raf.si.bank2.main.controllers;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.main.exceptions.CurrencyNotFoundException;
import rs.edu.raf.si.bank2.main.services.CurrencyService;
import rs.edu.raf.si.bank2.main.services.InflationService;
import rs.edu.raf.si.bank2.main.services.UserCommunicationService;
import rs.edu.raf.si.bank2.main.services.interfaces.UserCommunicationInterface;

@RestController
@CrossOrigin
@RequestMapping("/api/currencies")
@Timed
public class CurrencyController {
    private final CurrencyService currencyService;
    private final InflationService inflationService;
    private final UserCommunicationInterface userCommunicationInterface;

    @Autowired
    public CurrencyController(
            CurrencyService currencyService,
            InflationService inflationService,
            UserCommunicationService communicationService) {
        this.userCommunicationInterface = communicationService;
        this.currencyService = currencyService;
        this.inflationService = inflationService;
    }

    @Timed("controllers.balance.findAll")
    @GetMapping()
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(this.currencyService.findAll());
    }

    @Timed("controllers.balance.findById")
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable(name = "id") Long id) {
        try {
            return ResponseEntity.ok(this.currencyService.findById(id));
        } catch (CurrencyNotFoundException currencyNotFoundException) {
            return ResponseEntity.notFound().build();
        }
    }

    @Timed("controllers.balance.findByCurrencyCode")
    @GetMapping(value = "/code/{code}")
    public ResponseEntity<?> findByCurrencyCode(@PathVariable(name = "code") String currencyCode) {
        try {
            return ResponseEntity.ok(this.currencyService.findByCurrencyCode(currencyCode));
        } catch (CurrencyNotFoundException currencyNotFoundException) {
            return ResponseEntity.notFound().build();
        }
    }

    @Timed("controllers.balance.findInflationByCurrencyId")
    @GetMapping(value = "/{id}/inflation")
    public ResponseEntity<?> findInflationByCurrencyId(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(this.inflationService.findAllByCurrencyId(id));
    }

    @Timed("controllers.balance.findInflationByCurrencyIdAndYear")
    @GetMapping(value = "/{id}/inflation/{year}")
    public ResponseEntity<?> findInflationByCurrencyIdAndYear(
            @PathVariable(name = "id") Long id, @PathVariable(name = "year") Integer year) {
        return ResponseEntity.ok(this.inflationService.findByYear(id, year));
    }

    // todo addInflation odkomentarisati ako bude zatrebalo :)
    //    @Timed("controllers.balance.addInflation")
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

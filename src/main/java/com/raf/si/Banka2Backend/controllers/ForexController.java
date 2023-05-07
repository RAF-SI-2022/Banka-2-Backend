package com.raf.si.Banka2Backend.controllers;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import com.raf.si.Banka2Backend.dto.BuySellForexDto;
import com.raf.si.Banka2Backend.exceptions.BalanceNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.Forex;
import com.raf.si.Banka2Backend.services.BalanceService;
import com.raf.si.Banka2Backend.services.ForexService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/forex")
public class ForexController {

    private final ForexService forexService;
    private final BalanceService balanceService;

    @Autowired
    public ForexController(ForexService forexService, BalanceService balanceService) {
        this.forexService = forexService;
        this.balanceService = balanceService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok().body(forexService.findAll());
    }

    @GetMapping("/{fromCurrency}/{toCurrency}")
    public Forex getForexUsingFromAndToCurrency(
            @PathVariable(name = "fromCurrency") String fromCurrency,
            @PathVariable(name = "toCurrency") String toCurrency) {
        return forexService.getForexForCurrencies(fromCurrency, toCurrency);
    }

    @PostMapping("/buy-sell")
    public ResponseEntity<?> buyOrSell(@RequestBody @Valid BuySellForexDto dto) {
        Forex forex = forexService.getForexForCurrencies(dto.getFromCurrencyCode(), dto.getToCurrencyCode());
        //        if (forex == null) {
        //            return ResponseEntity.notFound().build();
        //        }
        String signedInUserEmail = getContext().getAuthentication().getName();
        try {
            boolean success = this.balanceService.buyOrSellCurrency(
                    signedInUserEmail,
                    forex.getFromCurrencyCode(),
                    forex.getToCurrencyCode(),
                    Float.parseFloat(forex.getExchangeRate()),
                    dto.getAmount(),
                    null);
            if (!success)
                return ResponseEntity.badRequest()
                        .body("Korisnik sa email-om "
                                + signedInUserEmail
                                + ", nema dovoljno novca u valuti "
                                + forex.getFromCurrencyName()
                                + " za kupovinu "
                                + dto.getAmount()
                                + " "
                                + dto.getToCurrencyCode()
                                + "("
                                + forex.getToCurrencyName()
                                + ")");
            return ResponseEntity.ok(forex);
        } catch (BalanceNotFoundException e1) {
            return ResponseEntity.badRequest()
                    .body("Korisnik sa email-om "
                            + signedInUserEmail
                            + ", nema dovoljno balansa u valuti "
                            + forex.getFromCurrencyName());
        } catch (Exception e3) {
            e3.printStackTrace();
            return ResponseEntity.internalServerError().body("Doslo je do neocekivane greske.");
        }
    }
}

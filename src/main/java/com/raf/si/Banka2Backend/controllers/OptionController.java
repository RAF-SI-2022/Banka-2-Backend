package com.raf.si.Banka2Backend.controllers;

import com.raf.si.Banka2Backend.services.OptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;

@RestController
@CrossOrigin
@RequestMapping("/api/options")
public class OptionController {

    private OptionService optionService;

    @Autowired
    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping("/{symbol}/{dateString}")
    public ResponseEntity<?> getStockBySymbol(@PathVariable String symbol, @PathVariable String dateString) throws ParseException {
        return ResponseEntity.ok().body(optionService.findByStockAndDate(symbol, dateString));
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<?> getStockBySymbol(@PathVariable String symbol) throws ParseException {
        return ResponseEntity.ok().body(optionService.findByStock(symbol));
    }

}
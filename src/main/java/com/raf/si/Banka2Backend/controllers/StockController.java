package com.raf.si.Banka2Backend.controllers;

import com.raf.si.Banka2Backend.exceptions.StockNotFoundException;
import com.raf.si.Banka2Backend.services.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin
@RequestMapping("/api/stock")
public class StockController {

  private StockService stockService;

  public StockController(StockService stockService) {
    this.stockService = stockService;
  }

  @GetMapping()
  public ResponseEntity<?> getAllStocks() {
    return ResponseEntity.ok().body(stockService.getAllStocks());
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getStockById(@PathVariable Long id) {
    try {
      return ResponseEntity.ok().body(stockService.getStockById(id));
    } catch (StockNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
    }
  }

  @GetMapping("/symbol/{symbol}")
  public ResponseEntity<?> getStockBySymbol(@PathVariable String symbol) {
    try {
      return ResponseEntity.ok().body(stockService.getStockBySymbol(symbol));
    } catch (StockNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
    }
  }

  @GetMapping("/{id}/history/{type}")
  public ResponseEntity<?> getStockHistoryByStockIdAndTimePeriod(
      @PathVariable Long id, @PathVariable String type) {
    return ResponseEntity.ok()
        .body(stockService.getStockHistoryByStockIdAndTimePeriod(id, type.toUpperCase()));
  }
}

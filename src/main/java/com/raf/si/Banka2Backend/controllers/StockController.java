package com.raf.si.Banka2Backend.controllers;

import com.raf.si.Banka2Backend.exceptions.StockNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.PermissionName;
import com.raf.si.Banka2Backend.models.mariadb.User;
import com.raf.si.Banka2Backend.requests.StockRequest;
import com.raf.si.Banka2Backend.services.AuthorisationService;
import com.raf.si.Banka2Backend.services.StockService;
import com.raf.si.Banka2Backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RestController
@CrossOrigin
@RequestMapping("/api/stock")
public class StockController {

  private StockService stockService;
  private final AuthorisationService authorisationService;
  private final UserService userService;

  @Autowired
  public StockController(StockService stockService, AuthorisationService authorisationService, UserService userService) {
    this.stockService = stockService;
    this.authorisationService = authorisationService;
    this.userService = userService;
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
  public ResponseEntity<?> getStockHistoryByStockIdAndTimePeriod(@PathVariable Long id, @PathVariable String type) {
    return ResponseEntity.ok()
        .body(stockService.getStockHistoryByStockIdAndTimePeriod(id, type.toUpperCase()));
  }

  @PostMapping(value = "/buy")
  public ResponseEntity<?> buyStock(@RequestBody StockRequest stockRequest){
    String signedInUserEmail = getContext().getAuthentication().getName();
    if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
      return ResponseEntity.status(401).body("You don't have permission to buy/sell.");
    }

    Optional<User> user = userService.findByEmail(signedInUserEmail);


    return stockService.buyStock(stockRequest);
  }

  @PostMapping(value = "/sell")
  public ResponseEntity<?> sellStock(@RequestBody StockRequest stockRequest){
    String signedInUserEmail = getContext().getAuthentication().getName();
    if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
      return ResponseEntity.status(401).body("You don't have permission to buy/sell.");
    }

    return stockService.sellStock(stockRequest);
  }

}

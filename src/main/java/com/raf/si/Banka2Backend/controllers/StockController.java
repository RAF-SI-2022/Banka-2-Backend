package com.raf.si.Banka2Backend.controllers;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import com.raf.si.Banka2Backend.exceptions.ExternalAPILimitReachedException;
import com.raf.si.Banka2Backend.exceptions.StockNotFoundException;
import com.raf.si.Banka2Backend.models.mariadb.PermissionName;
import com.raf.si.Banka2Backend.models.mariadb.User;
import com.raf.si.Banka2Backend.requests.StockRequest;
import com.raf.si.Banka2Backend.services.AuthorisationService;
import com.raf.si.Banka2Backend.services.StockService;
import com.raf.si.Banka2Backend.services.UserService;
import com.raf.si.Banka2Backend.services.UserStockService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin
@RequestMapping("/api/stock")
public class StockController {

    private StockService stockService;
    private final AuthorisationService authorisationService;
    private final UserService userService;
    private final UserStockService userStockService;

    @Autowired
    public StockController(
            StockService stockService,
            AuthorisationService authorisationService,
            UserService userService,
            UserStockService userStockService) {
        this.stockService = stockService;
        this.authorisationService = authorisationService;
        this.userService = userService;
        this.userStockService = userStockService;
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
        try {
            return ResponseEntity.ok()
                    .body(stockService.getStockHistoryForStockByIdAndType(id, type.toUpperCase()));
        } catch (StockNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ExternalAPILimitReachedException e) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/buy")
    public ResponseEntity<?> buyStock(@RequestBody StockRequest stockRequest) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("You don't have permission to buy/sell.");
        }

        Optional<User> user = userService.findByEmail(signedInUserEmail);
        if (user.isEmpty()) return ResponseEntity.status(400).body("Non existent user");
        stockRequest.setUserId(user.get().getId());
        return stockService.buyStock(stockRequest, user.get(), null);
    }

    @PostMapping(value = "/sell")
    public ResponseEntity<?> sellStock(@RequestBody StockRequest stockRequest) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("You don't have permission to buy/sell.");
        }
        Optional<User> user = userService.findByEmail(signedInUserEmail);
        if (user.isEmpty()) return ResponseEntity.status(400).body("Non existent user");
        stockRequest.setUserId(user.get().getId());
        return stockService.sellStock(stockRequest, null);
    }

    @GetMapping(value = "/user-stocks")
    public ResponseEntity<?> getAllUserStocks() {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401)
                    .body("You don't have permission to remove stock from market.");
        }

        return ResponseEntity.ok()
                .body(
                        this.stockService.getAllUserStocks(
                                userService.findByEmail(signedInUserEmail).get().getId()));
    }

    @PostMapping(value = "/remove/{symbol}")
    public ResponseEntity<?> removeStockFromMarket(@PathVariable String symbol) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401)
                    .body("You don't have permission to remove stock from market.");
        }
        return ResponseEntity.ok()
                .body(
                        userStockService.removeFromMarket(
                                userService.findByEmail(signedInUserEmail).get().getId(), symbol));
    }
}

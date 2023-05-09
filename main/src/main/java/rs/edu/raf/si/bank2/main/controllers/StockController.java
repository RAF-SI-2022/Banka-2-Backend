package rs.edu.raf.si.bank2.main.controllers;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import rs.edu.raf.si.bank2.main.exceptions.ExternalAPILimitReachedException;
import rs.edu.raf.si.bank2.main.exceptions.StockNotFoundException;
import rs.edu.raf.si.bank2.main.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.main.models.mariadb.User;
import rs.edu.raf.si.bank2.main.requests.StockRequest;
import rs.edu.raf.si.bank2.main.services.AuthorisationService;
import rs.edu.raf.si.bank2.main.services.StockService;
import rs.edu.raf.si.bank2.main.services.UserService;
import rs.edu.raf.si.bank2.main.services.UserStockService;
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
    public ResponseEntity<?> getStockHistoryByStockIdAndTimePeriod(@PathVariable Long id, @PathVariable String type) {
        try {
            return ResponseEntity.ok().body(stockService.getStockHistoryForStockByIdAndType(id, type.toUpperCase()));
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
            return ResponseEntity.status(401).body("Nemate dozvolu da kupujete akcije.");
        }

        Optional<User> user = userService.findByEmail(signedInUserEmail);
        return stockService.buyStock(stockRequest, user.get(), null);
    }

    @PostMapping(value = "/sell")
    public ResponseEntity<?> sellStock(@RequestBody StockRequest stockRequest) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da prodajete akcije.");
        }
        return stockService.sellStock(stockRequest, null);
    }

    @GetMapping(value = "/user-stocks")
    public ResponseEntity<?> getAllUserStocks() {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da pristupite svojim akcijama");
        }

        return ResponseEntity.ok()
                .body(this.stockService.getAllUserStocks(
                        userService.findByEmail(signedInUserEmail).get().getId()));
    }

    @PostMapping(value = "/remove/{symbol}")
    public ResponseEntity<?> removeStockFromMarket(@PathVariable String symbol) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da skinete akciju sa marketa.");
        }
        return ResponseEntity.ok()
                .body(userStockService.removeFromMarket(
                        userService.findByEmail(signedInUserEmail).get().getId(), symbol));
    }
}

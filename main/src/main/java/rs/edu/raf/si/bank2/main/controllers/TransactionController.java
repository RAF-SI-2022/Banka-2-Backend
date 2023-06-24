package rs.edu.raf.si.bank2.main.controllers;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.main.services.TransactionService;

@RestController
@CrossOrigin
@RequestMapping("/api/transactions")
@Timed
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Timed("controllers.transaction.getAllOrders")
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok().body(this.transactionService.getAll());
    }

    @Timed("controllers.transaction.getTransactionsByValue")
    @GetMapping("/{currencyCode}")
    public ResponseEntity<?> getTransactionsByValue(@PathVariable String currencyCode) {
        return ResponseEntity.ok().body(this.transactionService.getTransactionsByCurrencyValue(currencyCode));
    }
}

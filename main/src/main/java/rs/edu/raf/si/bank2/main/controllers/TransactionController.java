package rs.edu.raf.si.bank2.main.controllers;

import rs.edu.raf.si.bank2.main.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok().body(this.transactionService.getAll());
    }

    @GetMapping("/{currencyCode}")
    public ResponseEntity<?> getTransactionsByValue(@PathVariable String currencyCode) {
        return ResponseEntity.ok().body(this.transactionService.getTransactionsByCurrencyValue(currencyCode));
    }
}

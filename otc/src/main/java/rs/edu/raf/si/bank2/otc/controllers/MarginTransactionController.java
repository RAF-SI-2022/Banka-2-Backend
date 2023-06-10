package rs.edu.raf.si.bank2.otc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.otc.dto.MarginTransactionDto;
import rs.edu.raf.si.bank2.otc.models.mongodb.MarginTransaction;
import rs.edu.raf.si.bank2.otc.services.MarginTransactionService;

import java.util.List;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RestController
@CrossOrigin
@RequestMapping("/api/marginTransaction")
public class MarginTransactionController {

    private MarginTransactionService marginTransactionService;

    @Autowired
    public MarginTransactionController(MarginTransactionService marginTransactionService) {
        this.marginTransactionService = marginTransactionService;
    }

    @GetMapping
    public ResponseEntity<?> getAllTransactions() {
        List<MarginTransaction> transactions = marginTransactionService.findAll();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/byGroup/{group}")
    public ResponseEntity<?> getTransactionsByGroup(@PathVariable String group){
        return ResponseEntity.ok(marginTransactionService.findByGroup("USD", group));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable String id) {
        MarginTransaction transaction = marginTransactionService.findById(id);
        if (transaction != null) {
            return ResponseEntity.ok(transaction);
        } else {
            return ResponseEntity.status(404).body("Margin with provided Id doesn't exist.");
        }
    }

    @PostMapping(value = "/makeTransaction")
    public ResponseEntity<?> createMarginTransaction(@RequestBody MarginTransactionDto marginTransactionDto) {
//        System.err.println(marginTransactionDto);
        return ResponseEntity.ok().body(marginTransactionService.makeTransaction(marginTransactionDto, getContext().getAuthentication().getName()));
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateMarginTransaction(@PathVariable String id, @RequestBody MarginTransaction updatedTransaction) {
//        MarginTransaction existingTransaction = marginTransactionService.updateMarginTransaction(id,updatedTransaction);
//        if (existingTransaction != null) {
//            return ResponseEntity.ok(existingTransaction);
//        } else {
//            return ResponseEntity.status(404).body("No transaction to update");
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteMarginTransaction(@PathVariable String id) {
//        MarginTransaction existingTransaction = marginTransactionService.deleteMarginTransaction(id);
//        if (existingTransaction != null) {
//            return ResponseEntity.status(200).build();
//        } else {
//            return ResponseEntity.status(404).body("Requested resource doesn't exist");
//        }
//    }


    @GetMapping(value = "/email/{email}")
    public ResponseEntity<?> getMarginTransactionByEmail(@PathVariable String email) {
        List<MarginTransaction> existingMarginTransaction = marginTransactionService.findMarginsByEmail(email);
        return ResponseEntity.ok(existingMarginTransaction);
    }
}

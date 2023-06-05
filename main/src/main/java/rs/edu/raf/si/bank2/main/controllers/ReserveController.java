package rs.edu.raf.si.bank2.main.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.main.dto.ReserveDto;
import rs.edu.raf.si.bank2.main.models.mariadb.Balance;
import rs.edu.raf.si.bank2.main.models.mariadb.Future;
import rs.edu.raf.si.bank2.main.models.mariadb.UserOption;
import rs.edu.raf.si.bank2.main.models.mariadb.UserStock;
import rs.edu.raf.si.bank2.main.repositories.mariadb.BalanceRepository;
import rs.edu.raf.si.bank2.main.repositories.mariadb.FutureRepository;
import rs.edu.raf.si.bank2.main.repositories.mariadb.UserOptionRepository;
import rs.edu.raf.si.bank2.main.repositories.mariadb.UserStocksRepository;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/reserve")
public class ReserveController {

    private final UserOptionRepository userOptionRepository;
    private final UserStocksRepository userStocksRepository;
    private final FutureRepository futureRepository;
    private final BalanceRepository balanceRepository;

    @Autowired
    public ReserveController(UserOptionRepository userOptionRepository, UserStocksRepository userStocksRepository, FutureRepository futureRepository, BalanceRepository balanceRepository) {
        this.userOptionRepository = userOptionRepository;
        this.userStocksRepository = userStocksRepository;
        this.futureRepository = futureRepository;
        this.balanceRepository = balanceRepository;
    }

    @PostMapping("/reserveOption")
    public ResponseEntity<?> reserveUserOption(@RequestBody ReserveDto reserveDto){
        Optional<UserOption> userOption = userOptionRepository.findByIdAndUserId(reserveDto.getHartijaId(), reserveDto.getUserId());

        if (userOption.isEmpty()) return ResponseEntity.status(404).body("Stock nije pronadjen");
        if (userOption.get().getAmount() < reserveDto.getAmount()) return ResponseEntity.status(500).body("Nema dovoljno hartije za rezervaciju");

        userOption.get().setAmount(userOption.get().getAmount() - reserveDto.getAmount());
        userOptionRepository.save(userOption.get());

        return ResponseEntity.ok("Opcije su rezervisane");
    }

    @PostMapping("/reserveStock")
    public ResponseEntity<?> reserveUserStock(@RequestBody ReserveDto reserveDto){
        Optional<UserStock> userStock = userStocksRepository.findByIdAndStockId(reserveDto.getHartijaId(), reserveDto.getUserId());

        if (userStock.isEmpty()) return ResponseEntity.status(404).body("Stock nije pronadjen");
        if (userStock.get().getAmount() < reserveDto.getAmount()) return ResponseEntity.status(500).body("Nema dovoljno hartije za rezervaciju");

        userStock.get().setAmount(userStock.get().getAmount() - reserveDto.getAmount());
        userStocksRepository.save(userStock.get());

        return ResponseEntity.ok("Stockovi su rezervisani");
    }

    @PostMapping("/reserveFuture")
    public ResponseEntity<?> reserveFutureStock(@RequestBody ReserveDto reserveDto){
        Optional<Future> userFuture = futureRepository.findByIdAndUserId(reserveDto.getHartijaId(), reserveDto.getUserId());

        if (userFuture.isEmpty()) return ResponseEntity.status(404).body("Stock nije pronadjen");
//        if (!userFuture.get().isForSale()) return ResponseEntity.status(500).body("Future nije na prodaju");

        String saveString = userFuture.get().toString();
        futureRepository.delete(userFuture.get());
        return ResponseEntity.ok(saveString);
    }

    @PostMapping("/reserveMoney")
    public ResponseEntity<?> reserveMoney(@RequestBody ReserveDto reserveDto){
        //todo trenutno hard code na USD
        Optional<Balance> userBalance = balanceRepository.findBalanceByUserIdAndCurrencyId(reserveDto.getUserId(), reserveDto.getHartijaId());
                                                                                                                //!!! NIJE HARTIJA NEGO CURRENCY ID !!!
        if (userBalance.isEmpty()) return ResponseEntity.status(404).body("Stock nije pronadjen");
        if (userBalance.get().getFree() < reserveDto.getAmount()) return ResponseEntity.status(500).body("Nemas dovoljno ška");

        userBalance.get().setAmount( userBalance.get().getAmount() - reserveDto.getAmount());
        userBalance.get().setFree( userBalance.get().getFree() - reserveDto.getAmount());

        balanceRepository.save(userBalance.get());
        return ResponseEntity.ok("Novac uspesno rezervisan");
    }

}

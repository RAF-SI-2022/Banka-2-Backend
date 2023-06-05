package rs.edu.raf.si.bank2.main.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.main.dto.ReserveDto;
import rs.edu.raf.si.bank2.main.models.mariadb.*;
import rs.edu.raf.si.bank2.main.repositories.mariadb.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/reserve")
public class ReserveController {

    private final UserOptionRepository userOptionRepository;
    private final UserStocksRepository userStocksRepository;
    private final FutureRepository futureRepository;
    private final BalanceRepository balanceRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;

    @Autowired
    public ReserveController(UserOptionRepository userOptionRepository, UserStocksRepository userStocksRepository, FutureRepository futureRepository, BalanceRepository balanceRepository, UserRepository userRepository, StockRepository stockRepository) {
        this.userOptionRepository = userOptionRepository;
        this.userStocksRepository = userStocksRepository;
        this.futureRepository = futureRepository;
        this.balanceRepository = balanceRepository;
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
    }

    @PostMapping("/reserveOption")
    public ResponseEntity<?> reserveUserOption(@RequestBody ReserveDto reserveDto) {
        Optional<UserOption> userOption = userOptionRepository.findByIdAndUserId(reserveDto.getHartijaId(), reserveDto.getUserId());

        if (userOption.isEmpty()) return ResponseEntity.status(404).body("Stock nije pronadjen");
        if (userOption.get().getAmount() < reserveDto.getAmount())
            return ResponseEntity.status(500).body("Nema dovoljno hartije za rezervaciju");

        userOption.get().setAmount(userOption.get().getAmount() - reserveDto.getAmount());
        userOptionRepository.save(userOption.get());

        return ResponseEntity.ok("Opcije su rezervisane");
    }

    @PostMapping("/undoReserveOption")
    public ResponseEntity<?> undoReserveUserOption(@RequestBody ReserveDto reserveDto) {
        Optional<UserOption> userOption = userOptionRepository.findByIdAndUserId(reserveDto.getHartijaId(), reserveDto.getUserId());
        if (userOption.isEmpty()) return ResponseEntity.status(404).body("Stock nije pronadjen");

        userOption.get().setAmount(userOption.get().getAmount() + reserveDto.getAmount());
        userOptionRepository.save(userOption.get());

        return ResponseEntity.ok("Opcije su dodate");
    }

    @PostMapping("/reserveStock")
    public ResponseEntity<?> reserveUserStock(@RequestBody ReserveDto reserveDto) {
        Optional<UserStock> userStock = userStocksRepository.findByIdAndStockId(reserveDto.getHartijaId(), reserveDto.getUserId());

        if (userStock.isEmpty()) return ResponseEntity.status(404).body("Stock nije pronadjen");
        if (userStock.get().getAmount() < reserveDto.getAmount())
            return ResponseEntity.status(500).body("Nema dovoljno hartije za rezervaciju");

        userStock.get().setAmount(userStock.get().getAmount() - reserveDto.getAmount());
        userStocksRepository.save(userStock.get());

        return ResponseEntity.ok("Stockovi su rezervisani");
    }

    @PostMapping("/undoReserveStock")
    public ResponseEntity<?> undoReserveUserStock(@RequestBody ReserveDto reserveDto) {
        Optional<UserStock> userStock = userStocksRepository.findByIdAndStockId(reserveDto.getHartijaId(), reserveDto.getUserId());
        if (userStock.isEmpty()) return ResponseEntity.status(404).body("Stock nije pronadjen");

        userStock.get().setAmount(userStock.get().getAmount() + reserveDto.getAmount());
        userStocksRepository.save(userStock.get());

        return ResponseEntity.ok("Stockovi su dodati");
    }

    @PostMapping("/reserveFuture")
    public ResponseEntity<?> reserveFutureStock(@RequestBody ReserveDto reserveDto) {
        Optional<Future> userFuture = futureRepository.findByIdAndUserId(reserveDto.getHartijaId(), reserveDto.getUserId());
        if (userFuture.isEmpty()) return ResponseEntity.status(404).body("Stock nije pronadjen");

        String saveString = userFuture.get().toString();
        futureRepository.delete(userFuture.get());
        return ResponseEntity.ok(saveString);
    }

    @PostMapping("/undoReserveFuture")
    public ResponseEntity<?> undoReserveFutureStock(@RequestBody ReserveDto reserveDto) {
        String[] data = reserveDto.getFutureStorage().split(",");
        Optional<User> user = userRepository.findById(Long.parseLong(data[8]));

        if (user.isEmpty()) return ResponseEntity.status(404).body("Korisnik nije pronadjen");

        Future future = new Future(
                Long.parseLong(data[0]),
                data[1],
                Integer.parseInt(data[2]),
                data[3],
                Integer.parseInt(data[4]),
                data[5],
                data[6],
                false,
                user.get());
        return ResponseEntity.ok(futureRepository.save(future));
    }

    @PostMapping("/reserveMoney")
    public ResponseEntity<?> reserveMoney(@RequestBody ReserveDto reserveDto) {
        //todo trenutno hard code na USD
        Optional<Balance> userBalance = balanceRepository.findBalanceByUserIdAndCurrencyId(reserveDto.getUserId(), 138L);
        //!!! NIJE HARTIJA NEGO CURRENCY ID !!!
        if (userBalance.isEmpty()) return ResponseEntity.status(404).body("Balans nije pronadjen");
        if (userBalance.get().getFree() < reserveDto.getAmount())
            return ResponseEntity.status(500).body("Nemas dovoljno ška");

        userBalance.get().setAmount(userBalance.get().getAmount() - reserveDto.getAmount());
        userBalance.get().setFree(userBalance.get().getFree() - reserveDto.getAmount());

        balanceRepository.save(userBalance.get());
        return ResponseEntity.ok("Novac uspesno rezervisan");
    }

    @PostMapping("/undoReserveMoney")
    public ResponseEntity<?> undoReserveMoney(@RequestBody ReserveDto reserveDto) {
        //todo trenutno hard code na USD
        Optional<Balance> userBalance = balanceRepository.findBalanceByUserIdAndCurrencyId(reserveDto.getUserId(), 138L);
        //!!! NIJE HARTIJA NEGO CURRENCY ID !!!
        if (userBalance.isEmpty()) return ResponseEntity.status(404).body("Balans nije pronadjen");

        userBalance.get().setAmount(userBalance.get().getAmount() + reserveDto.getAmount());
        userBalance.get().setFree(userBalance.get().getFree() + reserveDto.getAmount());

        balanceRepository.save(userBalance.get());
        return ResponseEntity.ok("Novac uspesno dodat");
    }

    //            Optional<User> user = userRepository.findById(reserveDto.getUserId());
    //            if (user.isEmpty()) return ResponseEntity.status(404).body("Korisnik nije pronadjen");
    //
    //            Long stockId = Long.parseLong(reserveDto.getFutureStorage());//todo pomeri dole
    //            System.err.println(stockId);
    //            Optional<Stock> stock = stockRepository.findById(stockId);
    //
    //            UserStock newUserStock = new UserStock();
    //            newUserStock.setId(0L);
    //            newUserStock.setUser(user.get());
    //            newUserStock.setStock(stock.get());
    //            newUserStock.setAmount(reserveDto.getAmount());
    //            newUserStock.setAmountForSale(0);
    //
    //            return ResponseEntity.ok(userStocksRepository.save(newUserStock));


    @PostMapping("finalizeStock")
    public ResponseEntity<?> finalizeStockSale(@RequestBody ReserveDto reserveDto) {
        Optional<UserStock> userStock = userStocksRepository.findUserStockByUserIdAndStockId(reserveDto.getUserId(), reserveDto.getHartijaId());

        if (userStock.isEmpty()) {
            Optional<User> user = userRepository.findById(reserveDto.getUserId());
            if (user.isEmpty()) return ResponseEntity.status(404).body("Korisnik nije pronadjen");
            Optional<Stock> stock = stockRepository.findById(reserveDto.getHartijaId());

            UserStock newUserStock = new UserStock();
            newUserStock.setId(0L);
            newUserStock.setUser(user.get());
            newUserStock.setStock(stock.get());
            newUserStock.setAmount(reserveDto.getAmount());
            newUserStock.setAmountForSale(0);
            return ResponseEntity.ok(userStocksRepository.save(newUserStock));
        }

        userStock.get().setAmount(userStock.get().getAmount() + reserveDto.getAmount());
        userStocksRepository.save(userStock.get());

        return ResponseEntity.ok("Stockovi su dodati");
    }

    @PostMapping("finalizeFuture")
    public ResponseEntity<?> finalizeFutureSale() {


        return null;
    }

    @PostMapping("finalizeOption")
    public ResponseEntity<?> finalizeOptionSale() {


        return null;
    }

}

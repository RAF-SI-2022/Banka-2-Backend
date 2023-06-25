package rs.edu.raf.si.bank2.main.controllers;

import io.micrometer.core.annotation.Timed;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.main.dto.ReserveDto;
import rs.edu.raf.si.bank2.main.models.mariadb.*;
import rs.edu.raf.si.bank2.main.repositories.mariadb.*;

@RestController
@CrossOrigin
@RequestMapping("/api/reserve")
@Timed
public class ReserveController {

    private final UserOptionRepository userOptionRepository;
    private final UserStocksRepository userStocksRepository;
    private final FutureRepository futureRepository;
    private final BalanceRepository balanceRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final OptionRepository optionRepository;
    private final CurrencyRepository currencyRepository;

    @Autowired
    public ReserveController(
            UserOptionRepository userOptionRepository,
            UserStocksRepository userStocksRepository,
            FutureRepository futureRepository,
            BalanceRepository balanceRepository,
            UserRepository userRepository,
            StockRepository stockRepository,
            OptionRepository optionRepository, CurrencyRepository currencyRepository) {
        this.userOptionRepository = userOptionRepository;
        this.userStocksRepository = userStocksRepository;
        this.futureRepository = futureRepository;
        this.balanceRepository = balanceRepository;
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.optionRepository = optionRepository;
        this.currencyRepository = currencyRepository;
    }

    @Timed("controllers.reserve.reserveUserOption")
    @PostMapping("/reserveOption")
    public ResponseEntity<?> reserveUserOption(@RequestBody ReserveDto reserveDto) {
        Optional<UserOption> userOption =
                userOptionRepository.findByIdAndUserId(reserveDto.getHartijaId(), reserveDto.getUserId());

        if (userOption.isEmpty()) return ResponseEntity.status(404).body("User opcija nije pronadjena");
        if (userOption.get().getAmount() < reserveDto.getAmount())
            return ResponseEntity.status(500).body("Nema dovoljno hartije za rezervaciju");

        userOption.get().setAmount(userOption.get().getAmount() - reserveDto.getAmount());
        userOptionRepository.save(userOption.get());

        return ResponseEntity.ok("Opcije su rezervisane");
    }

    @Timed("controllers.reserve.undoReserveUserOption")
    @PostMapping("/undoReserveOption")
    public ResponseEntity<?> undoReserveUserOption(@RequestBody ReserveDto reserveDto) {
        Optional<UserOption> userOption =
                userOptionRepository.findByIdAndUserId(reserveDto.getHartijaId(), reserveDto.getUserId());
        if (userOption.isEmpty()) return ResponseEntity.status(404).body("User opcija nije pronadjena");

        userOption.get().setAmount(userOption.get().getAmount() + reserveDto.getAmount());
        userOptionRepository.save(userOption.get());

        return ResponseEntity.ok("Opcije su dodate");
    }

    @Timed("controllers.reserve.reserveUserStock")
    @PostMapping("/reserveStock")
    public ResponseEntity<?> reserveUserStock(@RequestBody ReserveDto reserveDto) {

        System.err.println(reserveDto);

        Optional<UserStock> userStock =
                userStocksRepository.findUserStockById(reserveDto.getHartijaId());

        if (userStock.isEmpty()) return ResponseEntity.status(404).body("Stock nije pronadjen");
        if (userStock.get().getAmount() < reserveDto.getAmount())
            return ResponseEntity.status(500).body("Nema dovoljno hartije za rezervaciju");

        userStock.get().setAmount(userStock.get().getAmount() - reserveDto.getAmount());
        userStocksRepository.save(userStock.get());

        return ResponseEntity.ok("Stockovi su rezervisani");
    }

    @Timed("controllers.reserve.undoReserveUserStock")
    @PostMapping("/undoReserveStock")
    public ResponseEntity<?> undoReserveUserStock(@RequestBody ReserveDto reserveDto) {
        Optional<UserStock> userStock =
                userStocksRepository.findByIdAndStockId(reserveDto.getHartijaId(), reserveDto.getUserId());
        if (userStock.isEmpty()) return ResponseEntity.status(404).body("Stock nije pronadjen");

        userStock.get().setAmount(userStock.get().getAmount() + reserveDto.getAmount());
        userStocksRepository.save(userStock.get());

        return ResponseEntity.ok("Stockovi su dodati");
    }

    @Timed("controllers.reserve.reserveFutureStock")
    @PostMapping("/reserveFuture")
    public ResponseEntity<?> reserveFutureStock(@RequestBody ReserveDto reserveDto) {
        Optional<Future> userFuture =
                futureRepository.findByIdAndUserId(reserveDto.getHartijaId(), reserveDto.getUserId());
        if (userFuture.isEmpty()) return ResponseEntity.status(404).body("Future nije pronadjen");

        String saveString = userFuture.get().toString();
        futureRepository.delete(userFuture.get());
        return ResponseEntity.ok(saveString);
    }

    @Timed("controllers.reserve.undoReserveFutureStock")
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

    @Timed("controllers.reserve.reserveMoney")
    @PostMapping("/reserveMoney")
    public ResponseEntity<?> reserveMoney(@RequestBody ReserveDto reserveDto) {
        // todo trenutno hard code na USD

        Optional<Currency> usd = currencyRepository.findCurrencyByCurrencyCode("USD");
        Optional<Balance> userBalance = balanceRepository.findBalanceByUserIdAndCurrencyId(reserveDto.getUserId(), usd.get().getId());
        // !!! NIJE HARTIJA NEGO CURRENCY ID !!!
        if (userBalance.isEmpty()) return ResponseEntity.status(404).body("Balans nije pronadjen");

        Float priceChange = Float.parseFloat(reserveDto.getFutureStorage());
        if (userBalance.get().getFree() < priceChange)
            return ResponseEntity.status(500).body("Nemas dovoljno ška");

        userBalance.get().setAmount(userBalance.get().getAmount() - priceChange);
        userBalance.get().setFree(userBalance.get().getFree() - priceChange);

        balanceRepository.save(userBalance.get());
        return ResponseEntity.ok("Novac uspesno rezervisan");
    }

    @Timed("controllers.reserve.undoReserveMoney")
    @PostMapping("/undoReserveMoney")
    public ResponseEntity<?> undoReserveMoney(@RequestBody ReserveDto reserveDto) {
        // todo trenutno hard code na USD

        Optional<Currency> usd = currencyRepository.findCurrencyByCurrencyCode("USD");
        Optional<Balance> userBalance =
                balanceRepository.findBalanceByUserIdAndCurrencyId(reserveDto.getUserId(), usd.get().getId());
        // !!! NIJE HARTIJA NEGO CURRENCY ID !!!
        if (userBalance.isEmpty()) return ResponseEntity.status(404).body("Balans nije pronadjen");

        Float priceChange = Float.parseFloat(reserveDto.getFutureStorage());
        userBalance.get().setAmount(userBalance.get().getAmount() + priceChange);
        userBalance.get().setFree(userBalance.get().getFree() + priceChange);

        balanceRepository.save(userBalance.get());
        return ResponseEntity.ok("Novac uspesno dodat");
    }

    @Timed("controllers.reserve.finalizeStockBuy")
    @PostMapping("finalizeStock")
    public ResponseEntity<?> finalizeStockBuy(@RequestBody ReserveDto reserveDto) {
        Optional<UserStock> userStock =
                userStocksRepository.findUserStockByUserIdAndStockId(reserveDto.getUserId(), reserveDto.getHartijaId());

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

    @Timed("controllers.reserve.finalizeOptionBuy")
    @PostMapping("finalizeOption")
    public ResponseEntity<?> finalizeOptionBuy(@RequestBody ReserveDto reserveDto) {

        String[] cut = reserveDto.getFutureStorage().split(",");
        Optional<User> user = userRepository.findById(reserveDto.getUserId());
        if (user.isEmpty()) return ResponseEntity.status(404).body("Korisnik nije pronadjen");
        Optional<Option> option = optionRepository.findById(Long.parseLong(cut[0]));

        // 1,20,CALL,2023-06-09,85,AAPL, cena
        // option ffield
        // - option id - premium - type (CALL ...) - expiriation date - strke - stock_symbol - price

        UserOption newUserOption = new UserOption();
        newUserOption.setUser(user.get());
        newUserOption.setOption(option.get());
        newUserOption.setPremium(Double.parseDouble(cut[1]));
        newUserOption.setAmount(reserveDto.getAmount());
        newUserOption.setType(cut[2]);
        LocalDate date = LocalDate.parse(cut[3]);
        newUserOption.setExpirationDate(date);
        newUserOption.setStrike(Double.parseDouble(cut[4]));
        newUserOption.setStockSymbol(cut[5]);

        userOptionRepository.save(newUserOption);
        return ResponseEntity.ok("Stockovi su dodati");
    }

    @Timed("controllers.reserve.finalizeFutureStock")
    @PostMapping("/finalizeFuture")
    public ResponseEntity<?> finalizeFutureStock(@RequestBody ReserveDto reserveDto) {
        String[] data = reserveDto.getFutureStorage().split(",");
        Optional<User> user = userRepository.findById(reserveDto.getUserId());

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
}

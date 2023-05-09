package rs.edu.raf.si.bank2.main.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.edu.raf.si.bank2.main.exceptions.*;
import rs.edu.raf.si.bank2.main.models.mariadb.*;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.*;
import rs.edu.raf.si.bank2.main.repositories.mariadb.BalanceRepository;
import rs.edu.raf.si.bank2.main.repositories.mariadb.OrderRepository;
import rs.edu.raf.si.bank2.main.services.interfaces.BalanceServiceInterface;

@Service
public class BalanceService implements BalanceServiceInterface {
    private final BalanceRepository balanceRepository;
    private final TransactionService transactionService;
    private final CurrencyService currencyService;
    private final UserService userService;
    private final OrderRepository orderRepository;

    @Autowired
    public BalanceService(
            BalanceRepository balanceRepository,
            TransactionService transactionService,
            CurrencyService currencyService,
            UserService userService,
            OrderRepository orderRepository) {
        this.balanceRepository = balanceRepository;
        this.transactionService = transactionService;
        this.currencyService = currencyService;
        this.userService = userService;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public boolean buyOrSellCurrency( // for forex
            String userEmail,
            String fromCurrencyCode,
            String toCurrencyCode,
            Float exchangeRate,
            Integer amount,
            ForexOrder forexOrder) {
        // Update existing balance(for fromCurrency)
        Optional<User> u = this.userService.findByEmail(userEmail);
        if (!u.isPresent()) throw new UserNotFoundException(userEmail);
        Balance balanceForFromCurrency = this.findBalanceByUserEmailAndCurrencyCode(userEmail, fromCurrencyCode);
        try {
            this.reserveAmount(amount.floatValue(), userEmail, fromCurrencyCode);
            this.decreaseBalance(userEmail, fromCurrencyCode, amount.floatValue());
        } catch (NotEnoughMoneyException | NotEnoughReservedMoneyException e) {
            ForexOrder fo = forexOrder == null
                    ? new ForexOrder(
                            0l,
                            OrderType.FOREX,
                            OrderTradeType.BUY,
                            OrderStatus.WAITING,
                            fromCurrencyCode + " " + toCurrencyCode,
                            amount,
                            exchangeRate,
                            this.getTimestamp(),
                            u.get())
                    : forexOrder;
            fo = (ForexOrder) this.orderRepository.save(fo);
            return false;
        }

        // Check if balance for toCurrency exists. If yes update it with new amount, if not create it.
        Optional<Balance> balanceForToCurrency =
                this.balanceRepository.findBalanceByUser_EmailAndCurrency_CurrencyCode(userEmail, toCurrencyCode);
        Optional<Currency> newCurrency = this.currencyService.findByCurrencyCode(toCurrencyCode);
        ForexOrder fo = null;
        fo = forexOrder == null
                ? new ForexOrder(
                        0l,
                        OrderType.FOREX,
                        OrderTradeType.BUY,
                        OrderStatus.IN_PROGRESS,
                        fromCurrencyCode + " " + toCurrencyCode,
                        amount,
                        exchangeRate,
                        this.getTimestamp(),
                        u.get())
                : forexOrder;
        System.out.println("FO ID: " + fo.getId());
        fo = this.orderRepository.save(fo);
        System.out.println("FO ID: " + fo.getId());
        Transaction transaction = this.transactionService.createTransaction(fo, balanceForFromCurrency, (float) amount);
        this.transactionService.save(transaction);
        if (balanceForToCurrency.isPresent()) {
            this.increaseBalance(userEmail, toCurrencyCode, amount * exchangeRate);
        } else {
            // create new balance for toCurrency
            Balance newBalanceForToCurrency = new Balance();
            newBalanceForToCurrency.setUser(
                    this.userService.findByEmail(userEmail).get());
            newBalanceForToCurrency.setCurrency(newCurrency.get());
            newBalanceForToCurrency.setAmount(amount * exchangeRate);
            newBalanceForToCurrency.setReserved(0f);
            newBalanceForToCurrency.setFree(amount * exchangeRate);
            newBalanceForToCurrency.setType(BalanceType.CASH);
            this.balanceRepository.save(newBalanceForToCurrency);
        }
        this.updateOrderStatus(fo.getId(), OrderStatus.COMPLETE);
        return true;
    }

    private void updateOrderStatus(Long id, OrderStatus orderStatus) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            order.get().setStatus(orderStatus);
            this.orderRepository.save(order.get());
        }
    }

    @Override
    public List<Balance> findAllByUserId(Long userId) {
        return this.balanceRepository.findAllByUser_Id(userId);
    }

    @Override
    public Balance findBalanceByUserIdAndCurrency(Long userId, String currencyCode) {
        Optional<Currency> currency = this.currencyService.findCurrencyByCurrencyCode(currencyCode);
        if (currency.isEmpty()) return null;

        Optional<Balance> balance = balanceRepository.findBalanceByUserIdAndCurrencyId(
                userId, currency.get().getId());
        if (balance.isPresent()) return balance.get();
        return null;
    }

    @Override
    public Balance findBalanceByUserEmailAndCurrencyCode(String userEmail, String currencyCode) {
        Optional<Balance> balance =
                this.balanceRepository.findBalanceByUser_EmailAndCurrency_CurrencyCode(userEmail, currencyCode);
        if (balance.isEmpty()) {
            throw new BalanceNotFoundException(userEmail, currencyCode);
        }
        return balance.get();
    }

    @Override
    public Balance reserveAmount(Float amount, String userEmail, String currencyCode) {
        Balance balance = this.findBalanceByUserEmailAndCurrencyCode(userEmail, currencyCode);
        if (balance.getFree() < amount) {
            throw new NotEnoughMoneyException();
        }
        balance.setReserved(balance.getReserved() + amount);
        balance.setFree(balance.getFree() - amount);
        return this.balanceRepository.save(balance);
    }

    @Override
    public Balance increaseBalance(String userEmail, String currencyCode, Float amount)
            throws CurrencyNotFoundException, UserNotFoundException {
        Optional<Balance> balance =
                this.balanceRepository.findBalanceByUser_EmailAndCurrency_CurrencyCode(userEmail, currencyCode);
        if (balance.isPresent()) {
            balance.get().setFree(balance.get().getFree() + amount);
            balance.get().setAmount(balance.get().getAmount() + amount);
            this.balanceRepository.save(balance.get());
            return balance.get();
        } else {
            Balance newBalance = new Balance();
            newBalance.setUser(this.userService.findByEmail(userEmail).get());
            newBalance.setCurrency(
                    this.currencyService.findByCurrencyCode(currencyCode).get());
            newBalance.setReserved(0f);
            newBalance.setFree(amount);
            newBalance.setAmount(amount);
            return this.balanceRepository.save(newBalance);
        }
    }

    /**
     * Da bi se ova metoda uspesno izvrsila, prethodno je potrebno rezervisati sumu.
     */
    @Override
    public Balance decreaseBalance(String userEmail, String currencyCode, Float amount)
            throws BalanceNotFoundException, NotEnoughMoneyException {
        Balance balance = this.findBalanceByUserEmailAndCurrencyCode(userEmail, currencyCode);
        if (balance.getAmount() < amount) {
            throw new NotEnoughMoneyException();
        }
        if (balance.getReserved() < amount) {
            throw new NotEnoughReservedMoneyException(
                    "Not enough money has been previously reserved, so balance for user with mail" + userEmail
                            + "and currency code: " + currencyCode + " can't be decreased.");
        }
        balance.setReserved(balance.getReserved() - amount);
        balance.setAmount(balance.getAmount() - amount);
        return this.balanceRepository.save(balance);
    }

    /**
     * Ova metoda se zove po zavrsetku svih transakcija iz nekog ordera.
     * */
    @Override
    public Balance updateBalance(Order order, String userEmail, String currencyCode) {
        List<Transaction> orderTransactions = this.transactionService.findAllByOrderId(order.getId());
        Float money = 0f;
        for (Transaction transaction : orderTransactions) {
            money += transaction.getAmount();
        }
        if (order.getTradeType().equals(OrderTradeType.BUY)) {
            return this.decreaseBalance(userEmail, currencyCode, money);
        } else {
            return this.increaseBalance(userEmail, currencyCode, money);
        }
    }

    @Override
    public boolean hasEnoughFounds(Float amountNeeded, String userEmail, String currencyCode) {
        Balance balance = this.findBalanceByUserEmailAndCurrencyCode(userEmail, currencyCode);
        return balance.getFree() >= amountNeeded;
    }

    @Override
    public Balance save(Balance balance) {
        return balanceRepository.save(balance);
    }

    @Override
    public void exchangeMoney(String userSellerEmail, String userBuyerEmail, Float amount, String currencyCode) {
        decreaseBalance(userBuyerEmail, currencyCode, amount);
        increaseBalance(userSellerEmail, currencyCode, amount);
    }

    private String getTimestamp() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentDateTime.format(formatter);
    }
}

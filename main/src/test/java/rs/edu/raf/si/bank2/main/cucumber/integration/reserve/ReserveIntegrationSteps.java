package rs.edu.raf.si.bank2.main.cucumber.integration.reserve;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.main.models.mariadb.*;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.*;
import rs.edu.raf.si.bank2.main.repositories.mariadb.*;
import rs.edu.raf.si.bank2.main.services.OptionService;
import rs.edu.raf.si.bank2.main.services.OrderService;
import rs.edu.raf.si.bank2.main.services.StockService;
import rs.edu.raf.si.bank2.main.services.UserService;

public class ReserveIntegrationSteps extends ReserveIntegrationTestConfig {
    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    protected OptionService optionService;

    @Autowired
    private UserOptionRepository userOptionRepository;

    @Autowired
    private UserStocksRepository userStocksRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private ExchangeRepository exchangeRepository;
    private Stock stock;
    private Option option;
    private UserOption userOption;

    protected static String token;

    @Given("user logs in")
    public void user_logs_in() {
        token = null;
        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/auth/login")
                                    .contentType("application/json")
                                    .content(
                                            """
                                                    {
                                                      "email": "anesic3119rn+banka2backend+admin@raf.rs",
                                                      "password": "admin"
                                                    }
                                                    """))
                    .andExpect(status().isOk())
                    .andReturn();
            token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");
        } catch (Exception e) {
            fail("Test user failed to login");
        }
    }

    @When("user is logged in")
    public void user_is_logged_in() {
        try {
            assertNotEquals(token, null);
            assertNotEquals(token, "");
        } catch (Exception e) {
            fail("User token null or empty - not logged in properly");
        }
    }

    @Given("user-option exists in db")
    public void userOptionExistsInDb() {
        Optional<Option> optionalOption = this.optionService.findById(111L);
        if (optionalOption.isPresent()) {
            this.option = optionalOption.get();
        } else {
            this.option = Option.builder()
                    .id(111L)
                    .stockSymbol("AAPL")
                    .contractSymbol("a")
                    .optionType("stock")
                    .strike(50d)
                    .impliedVolatility(60d)
                    .price(100d)
                    .expirationDate(LocalDate.of(2024, 11, 10))
                    .openInterest(32)
                    .contractSize(44)
                    .maintenanceMargin(89d)
                    .bid(33d)
                    .ask(23d)
                    .changePrice(110d)
                    .percentChange(23d)
                    .inTheMoney(true)
                    .build();
        }
        Optional<UserOption> userOptionOptional = this.userOptionRepository.findById(111L);
        User user = this.userService
                .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                .get();
        if (userOptionOptional.isPresent()) {
            this.userOption = userOptionOptional.get();
        } else {
            this.userOption = UserOption.builder()
                    .id(111L)
                    .user(user)
                    .option(option)
                    .premium(33d)
                    .amount(222)
                    .type("stock")
                    .expirationDate(LocalDate.of(2024, 11, 10))
                    .strike(22d)
                    .stockSymbol("AAPL")
                    .build();
            this.userOption = this.userOptionRepository.save(this.userOption);
        }
    }
    @Given("stock exists in db")
    public void stockExistsInDb() {
       Optional<Stock> optionalStock = this.stockRepository.findById(111L);
       if(optionalStock.isPresent()) {
           this.stock = optionalStock.get();
       } else {
           Exchange exchange = null;
           List<Exchange> exchangeList = this.exchangeRepository.findAll();
           if(exchangeList.size() == 0) {
               Currency currency = null;
               List<Currency> currencyList = this.currencyRepository.findAll();
               if(currencyList.size() == 0) {
                   currency = Currency.builder()
                           .id(1L)
                           .currencyName("United States Dollar")
                           .currencyCode("USD")
                           .currencySymbol("$")
                           .polity("United States")
                           .inflations(new ArrayList<>())
                           .build();
                   currency = this.currencyRepository.save(currency);
               } else {
                   currency = currencyList.get(0);
               }
               exchange = new Exchange("New York exchange", "NYEM", "11122223333", "United States", currency, "1", "9:00", "17:00");
           } else {
               exchange = exchangeList.get(0);
           }
           this.stock = Stock.builder()
                .id(111L)
                .symbol("AAPL")
                .companyName("Test company")
                .outstandingShares(41L)
                .dividendYield(new BigDecimal(23))
                .exchange(exchange)
                .build();
           this.stock = this.stockRepository.save(this.stock);
       }
    }

    @Then("user reserves user-option")
    public void userReservesUserOption() {
        User user = this.userService
                .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                .get();
        try {
            mockMvc.perform(post("/api/reserve/reserveOption")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + this.token)
                            .content(String.format(
                                    """
                                                    {
                                                      "userId": "%s",
                                                      "hartijaId": "%s",
                                                      "amount": "1",
                                                      "futureStorage": "future storage string"
                                                    }
                                                    """,
                                    user.getId(), this.option.getId())))
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user cancels reservation")
    public void userCancelsReservation() {
        User user = this.userService
                .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                .get();
        try {
            mockMvc.perform(post("/api/reserve/undoReserveOption")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + this.token)
                            .content(String.format(
                                    """
                                                    {
                                                      "userId": "%s",
                                                      "hartijaId": "%s",
                                                      "amount": "1",
                                                      "futureStorage": "future storage string"
                                                    }
                                                    """,
                                    user.getId(), this.option.getId())))
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user reserves stock")
    public void userReservesStock() {
        User user = this.userService
                .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                .get();
        try {
            mockMvc.perform(post("/api/reserve/reserveStock")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + this.token)
                            .content(String.format(
                                    """
                                                    {
                                                      "userId": "%s",
                                                      "hartijaId": "%s",
                                                      "amount": "1",
                                                      "futureStorage": "future storage string"
                                                    }
                                                    """,
                                    user.getId(), this.option.getId())))
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user cancels stock reservation")
    public void userCancelsStockReservation() {
        User user = this.userService
                .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                .get();
        try {
            mockMvc.perform(post("/api/reserve/undoReserveStock")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + this.token)
                            .content(String.format(
                                    """
                                                    {
                                                      "userId": "%s",
                                                      "hartijaId": "%s",
                                                      "amount": "1",
                                                      "futureStorage": "future storage string"
                                                    }
                                                    """,
                                    user.getId(), this.option.getId())))
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user reserves future")
    public void userReservesFuture() {
        User user = this.userService
                .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                .get();
        try {
            mockMvc.perform(post("/api/reserve/reserveFuture")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + this.token)
                            .content(String.format(
                                    """
                                                    {
                                                      "userId": "%s",
                                                      "hartijaId": "%s",
                                                      "amount": "1",
                                                      "futureStorage": "future storage string"
                                                    }
                                                    """,
                                    user.getId(), this.option.getId())))
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user cancels future reservation")
    public void userCancelsFutureReservation() {
        User user = this.userService
                .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                .get();
        try {
            mockMvc.perform(post("/api/reserve/undoReserveFuture")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + this.token)
                            .content(String.format(
                                    """
                                                    {
                                                      "userId": "%s",
                                                      "hartijaId": "%s",
                                                      "amount": 1,
                                                      "futureStorage": "0,1,2,3,4,5,6,7,%s"
                                                    }
                                                    """,
                                    user.getId(), this.option.getId(), user.getId())))
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user reserves money")
    public void userReservesMoney() {
        User user = this.userService
                .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                .get();
        try {
            mockMvc.perform(post("/api/reserve/reserveMoney")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + this.token)
                            .content(String.format(
                                    """
                                                    {
                                                      "userId": "%s",
                                                      "hartijaId": "%s",
                                                      "amount": 1,
                                                      "futureStorage": "1"
                                                    }
                                                    """,
                                    user.getId(), this.option.getId())))
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user cancels money reservation")
    public void userCancelsMoneyReservation() {
        User user = this.userService
                .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                .get();
        try {
            mockMvc.perform(post("/api/reserve/undoReserveMoney")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + this.token)
                            .content(String.format(
                                    """
                                                    {
                                                      "userId": "%s",
                                                      "hartijaId": "%s",
                                                      "amount": 1,
                                                      "futureStorage": "1"
                                                    }
                                                    """,
                                    user.getId(), this.option.getId())))
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user finalizes stock")
    public void userFinalizesStock() {
        User user = this.userService
                .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                .get();
        try {
            mockMvc.perform(post("/api/reserve/finalizeStock")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + this.token)
                            .content(String.format(
                                    """
                                                    {
                                                      "userId": "%s",
                                                      "hartijaId": "%s",
                                                      "amount": 1,
                                                      "futureStorage": "1"
                                                    }
                                                    """,
                                    user.getId(), this.stock.getId())))
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Then("user finalizes option")
    public void userFinalizesOption() {
        User user = this.userService
                .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                .get();
        try {
            mockMvc.perform(post("/api/reserve/finalizeOption")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + this.token)
                            .content(String.format(
                                    """
                                                    {
                                                      "userId": "%s",
                                                      "hartijaId": "%s",
                                                      "amount": 1,
                                                      "futureStorage": "%s,1,future,2023-11-29,4,AAPL,6,7,8"
                                                    }
                                                    """,
                                    user.getId(), this.option.getId(), this.option.getId())))
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Then("user finalizes future")
    public void userFinalizesFuture() {
        User user = this.userService
                .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                .get();
        try {
            mockMvc.perform(post("/api/reserve/finalizeFuture")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + this.token)
                            .content(String.format(
                                    """
                                                    {
                                                      "userId": "%s",
                                                      "hartijaId": "%s",
                                                      "amount": 1,
                                                      "futureStorage": "93,futureName,12,unit,10,type,2025-06-23,6,7,8"
                                                    }
                                                    """,
                                    user.getId(), this.option.getId(), this.option.getId())))
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}

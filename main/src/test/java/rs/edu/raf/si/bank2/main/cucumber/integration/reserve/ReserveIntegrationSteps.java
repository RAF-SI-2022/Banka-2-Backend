package rs.edu.raf.si.bank2.main.cucumber.integration.reserve;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.LocalDate;
import java.util.Optional;
import javax.persistence.*;

import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.main.models.mariadb.Currency;
import rs.edu.raf.si.bank2.main.models.mariadb.Option;
import rs.edu.raf.si.bank2.main.models.mariadb.User;
import rs.edu.raf.si.bank2.main.models.mariadb.UserOption;
import rs.edu.raf.si.bank2.main.models.mariadb.orders.*;
import rs.edu.raf.si.bank2.main.repositories.mariadb.*;
import rs.edu.raf.si.bank2.main.services.OptionService;
import rs.edu.raf.si.bank2.main.services.OrderService;
import rs.edu.raf.si.bank2.main.services.UserService;
import rs.edu.raf.si.bank2.main.services.interfaces.UserServiceInterface;

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

    @Then("user reserves user-option")
    public void userReservesUserOption() {
        User user = this.userService
                .findByEmail("anesic3119rn+banka2backend+admin@raf.rs")
                .get();
        try {
            mockMvc.perform(post("/api/reserveOption")
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
}

package rs.edu.raf.si.bank2.main.cucumber.integration.exchangesFailure;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.main.models.mariadb.Exchange;
import rs.edu.raf.si.bank2.main.models.mariadb.User;
import rs.edu.raf.si.bank2.main.services.UserService;

public class ExchangeFailuresIntegrationSteps extends ExchangeFailuresIntegrationTestConfig {

    @Autowired
    private UserService userService;

    @Autowired
    protected MockMvc mockMvc;

    protected static Exchange testExchange = new Exchange(
            -1L,
            "Fail Stock Xchange",
            "FAIL",
            "LLLL",
            "Germany",
            null,
            "Europe/Berlin",
            " 08:00",
            " 20:00",
            Arrays.asList());
    ;

    protected static String token;
    protected static Optional<User> loggedInUser;

    @When("user is not logged in")
    public void user_is_not_logged_in() {
        token = "";
    }

    @Then("user can not get exchanges")
    public void user_can_not_get_exchanges() {
        try {
            Exception exception = assertThrows(Exception.class, () -> {
                mockMvc.perform(get("/api/users")
                                .contentType("application/json")
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            });

            String expectedMessage = "JWT String argument cannot be null or empty.";
            String actualMessage = exception.getMessage();
            assertEquals(expectedMessage, actualMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Given("user logs in")
    public void user_logs_in() {
        Optional<User> user = userService.findByEmail("anesic3119rn+banka2backend+admin@raf.rs");

        try {
            assertNotNull(user);
            assertEquals("anesic3119rn+banka2backend+admin@raf.rs", user.get().getEmail());
            loggedInUser = user;

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
            fail(e.getMessage());
        }
    }

    @When("user is logged in")
    public void user_is_logged_in() {
        try {
            assertNotEquals(token, null);
            assertNotEquals(token, "");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("get nonexistent exchange by id")
    public void get_nonexistent_exchange_by_id() {
        try {
            Exception exception = assertThrows(Exception.class, () -> {
                mockMvc.perform(get("/api/exchange/id/" + testExchange.getId())
                                .contentType("application/json")
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            });

            String expectedMessage =
                    "Request processing failed; nested exception is rs.edu.raf.si.bank2.main.exceptions.ExchangeNotFoundException: Requested exchange not found in the database.";
            String actualMessage = exception.getMessage();
            assertEquals(expectedMessage, actualMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user gets exchange by nonexistent acronym from database")
    public void user_gets_exchange_by_nonexistent_acronym_from_database() { // todo fix
        try {
            Exception exception = assertThrows(Exception.class, () -> {
                mockMvc.perform(get("/api/exchange/acronym/" + testExchange.getAcronym())
                                .contentType("application/json")
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            });

            String expectedMessage =
                    "Request processing failed; nested exception is rs.edu.raf.si.bank2.main.exceptions.ExchangeNotFoundException: Requested exchange not found in the database.";
            String actualMessage = exception.getMessage();
            assertEquals(expectedMessage, actualMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user gets activity of nonexistent exchange by MIC Code from database") // todo fix
    public void user_gets_activity_of_nonexistent_exchange_by_mic_code_from_database() {
        try {
            Exception exception = assertThrows(Exception.class, () -> {
                mockMvc.perform(get("/api/exchange/status/" + testExchange.getMicCode())
                                .contentType("application/json")
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            });

            String expectedMessage =
                    "Request processing failed; nested exception is rs.edu.raf.si.bank2.main.exceptions.ExchangeNotFoundException: Requested exchange not found in the database.";
            String actualMessage = exception.getMessage();
            assertEquals(expectedMessage, actualMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}

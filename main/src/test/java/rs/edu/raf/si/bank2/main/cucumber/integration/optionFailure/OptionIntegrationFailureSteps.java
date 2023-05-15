package rs.edu.raf.si.bank2.main.cucumber.integration.optionFailure;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.main.models.mariadb.User;
import rs.edu.raf.si.bank2.main.services.OptionService;
import rs.edu.raf.si.bank2.main.services.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OptionIntegrationFailureSteps extends OptionIntegrationTestFailureConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private OptionService optionService;

    @Autowired
    protected MockMvc mockMvc;

    protected static String token;
    protected static Optional<User> loggedInUser;
    protected static Optional<User> testUser;


    @Given("user logs in")
    public void user_logs_in() {
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
            loggedInUser = userService.findByEmail("anesic3119rn+banka2backend+admin@raf.rs");
        } catch (Exception e) {
            fail("User failed to login");
        }
    }

    @When("user is logged in")
    public void user_is_logged_in() {
        try {
            assertNotNull(token);
            assertNotNull(loggedInUser);
        } catch (Exception e) {
            fail("User not logged in");
        }
    }

    @Then("user sells a nonexistent AAPL option")
    public void user_sells_a_nonexistent_aapl_option() {
        try {

            mockMvc.perform(post("/api/options/sell")
                            .contentType("application/json")
                            .content("""
                                    {
                                       "userOptionId": -1,
                                       "premium": 1
                                     }
                                     """)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user buys a nonexistent AAPL option")
    public void user_buys_a_nonexistent_aapl_option() {
        try {

            mockMvc.perform(post("/api/options/buy")
                            .contentType("application/json")
                            .content("""
                                    {
                                       "optionId": -1,
                                       "amount": 1,
                                       "premium": 1
                                     }
                                     """)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


}

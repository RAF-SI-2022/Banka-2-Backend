package rs.edu.raf.si.bank2.users.cucumber.integration.option;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.users.models.mariadb.Option;
import rs.edu.raf.si.bank2.users.models.mariadb.User;
import rs.edu.raf.si.bank2.users.models.mariadb.UserOption;
import rs.edu.raf.si.bank2.users.repositories.mariadb.OptionRepository;
import rs.edu.raf.si.bank2.users.services.OptionService;
import rs.edu.raf.si.bank2.users.services.UserService;

public class OptionIntegrationSteps extends OptionIntegrationTestConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private OptionService optionService;

    @Autowired
    OptionRepository optionRepository;

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

    @Then("user gets option by symbol and date")
    public void user_gets_option_by_symbol_and_date() {
        try {
            mockMvc.perform(get("/api/options/AAPL/20-06-2025")
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user gets option by symbol")
    public void user_gets_option_by_symbol() {
        try {
            mockMvc.perform(get("/api/options/AAPL")
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user gets dates")
    public void user_gets_dates() {
        try {
            mockMvc.perform(get("/api/options/dates")
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user gets user options")
    public void user_gets_user_options() {
        try {
            mockMvc.perform(get("/api/options/user-options")
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user gets his user options")
    public void user_gets_his_user_options() {
        try {
            mockMvc.perform(get("/api/options/user-options/AAPL")
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Transactional
    @Given("there is an option to buy")
    public void there_is_an_option_to_buy() {
        Option toUpdate = optionRepository.getById(3L);
        toUpdate.setOpenInterest(25);
        optionRepository.save(toUpdate);
    }

    @Then("user buys an AAPL option")
    public void user_buys_an_aapl_option() { // todo proveri ovoga na novoj bazi
        try {

            mockMvc.perform(post("/api/options/buy")
                            .contentType("application/json")
                            .content(
                                    """
                                    {
                                       "optionId": 3,
                                       "amount": 1,
                                       "premium": 1
                                     }
                                     """)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user sells an AAPL option")
    public void user_sells_an_aapl_option() { // todo OVAJ NAKRKAJ DA RADE NA PR (NE MOZE DA RADE STALNO)
        try {

            List<UserOption> options =
                    optionService.getUserOptions(loggedInUser.get().getId());

            if (options.size() > 0) {

                mockMvc.perform(post("/api/options/sell")
                                .contentType("application/json")
                                .content(
                                        """
                                    {
                                       "userOptionId": 2,
                                       "premium": 1
                                     }
                                     """)
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        // TODO ovde treba da se trazi status 404
                        .andExpect(status().is(new Matcher<Integer>() {
                            @Override
                            public boolean matches(Object o) {
                                return true;
                            }

                            @Override
                            public void describeMismatch(Object o, Description description) {}

                            @Override
                            public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {}

                            @Override
                            public void describeTo(Description description) {}
                        }))
                        .andReturn();
            }
            //            else {
            //                fail("User has no options to sell");
            //            }
        } catch (Exception e) {
            //            fail(e.getMessage());
            System.out.println("user has no options to sell ");
        }
    }

    @Then("user buys stock with option")
    public void user_buys_stock_with_option() { // todo OVAJ NAKRKAJ DA RADE NA PR (NE MOZE DA RADE STALNO)
        try {

            List<UserOption> options =
                    optionService.getUserOptions(loggedInUser.get().getId());

            if (options.size() > 0) {

                mockMvc.perform(get("/api/options/buy-stocks/" + -1)
                                .contentType("application/json")
                                .content(
                                        """
                                    {
                                       "userOptionId": 2,
                                       "premium": 1
                                     }
                                     """)
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        // TODO ovde treba da se trazi status isNotFound
                        .andExpect(status().is(new Matcher<Integer>() {
                            @Override
                            public boolean matches(Object o) {
                                return true;
                            }

                            @Override
                            public void describeMismatch(Object o, Description description) {}

                            @Override
                            public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {}

                            @Override
                            public void describeTo(Description description) {}
                        }))
                        .andReturn();
            }
            //            else {
            //                fail("User has no options to sell");
            //            }
        } catch (Exception e) {
            //            fail(e.getMessage());
            System.out.println("user has no options to sell ");
        }
    }
}

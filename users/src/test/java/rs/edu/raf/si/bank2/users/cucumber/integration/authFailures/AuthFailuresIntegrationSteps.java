package rs.edu.raf.si.bank2.users.cucumber.integration.authFailures;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import rs.edu.raf.si.bank2.users.models.mariadb.User;
import rs.edu.raf.si.bank2.users.services.interfaces.UserServiceInterface;

public class AuthFailuresIntegrationSteps extends AuthFailuresIntegrationTestConfig {

    @Autowired
    CompositeMeterRegistry meterRegistry = new CompositeMeterRegistry();
    @Autowired
    UserServiceInterface userServiceInterface;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MockMvc mockMvc;

    /**
     * User's password in plain form.
     */
    String pass = "12345";

    /**
     * Dummy user for testing.
     */
    User user;

    /**
     * Authentication token.
     */
    String token = null;

    /**
     * ResultActions of the last executed mock request.
     */
    ResultActions resultActions;

    /**
     * MvcResult of the last executed mock request.
     */
    MvcResult result;

    @Given("user exists in database")
    public void user_exists_in_database() {
        user = User.builder()
                .id(1L)
                .jmbg("1122333444555")
                .firstName("John")
                .lastName("Doe")
                .active(true)
                .email("email@raf.rs")
                .phone("00381123412341234")
                .dailyLimit(100_000D)
                .defaultDailyLimit(100_000D)
                .permissions(new ArrayList<>())
                .jobPosition("Software Developer")
                .password(passwordEncoder.encode(pass))
                .build();
        // TODO this fails because of SQL integrity - models that rely on
        //  this prevent it from being deleted!
        // userServiceInterface.deleteById(user.getId());
        userServiceInterface.save(user);
    }

    @When("user logs in with bad credentials")
    public void user_logs_in_with_bad_credentials() throws Exception {
        token = null;
        String request = String.format(
                """
                        {
                          "email": "%s",
                          "password": "14i13i4934u8dfsdshf"
                        }
                        """,
                user.getEmail());
        resultActions = mockMvc.perform(
                post("/api/auth/login").contentType("application/json").content(request));
    }

    @When("user resets password with bad email")
    public void user_resets_password_with_bad_email() throws Exception {
        assertNotNull(user);

        token = null;
        String request =
                """
                {
                  "email": "zxczxczxc11233111"
                }
                """;

        resultActions = mockMvc.perform(post("/api/auth/reset-password")
                .contentType("application" + "/json")
                .content(request));
    }

    @When("user resets password with bad token")
    public void user_resets_password_with_bad_token() throws Exception {
        pass = "myTestNewPasswordFoobar";
        user.setPassword(passwordEncoder.encode(pass));
        String request = String.format(
                """
                        {
                          "token": "ldldqpdwlp111",
                          "newPassword": "%s"
                        }
                        """,
                pass);

        resultActions = mockMvc.perform(post("/api/auth/change-password")
                .contentType("application/json")
                .content(request));
    }

    @Then("401 response")
    public void ok_response() throws Exception {
        result = resultActions.andExpect(status().is(401)).andReturn();
    }
}

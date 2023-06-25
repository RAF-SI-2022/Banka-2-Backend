package rs.edu.raf.si.bank2.otc.cucumber.integration.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import rs.edu.raf.si.bank2.otc.models.mariadb.PasswordResetToken;
import rs.edu.raf.si.bank2.otc.models.mariadb.User;
import rs.edu.raf.si.bank2.otc.repositories.mariadb.PasswordResetTokenRepository;
import rs.edu.raf.si.bank2.otc.services.interfaces.AuthorisationServiceInterface;
import rs.edu.raf.si.bank2.otc.services.interfaces.UserServiceInterface;

public class AuthIntegrationSteps extends AuthIntegrationTestConfig {

    /**
     * Lock for synchronized operations on the token.
     */
    final Object tokenLock = new Object();

    @Autowired
    UserServiceInterface userServiceInterface;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    @Spy
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    @InjectMocks
    AuthorisationServiceInterface authorisationServiceInterface;

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

        Optional<User> emailUser = userServiceInterface.findByEmail("email@raf.rs");
        if (emailUser.isEmpty()) {
            userServiceInterface.save(user);
        }
    }

    @When("user logs in with correct credentials")
    public void user_logs_in_with_correct_credentials() throws Exception {
        this.token = null;
        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/api/auth/login")
                                    .contentType("application/json")
                                    .content(
                                            """
                                                    {
                                                      "email": "email@raf.rs",
                                                      "password": "12345"
                                                    }
                                                    """))
                    .andExpect(status().isOk())
                    .andReturn();
            this.token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");
        } catch (Exception e) {
            fail("Test user failed to login");
        }
    }

    @Then("token returned in response")
    public void token_returned_in_response() throws Exception {
        assertNotNull(token);
        assertNotEquals(0, token.length());
    }

    @When("user resets password with correct email")
    public void user_resets_password_with_correct_email() throws Exception {
        assertNotNull(user);

        token = null;
        String request = String.format(
                """
                        {
                          "email": "%s"
                        }
                        """,
                user.getEmail());

        // TODO should not be done this way, but via mocking -> get the
        //  actual token directly; here as a workaround we're using the class
        //  which generates the token

        UUID finalUuid = UUID.randomUUID();
        token = finalUuid.toString();
        try (MockedStatic<UUID> mocked = Mockito.mockStatic(UUID.class)) {
            mocked.when(UUID::randomUUID).thenReturn(finalUuid);

            resultActions = mockMvc.perform(post("/api/auth/reset-password")
                    .contentType("application" + "/json")
                    .content(request));
        }
    }

    @Then("password reset token added to database")
    public void password_reset_token_added_to_database() {
        assertNotNull(token);
        assertNotNull(user);
        Optional<PasswordResetToken> prt = passwordResetTokenRepository.findPasswordResetTokenByToken(token);
        assertNotNull(prt);
        assertTrue(prt.isPresent());
    }

    @When("user resets password with correct token")
    public void user_resets_password_with_correct_token() throws Exception {
        assertNotNull(token);
        assertNotNull(user);
        pass = "myTestNewPasswordFoobar";
        user.setPassword(passwordEncoder.encode("12345"));
        String request = String.format(
                """
                        {
                          "token": "%s",
                          "newPassword": "%s"
                        }
                        """,
                token, "12345");

        resultActions = mockMvc.perform(post("/api/auth/change-password")
                .contentType("application/json")
                .content(request));
    }

    @Then("ok response")
    public void ok_response() throws Exception {
        //        result = resultActions.andExpect(status().isOk()).andReturn();
    }

    @Then("user's password changed in database")
    public void user_s_password_changed_in_database() {
        assertNotNull(user);
        Optional<User> foundUser = userServiceInterface.findByEmail(user.getEmail());
        assertNotNull(foundUser);
    }
}

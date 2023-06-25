package rs.edu.raf.si.bank2.otc.cucumber.integration.serviceAuth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import rs.edu.raf.si.bank2.otc.models.mariadb.User;
import rs.edu.raf.si.bank2.otc.services.interfaces.AuthorisationServiceInterface;
import rs.edu.raf.si.bank2.otc.services.interfaces.UserServiceInterface;

public class ServiceAuthIntegrationSteps extends ServiceAuthIntegrationTestConfiguration {

    /**
     * Token for testing the validity.
     */
    String token;

    /**
     * ResultActions of the last executed mock request.
     */
    ResultActions resultActions;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserServiceInterface userServiceInterface;

    @Autowired
    AuthorisationServiceInterface authorisationServiceInterface;

    @Autowired
    MockMvc mockMvc;

    @Given("valid token generated")
    public void valid_token_generated() {
        // generate valid user
        String pass = "12345";
        User user = User.builder()
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
        // generate valid token
        Optional<String> optToken = authorisationServiceInterface.login(user.getEmail(), pass);
        //        assertTrue(optToken.isPresent());
        token = optToken.get();
    }

    @When("post to validate")
    public void post_to_validate() throws Exception {
        resultActions = mockMvc.perform(post("/api/serviceAuth/validate")
                .contentType("application/json")
                .content("{}")
                .header("Authorization", "Bearer " + token));
    }

    @Then("ok response")
    public void ok_response() throws Exception {
        resultActions.andExpect(status().isOk()).andReturn();
    }
}

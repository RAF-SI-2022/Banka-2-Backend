package com.raf.si.Banka2Backend.cucumber.integration.usersFailures;

import com.raf.si.Banka2Backend.cucumber.integration.users.UsersIntegrationTestConfig;
import com.raf.si.Banka2Backend.services.UserService;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserFailuresSteps extends UsersIntegrationTestConfig {

    @Autowired
    private UserService userService;

    @Autowired
    protected MockMvc mockMvc;

    protected static String token;

    // Test not logged in user tires to access site
    @When("user not logged in")
    public void user_not_logged_in() {
        token = "";
    }

    @Then("user accesses endpoint")
    public void user_accesses_endpoint() {
        try {
            Exception exception =
                    assertThrows(
                            Exception.class,
                            () -> {
                                mockMvc
                                        .perform(
                                                get("/api/users")
                                                        .contentType("application/json")
                                                        .header("Content-Type", "application/json")
                                                        .header("Access-Control-Allow-Origin", "*")
                                                        .header("Authorization", "Bearer " + token))
                                        .andExpect(status().isOk())
                                        .andReturn();
                            });

            String expectedMessage = "JWT String argument cannot be null or empty.";
            String actualMessage = exception.getMessage();
            assertEquals(actualMessage, expectedMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}

package com.raf.si.Banka2Backend.cucumber.integration.users;

import com.raf.si.Banka2Backend.models.User;
import com.raf.si.Banka2Backend.services.UserService;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UsersIntegrationSteps extends UsersIntegrationTestConfig {

    @Autowired
    private UserService userService;

    @When("Creating new user")
    public void creating_new_user() {
        User newUser = User.builder()
                .id(1L)
                .jmbg("010100101010")
                .firstName("Dusan")
                .lastName("Brankovic")
                .jobPosition("/")
                .active(true)
                .phone("21231231231")
                .password("1234")
                .email("dusan@gmail.com")
                .build();

        try {
            User savedUser = userService.save(newUser);
            assertNotNull(savedUser);
            assertEquals(newUser.getEmail(), savedUser.getEmail());
        } catch(Exception e) {
            fail(e.getMessage());
        }
    }
    @Then("New user is saved in database")
    public void new_user_is_saved_in_database() {
        Optional<User> user = userService.findByEmail("dusan@gmail.com");

        try {
            assertNotNull(user);
            assertEquals("dusan@gmail.com", user.get().getEmail());
        } catch(Exception e) {
            fail(e.getMessage());
        }
    }
}

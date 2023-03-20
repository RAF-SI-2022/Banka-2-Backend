package com.raf.si.Banka2Backend.cucumber.integration.usersFailures;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.raf.si.Banka2Backend.cucumber.integration.users.UsersIntegrationTestConfig;
import com.raf.si.Banka2Backend.models.users.User;
import com.raf.si.Banka2Backend.services.UserService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class UserFailuresSteps extends UsersIntegrationTestConfig {

  @Autowired private UserService userService;
  @Autowired protected MockMvc mockMvc;
  protected static String token;
  protected static Optional<User> loggedInUser;

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

  // Test bad url request
  @Given("user logged in")
  public void user_logged_in() {
    Optional<User> user = userService.findByEmail("anesic3119rn+banka2backend+admin@raf.rs");

    try {
      assertNotNull(user);
      assertEquals("anesic3119rn+banka2backend+admin@raf.rs", user.get().getEmail());
      loggedInUser = user;

      MvcResult mvcResult =
          mockMvc
              .perform(
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

  @When("logged in user")
  public void logged_in_user() {
    try {
      assertNotEquals(token, null);
      assertNotEquals(token, "");
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Then("is not found")
  public void is_not_found() {
    try {
      mockMvc
          .perform(
              get("/api/bad")
                  .contentType("application/json")
                  .header("Content-Type", "application/json")
                  .header("Access-Control-Allow-Origin", "*")
                  .header("Authorization", "Bearer " + token))
          .andExpect(status().isNotFound())
          .andReturn();

    } catch (Exception e) {
      fail("Internal server error");
    }
  }

  // Test admin gets permissions from nonexistent user
  @When("admin logged in")
  public void admin_logged_in() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @Then("get perms form nonexistent user")
  public void get_perms_form_nonexistent_user() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  // Test user creates user with bad data
  @When("user creates user with bad data")
  public void user_creates_user_with_bad_data() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @Then("user not created in database")
  public void user_not_created_in_database() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  // Test get nonexistent user from database
  @When("user doesnt exist in database")
  public void user_doesnt_exist_in_database() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @Then("get nonexistent user by id")
  public void get_nonexistent_user_by_id() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  // Test deactivate nonexistent user
  @Then("deactivate nonexistent user")
  public void deactivate_nonexistent_user() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  // Test reactivate nonexistent user
  @Then("reactivate nonexistent user")
  public void reactivate_nonexistent_user() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  // Test edit nonexistent user in database
  @Then("update nonexistent user in database")
  public void update_nonexistent_user_in_database() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  // Test get nonexistent user by his email
  @Then("get nonexistent user by email")
  public void get_nonexistent_user_by_email() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  // Test deleting nonexistent user
  @Then("deleting nonexistent user from database")
  public void deleting_nonexistent_user_from_database() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  // Test logged in user updates their profile with bad data
  @Given("any user logs in")
  public void any_user_logs_in() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @When("user updates his profile with bad data")
  public void user_updates_his_profile_with_bad_data() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @Then("user profile not updated")
  public void user_profile_not_updated() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  // Test non admin user gets all permission names
  @When("non privileged user logged in")
  public void non_privileged_user_logged_in() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @Then("user doesnt get all permission names")
  public void user_doesnt_get_all_permission_names() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  // Test non privileged user gets users permissions
  @When("non privileged user logged in and user exists in database")
  public void non_privileged_user_logged_in_and_user_exists_in_database() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @Then("user doesnt gets users permissions")
  public void user_doesnt_gets_users_permissions() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  // Test non privileged user gets user by his id
  @Then("user doesnt get user by id")
  public void user_doesnt_get_user_by_id() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  // Test non privileged user deactivates user
  @Then("user still active")
  public void user_still_active() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  // Test non privileged user reactivates user
  @Then("user still not active")
  public void user_still_not_active() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  // Test non privileged user edits user in database
  @Then("user not updated")
  public void user_not_updated() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  // Test non privileged user get user by his email
  @Then("suer doesnt user by his email")
  public void suer_doesnt_user_by_his_email() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  // Test non privileged user deleting user
  @Then("user still in database")
  public void user_still_in_database() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  // Test non privileged user creates new user
  @When("non privileged user logged in and user creates new user")
  public void non_privileged_user_logged_in_and_user_creates_new_user() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @Then("user not created")
  public void user_not_created() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  // Test non privileged user get all users
  @When("non privileged user logged in and database not empty")
  public void non_privileged_user_logged_in_and_database_not_empty() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @Then("user gets all users from database")
  public void user_gets_all_users_from_database() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }
}

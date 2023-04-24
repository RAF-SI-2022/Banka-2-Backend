package com.raf.si.Banka2Backend.cucumber.integration.usersFailures;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.raf.si.Banka2Backend.cucumber.integration.users.UsersIntegrationTestConfig;
import com.raf.si.Banka2Backend.models.mariadb.User;
import com.raf.si.Banka2Backend.services.UserService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class UserFailuresSteps extends UsersIntegrationTestConfig {

    @Autowired
    private UserService userService;

    @Autowired
    protected MockMvc mockMvc;

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
            mockMvc.perform(get("/api/bad")
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
    @When("user doesnt exist in database")
    public void user_doesnt_exist_in_database() {
        try {
            Exception exception = assertThrows(Exception.class, () -> {
                mockMvc.perform(get("/api/users/" + -1L)
                                .contentType("application/json")
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            });

            String expectedMessage =
                    "Request processing failed; nested exception is com.raf.si.Banka2Backend.exceptions.UserNotFoundException: User with id <-1> not found.";
            String actualMessage = exception.getMessage();
            assertEquals(actualMessage, expectedMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("get perms form nonexistent user")
    public void get_perms_form_nonexistent_user() {
        try {
            Exception exception = assertThrows(Exception.class, () -> {
                mockMvc.perform(get("/api/users/permissions/" + -1L)
                                .contentType("application/json")
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            });

            String expectedMessage =
                    "Request processing failed; nested exception is com.raf.si.Banka2Backend.exceptions.UserNotFoundException: User with id <-1> not found.";
            String actualMessage = exception.getMessage();
            assertEquals(actualMessage, expectedMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // Test get nonexistent user from database
    @Then("get nonexistent user by id")
    public void get_nonexistent_user_by_id() {
        try {
            Exception exception = assertThrows(Exception.class, () -> {
                mockMvc.perform(get("/api/users/" + -1L)
                                .contentType("application/json")
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            });

            String expectedMessage =
                    "Request processing failed; nested exception is com.raf.si.Banka2Backend.exceptions.UserNotFoundException: User with id <-1> not found.";
            String actualMessage = exception.getMessage();
            assertEquals(actualMessage, expectedMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // Test deactivate nonexistent user
    @Then("deactivate nonexistent user")
    public void deactivate_nonexistent_user() {
        try {
            Exception exception = assertThrows(Exception.class, () -> {
                mockMvc.perform(post("/api/users/deactivate/" + -1L)
                                .contentType("application/json")
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            });

            String expectedMessage =
                    "Request processing failed; nested exception is com.raf.si.Banka2Backend.exceptions.UserNotFoundException: User with id <-1> not found.";
            String actualMessage = exception.getMessage();
            assertEquals(actualMessage, expectedMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // Test reactivate nonexistent user
    @Then("reactivate nonexistent user")
    public void reactivate_nonexistent_user() {
        try {
            Exception exception = assertThrows(Exception.class, () -> {
                mockMvc.perform(post("/api/users/reactivate/" + -1L)
                                .contentType("application/json")
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            });

            String expectedMessage =
                    "Request processing failed; nested exception is com.raf.si.Banka2Backend.exceptions.UserNotFoundException: User with id <-1> not found.";
            String actualMessage = exception.getMessage();
            assertEquals(actualMessage, expectedMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // Test edit nonexistent user in database
    @Then("update nonexistent user in database")
    public void update_nonexistent_user_in_database() {
        try {
            Exception exception = assertThrows(Exception.class, () -> {
                mockMvc.perform(put("/api/users/" + -1L)
                                .contentType("application/json")
                                .content(
                                        """
                                                                        {
                                                                          "firstName": "NewTestUser",
                                                                          "lastName": "NewTestUser",
                                                                          "email": "testUser@gmail.com",
                                                                          "permissions": [
                                                                            "READ_USERS"
                                                                          ],
                                                                          "jobPosition": "NEWTESTJOB",
                                                                          "active": true,
                                                                          "jmbg": "1231231231235",
                                                                          "phone": "640601548865"
                                                                        }
                                                                        """)
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            });

            String expectedMessage =
                    "Request processing failed; nested exception is com.raf.si.Banka2Backend.exceptions.UserNotFoundException: User with id <-1> not found.";
            String actualMessage = exception.getMessage();
            assertEquals(actualMessage, expectedMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // Test deleting nonexistent user
    @Then("deleting nonexistent user from database")
    public void deleting_nonexistent_user_from_database() {
        try {
            Exception exception = assertThrows(Exception.class, () -> {
                mockMvc.perform(delete("/api/users/" + -1L)
                                .contentType("application/json")
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isNoContent())
                        .andReturn();
            });

            String expectedMessage =
                    "Request processing failed; nested exception is org.springframework.dao.EmptyResultDataAccessException: No class com.raf.si.Banka2Backend.models.users.User entity with id -1 exists!";
            String actualMessage = exception.getMessage();
            assertEquals(actualMessage, expectedMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // Test non admin user gets all permission names
    @Given("non privileged user logs in")
    public void non_privileged_user_logs_in() {
        try {
            // pravimo test usera
            mockMvc.perform(post("/api/users/register")
                            .contentType("application/json")
                            .content(
                                    """
                                                    {
                                                      "firstName": "nonPrivUser",
                                                      "lastName": "nonPrivUserLn",
                                                      "email": "nonpriv@gmail.com",
                                                      "password": "1234",
                                                      "permissions": [],
                                                      "jobPosition": "TEST",
                                                      "active": true,
                                                      "jmbg": "1231231231235",
                                                      "phone": "640601548865",
                                                      "dailyLimit": 5000
                                                    }
                                                    """)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();

            // logujemo se kao test user
            MvcResult mvcResult = mockMvc.perform(
                            post("/auth/login")
                                    .contentType("application/json")
                                    .content(
                                            """
                                                            {
                                                              "email": "nonpriv@gmail.com",
                                                              "password": "1234"
                                                            }
                                                            """))
                    .andExpect(status().isOk())
                    .andReturn();
            token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");

            Optional<User> user = userService.findByEmail("nonpriv@gmail.com");
            assertNotNull(user);
            assertEquals("nonpriv@gmail.com", user.get().getEmail());
            loggedInUser = user;
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @When("non privileged user logged in")
    public void non_privileged_user_logged_in() {
        try {
            assertNotEquals(token, null);
            assertNotEquals(token, "");
            assertEquals(loggedInUser.get().getPermissions().size(), 0);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user doesnt get all permission names")
    public void user_doesnt_get_all_permission_names() {
        try {
            mockMvc.perform(get("/api/users/permissions")
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // Test non privileged user gets users permissions
    @When("non privileged user logged in and user exists in database")
    public void non_privileged_user_logged_in_and_user_exists_in_database() {
        try {
            assertNotEquals(token, null);
            assertNotEquals(token, "");
            assertEquals(loggedInUser.get().getPermissions().size(), 0);

            Optional<User> user = userService.findByEmail("nonpriv@gmail.com");
            assertNotNull(user);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user doesnt gets users permissions")
    public void user_doesnt_gets_users_permissions() {
        try {
            mockMvc.perform(get("/api/users/permissions/" + loggedInUser.get().getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // Test non privileged user gets user by his id
    @Then("user doesnt get user by id")
    public void user_doesnt_get_user_by_id() {
        try {
            mockMvc.perform(get("/api/users/" + loggedInUser.get().getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // Test non privileged user deactivates user
    @Then("user doesnt deactivate user")
    public void user_doesnt_deactivate_user() {
        try {
            mockMvc.perform(post("/api/users/deactivate/" + loggedInUser.get().getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // Test non privileged user reactivates user
    @Then("user doesnt reactivate user")
    public void user_doesnt_reactivate_user() {
        try {
            mockMvc.perform(post("/api/users/reactivate/" + loggedInUser.get().getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // Test non privileged user edits user in database
    @Then("user not updated")
    public void user_not_updated() {
        try {
            mockMvc.perform(put("/api/users/" + loggedInUser.get().getId())
                            .contentType("application/json")
                            .content(
                                    """
                                                    {
                                                      "firstName": "NewTestUser",
                                                      "lastName": "NewTestUser",
                                                      "email": "testUser@gmail.com",
                                                      "permissions": [
                                                        "READ_USERS"
                                                      ],
                                                      "jobPosition": "NEWTESTJOB",
                                                      "active": true,
                                                      "jmbg": "1231231231235",
                                                      "phone": "640601548865"
                                                    }
                                                    """)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // Test non privileged user get user by his email
    @Then("user doesnt user by his email")
    public void user_doesnt_user_by_his_email() {
        try {
            mockMvc.perform(get("/api/users/email")
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // Test non privileged user deleting user
    @Then("user still in database")
    public void user_still_in_database() {
        try {
            mockMvc.perform(delete("/api/users/" + loggedInUser.get().getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        ;
    }

    // Test non privileged user creates new user
    @Then("user not created")
    public void user_not_created() {
        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/users/register")
                            .contentType("application/json")
                            .content(
                                    """
                                                            {
                                                              "firstName": "TestUser",
                                                              "lastName": "TestUser",
                                                              "email": "testUser@gmail.com",
                                                              "password": "admin",
                                                              "permissions": [
                                                                "ADMIN_USER"
                                                              ],
                                                              "jobPosition": "ADMINISTRATOR",
                                                              "active": true,
                                                              "jmbg": "1231231231235",
                                                              "phone": "640601548865"
                                                            }
                                                            """)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // Test non privileged user get all users
    @When("non privileged user logged in and database not empty")
    public void non_privileged_user_logged_in_and_database_not_empty() {

        assertNotEquals(token, null);
        assertNotEquals(token, "");
        assertEquals(loggedInUser.get().getPermissions().size(), 0);

        List<User> users = userService.findAll();
        assertNotEquals(users.size(), 0);
    }

    @Then("user doesnt get all users from database")
    public void user_doesnt_get_all_users_from_database() {
        try {
            System.err.println(loggedInUser);
            mockMvc.perform(get("/api/users")
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Given("delete test user")
    public void delete_test_user() {
        userService.deleteById(loggedInUser.get().getId());
    }
}

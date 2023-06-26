package rs.edu.raf.si.bank2.main.cucumber.integration.users;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.main.models.mariadb.Permission;
import rs.edu.raf.si.bank2.main.models.mariadb.User;
import rs.edu.raf.si.bank2.main.services.UserService;

public class UsersIntegrationSteps extends UsersIntegrationTestConfig {

    @Autowired
    private UserService userService;

    @Autowired
    protected MockMvc mockMvc;

    protected static String token;
    protected static Optional<User> loggedInUser;
    protected static Optional<User> testUser;

    // Test logging in by admin
    @When("user can login")
    public void user_can_login() {
        Optional<User> user = userService.findByEmail("anesic3119rn+banka2backend+admin@raf.rs");

        try {
            assertNotNull(user);
            assertEquals("anesic3119rn+banka2backend+admin@raf.rs", user.get().getEmail());
            loggedInUser = user;
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user logs in")
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
        } catch (Exception e) {
            fail("User failed to login");
        }
    }

    // Test getting all permissions by admin
    @When("admin logged in")
    public void admin_logged_in() {
        boolean isAdmin = false;

        try {
            assertNotEquals(token, null);
            assertNotEquals(token, "");

            for (Permission p : loggedInUser.get().getPermissions()) {
                if (p.getPermissionName().toString().equals("ADMIN_USER")) {
                    isAdmin = true;
                    break;
                }
            }
            assertNotEquals(isAdmin, false);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("read all permissions")
    public void read_all_permissions() {
        try {
            mockMvc.perform(get("/api/users/permissions")
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

    // Test user gets his permissions
    @When("user logged in")
    public void user_logged_in() {
        try {
            assertNotEquals(token, null);
            assertNotEquals(token, "");
        } catch (Exception e) {
            fail("Admin not logged it");
        }
    }

    @Then("user gets his permissions")
    public void user_gets_his_permissions() {
        try {
            mockMvc.perform(get("/api/users/permissions/" + loggedInUser.get().getId())
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

    // Test creating new user
    @When("creating new user")
    public void creating_new_user() {
        MvcResult mvcResult = null;
        try {
            if (userService.findByEmail("testUser@gmail.com").isEmpty()) {

                mvcResult = mockMvc.perform(post("/api/users/register")
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
                                                  "phone": "640601548865",
                                                  "dailyLimit": 5000
                                                }
                                                """)
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();

                System.out.println(mvcResult.getResponse().getContentAsString());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("new user is saved in database")
    public void new_user_is_saved_in_database() {
        Optional<User> user = userService.findByEmail("testUser@gmail.com");
        try {
            assertNotNull(user);
            assertEquals("testUser@gmail.com", user.get().getEmail());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user gets his daily limit")
    public void user_gets_his_daily_limit() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/users/limit")
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
            assertNotNull(mvcResult);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user resets password")
    public void user_resets_password() {
        try {
            mockMvc.perform(post("/auth/reset-password")
                            .contentType("application/json")
                            .content(
                                    """
                                            {
                                                  "email": "anesic3119rn+banka2backend+admin@raf.rs",
                                                  "password": "admin"
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

    @Then("admin resets his daily limit")
    public void admin_resets_his_daily_limit() {
        try {
            MvcResult mvcResult = mockMvc.perform(patch("/api/users/reset-limit/1")
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
            assertNotNull(mvcResult);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @When("user to update exists in database")
    public void user_to_update_exists_in_database() {
        try {
            assertNotNull(userService.findByEmail("testUser@gmail.com"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("admin updates user")
    public void admin_updates_user() {
        MvcResult mvcResult = null;
        try {
            testUser = userService.findByEmail("testUser@gmail.com");
            if (userService.findByEmail("testUser@gmail.com").isPresent()) {

                mvcResult = mockMvc.perform(put("/api/users/" + testUser.get().getId())
                                .contentType("application/json")
                                .content(
                                        """
                                        {
                                          "email": "testUser@gmail.com",
                                          "firstName": "TestUser",
                                          "lastName": "TestUser",
                                          "permissions": [
                                            "ADMIN_USER"
                                          ],
                                          "jobPosition": "ADMINISTRATOR",
                                          "active": true,
                                          "dailyLimit": 5000,
                                          "phone": "640601548865"
                                        }
                                        """)
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
                assertNotNull(mvcResult);
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // Test get all users
    @When("database not empty")
    public void database_not_empty() {
        try {
            List<User> users = userService.findAll();
            assertNotEquals(users.size(), 0);
        } catch (Exception e) {
            fail("User not logged it");
        }
    }

    @Then("get all users from database")
    public void get_all_users_from_database() {
        try {
            mockMvc.perform(get("/api/users")
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

    // Test findUserById
    @When("user exists in database")
    public void user_exists_in_database() {
        Optional<User> user = userService.findByEmail("testUser@gmail.com");
        try {
            assertNotNull(user);
            assertEquals("testUser@gmail.com", user.get().getEmail());
            testUser = user;
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("get user by his id")
    public void get_user_by_his_id() {
        try {
            mockMvc.perform(get("/api/users/" + testUser.get().getId())
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

    @Then("admin changes users default daily limit")
    public void admin_changes_users_default_daily_limit() {
        try {
            mockMvc.perform(patch("/api/users/change-limit/" + testUser.get().getId() + "/1000")
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

    // Test deactivate user
    @Then("deactivate user in database")
    public void deactivate_user_in_database() {
        try {
            mockMvc.perform(post("/api/users/deactivate/" + testUser.get().getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
            boolean isActive =
                    userService.findById(testUser.get().getId()).get().isActive();
            assertFalse(isActive);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // Test reactivate user
    @Then("reactivate user in database")
    public void reactivate_user_in_database() {
        try {
            mockMvc.perform(post("/api/users/reactivate/" + testUser.get().getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
            boolean isActive =
                    userService.findById(testUser.get().getId()).get().isActive();
            assertTrue(isActive);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // Test edit user by admin
    @When("admin logged in and user exists in database")
    public void admin_logged_in_and_user_exists_in_database() {
        boolean isAdmin = false;

        try {
            assertNotEquals(token, null);
            assertNotEquals(token, "");

            for (Permission p : loggedInUser.get().getPermissions()) {
                if (p.getPermissionName().toString().equals("ADMIN_USER")) {
                    isAdmin = true;
                    break;
                }
            }
            assertNotEquals(isAdmin, false);

        } catch (Exception e) {
            fail("Admin not logged in");
        }
    }

    @Then("update user in database")
    public void update_user_in_database() {
        try {
            mockMvc.perform(put("/api/users/edit-profile/" + testUser.get().getId())
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
                                              "phone": "640601548865",
                                              "dailyLimit": 1000
                                            }
                                            """)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
            String editedName =
                    userService.findById(testUser.get().getId()).get().getFirstName();
            assertEquals(editedName, "NewTestUser");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // Test user updates their profile
    @Given("any user logs in")
    public void any_user_logs_in() {
        token = null;

        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/auth/login")
                                    .contentType("application/json")
                                    .content(
                                            """
                                                    {
                                                      "email": "testUser@gmail.com",
                                                      "password": "admin"
                                                    }
                                                    """))
                    .andExpect(status().isOk())
                    .andReturn();
            token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");
        } catch (Exception e) {
            fail("Test user failed to login");
        }
    }

    @When("user is logged in")
    public void user_is_logged_in() {
        try {
            assertNotEquals(token, null);
            assertNotEquals(token, "");
        } catch (Exception e) {
            fail("User token null or empty - not logged in properly");
        }
    }

        @Then("user updates his profile")
        public void user_updates_his_profile() {
            try {
                mockMvc.perform(put("/api/users/edit-profile/" + testUser.get().getId())
                                .contentType("application/json")
                                .content(
                                        """
                                                  {
                                                    "email": "testUser@gmail.com",
                                                    "firstName": "UserEditedName",
                                                    "lastName": "UserEditedLName",
                                                    "phone": "666666666"
                                                  }
                                                """)
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
                String editedName =
                        userService.findById(testUser.get().getId()).get().getFirstName();
                System.out.println(editedName);
                assertEquals(editedName, "UserEditedName");
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }

//     Test findUserByEmail
        @Then("get user by his email")
        public void get_user_by_his_email() {
            try {
                mockMvc.perform(get("/api/users/email")
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

//     Test user changes his password
        @Then("user changes his password")
        public void user_changes_his_password() {
            try {
                mockMvc.perform(put("/api/users/password/" + testUser.get().getId())
                                .contentType("application/json")
                                .content(
                                        """
                                                  {
                                                      "password": "testPass"
                                                  }
                                                """)
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
                String oldPass = testUser.get().getPassword();
                String newPass = userService.findById(testUser.get().getId()).get().getPassword();
                assertNotEquals(oldPass, newPass);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }

    // Testing deleting user
    @Given("privileged user logged in")
    public void privileged_user_logged_in() {
        token = null;
        boolean privileged = false;

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

            assertNotEquals(token, null);
            assertNotEquals(token, "");

            for (Permission p : loggedInUser.get().getPermissions()) {
                if (p.getPermissionName().toString().equals("ADMIN_USER")
                        || p.getPermissionName().toString().equals("DELETE_USERS")) {
                    privileged = true;
                    break;
                }
            }
            assertNotEquals(privileged, false);
        } catch (Exception e) {
            fail("User failed to login");
        }
    }

    @When("deleting user from database")
    public void deleting_user_from_database() {
        try {
            mockMvc.perform(delete("/api/users/" + testUser.get().getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNoContent())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user no longer in database")
    public void user_no_longer_in_database() { // todo fix
        Exception exception = assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/api/users/" + testUser.get().getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        });

        String expectedMessage = "Request processing failed; nested exception is"
                + " rs.edu.raf.si.bank2.main.exceptions.UserNotFoundException: User with id <"
                + testUser.get().getId()
                + "> not found.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}

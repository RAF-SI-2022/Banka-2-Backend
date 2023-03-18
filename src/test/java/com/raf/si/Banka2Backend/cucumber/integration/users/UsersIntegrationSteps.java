package com.raf.si.Banka2Backend.cucumber.integration.users;

import com.jayway.jsonpath.JsonPath;
import com.raf.si.Banka2Backend.models.Permission;
import com.raf.si.Banka2Backend.models.User;
import com.raf.si.Banka2Backend.services.UserService;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UsersIntegrationSteps extends UsersIntegrationTestConfig {

  @Autowired private UserService userService;

  @Autowired
  protected MockMvc mockMvc;
  protected static String token;
  protected static Optional<User> loggedInUser;
  protected static Optional<User> testUser;


  //Test logging in by admin
  @When("user can login")
  public void user_can_login() {

    //todo ne mogu da se gettuju permisije jer baca error "failed to lazily initialize a collection of role"
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
      MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                      .contentType("application/json")
                      .content("""
                              {
                                "email": "anesic3119rn+banka2backend+admin@raf.rs",
                                "password": "admin"
                              }
                              """)

              )
              .andExpect(status().isOk())
              .andReturn();
      token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");
    } catch (Exception e) {
      fail("User failed to login");
    }
  }


  //Test getting all permissions by admin
  @When("admin logged in")
  public void admin_logged_in() {
    try {
      assertNotEquals(token, null);
      assertNotEquals(token, "");

      /*
      user.getPerms contains "ADMIN_ROLE" //todo stavi kada se resi error sa gettovanjem permisija
       */

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
                      .header("Authorization", "Bearer " + token)
              )
              .andExpect(status().isOk())
              .andReturn();
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


  //Test user gets his permissions
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
                      .header("Authorization", "Bearer " + token)
              )
              .andExpect(status().isOk())
              .andReturn();
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


  //Test creating new user
  @When("Creating new user")
  public void creating_new_user() {
//    try {
//      MvcResult mvcResult = mockMvc.perform(post("/api/users/register") //todo odkomentarisi kada se napise testDeleteUserId
//                      .contentType("application/json")
//                      .content("""
//                              {
//                                "firstName": "TestUser",
//                                "lastName": "TestUser",
//                                "email": "testUser@gmail.com",
//                                "password": "admin",
//                                "permissions": [
//                                  "ADMIN_USER"
//                                ],
//                                "jobPosition": "ADMINISTRATOR",
//                                "active": true,
//                                "jmbg": "1231231231235",
//                                "phone": "640601548865"
//                              }
//                              """)
//                      .header("Content-Type", "application/json")
//                      .header("Access-Control-Allow-Origin", "*")
//                      .header("Authorization", "Bearer " + token)
//              )
//              .andExpect(status().isOk())
//              .andReturn();
//    } catch (Exception e) {
//      fail(e.getMessage());
//    }
  }
  @Then("New user is saved in database")
  public void new_user_is_saved_in_database() {
    Optional<User> user = userService.findByEmail("testUser@gmail.com");
    try {
      assertNotNull(user);
      assertEquals("testUser@gmail.com", user.get().getEmail());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


  //Test get all users
  @When("database not empty")
  public void database_not_empty() {
    try {
      List<User> users = userService.findAll();
      assertNotEquals(users.size(), 0);
    }
    catch (Exception e){
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
                      .header("Authorization", "Bearer " + token)
              )
              .andExpect(status().isOk())
              .andReturn();
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


  //Test deactivate user
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
  @Then("deactivate user in database")
  public void deactivate_user_in_database() {
    try {
      mockMvc.perform(post("/api/users/deactivate/" + testUser.get().getId())
                      .contentType("application/json")
                      .header("Content-Type", "application/json")
                      .header("Access-Control-Allow-Origin", "*")
                      .header("Authorization", "Bearer " + token)
              )
              .andExpect(status().isOk())
              .andReturn();
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


  //Test reactivate user
  @Then("reactivate user in database")
  public void reactivate_user_in_database() {
    try {
      mockMvc.perform(post("/api/users/reactivate/" + testUser.get().getId())
                      .contentType("application/json")
                      .header("Content-Type", "application/json")
                      .header("Access-Control-Allow-Origin", "*")
                      .header("Authorization", "Bearer " + token)
              )
              .andExpect(status().isOk())
              .andReturn();
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


  //Test edit user by admin
  @When("admin logged in and user exists in database")
  public void admin_logged_in_and_user_exists_in_database() {
    try{
      assertNotEquals(token, null);
      assertNotEquals(token, "");

      //user.getPerms contains "ADMIN_ROLE" //todo stavi kada se resi error sa gettovanjem permisija

    } catch (Exception e){
      fail("Admin not logged in");
    }
  }
  @Then("update user in database")
  public void update_user_in_database() {
    try {
      mockMvc.perform(post("/api/users/reactivate/" + testUser.get().getId())
                      .contentType("application/json")
                      .content("""
                                {
                                  "firstName": "ChangedName",
                                  "lastName": "ChangedLastname",
                                  "permissions": [
                                    "ADMIN_USER"
                                  ],
                                  "jobPosition": "string",
                                  "active": true,
                                  "phone": "string"
                                }
                              """)
                      .header("Content-Type", "application/json")
                      .header("Access-Control-Allow-Origin", "*")
                      .header("Authorization", "Bearer " + token)
              )
              .andExpect(status().isOk())
              .andReturn();
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /*
      email: email,
        firstName: firstName,
        lastName: lastName,
        permissions: permissions,
        jobPosition: jobPosition,
        active: active,
        phone: phone
   */


//  //Test find by email
//  @When("user exists in database")
//  public void user_exists_in_database() {
//    Optional<User> user = userService.findByEmail("testUser@gmail.com");
//    try {
//      assertNotNull(user);
//      assertEquals("testUser@gmail.com", user.get().getEmail());
//    } catch (Exception e) {
//      fail(e.getMessage());
//    }
//  }
//  @Then("get user by email")
//  public void get_user_by_email() {
//    try {
//      MvcResult mvcResult = mockMvc.perform(get("/api/users/email")
//                      .contentType("application/json")
//                      .header("Content-Type", "application/json")
//                      .header("Access-Control-Allow-Origin", "*")
//                      .header("Authorization", "Bearer " + token)
//                      .contextPath("/testUser@gmail.com")
//              )
//              .andExpect(status().isOk())
//              .andReturn();
//    } catch (Exception e) {
//      fail(e.getMessage());
//    }
//  }





  //Test ...
//  @Given("user in database")//given sluzi da se setup-uju baza za proceduju, npr dodavanje usera za brisanje
//  public void user_in_database() {
//    Optional<User> user = userService.findByEmail("dusan@gmail.com");
//    try {
//      assertNotNull(user);
//      assertEquals("dusan@gmail.com", user.get().getEmail());
//    } catch (Exception e) {
//      fail(e.getMessage());
//    }
//  }
//  @When("deleting user from database")
//  public void deleting_user_from_database() {
//    try{
//      Optional<User> toDeleteUser = userService.findByEmail("dusan@gmail.com");
//
//      userService.deleteById(toDeleteUser.get().getId());
//    }
//    catch (Exception e){
//      fail(e.getMessage());
//    }
//  }
//  @Then("user no longer in database")
//  public void user_no_longer_in_database() {
//    Exception exception = assertThrows(Exception.class, () -> {
//      userService.findByEmail("dusan@gmail.com");
//    });
//    String expectedMessage = "Can't delete user with id 1, because it doesn't exist";
//    String actualMessage = exception.getMessage();
//    assertEquals(actualMessage, expectedMessage);
//  }


  //Test ...
//  @Given("user in database")
//  public void user_in_database() {
//    User newUser =
//            User.builder()
//                    .id(1L)
//                    .jmbg("010100101010")
//                    .firstName("NewUser")
//                    .lastName("NewUser")
//                    .jobPosition("/")
//                    .active(true)
//                    .phone("21231231231")
//                    .password("1234")
//                    .email("newUser@gmail.com")
//                    .build();
//
//    try {
//      User savedUser = userService.save(newUser);
//      assertNotNull(savedUser);
//      assertEquals(newUser.getEmail(), savedUser.getEmail());
//    } catch (Exception e) {
//      fail(e.getMessage());
//    }
//  }
//  @When("user exists in database")
//  public void user_exists_in_database() {
//    Optional<User> user = userService.findByEmail("newUser@gmail.com");
//    try {
//      assertNotNull(user);
//      assertEquals("newUser@gmail.com", user.get().getEmail());
//    } catch (Exception e) {
//      fail(e.getMessage());
//    }
//  }
//  @Then("finding user by id")
//  public void finding_user_by_id() {
//    Optional<User> user = userService.findByEmail("newUser@gmail.com");
//    Optional<User> userById = userService.findById(user.get().getId());
//    try {
//      assertNotNull(user);
//      assertEquals(user.get().getEmail(), userById.get().getEmail());
//    } catch (Exception e) {
//      fail(e.getMessage());
//    }
//  }

}

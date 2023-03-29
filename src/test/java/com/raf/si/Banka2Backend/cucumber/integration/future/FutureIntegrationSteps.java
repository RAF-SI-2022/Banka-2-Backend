package com.raf.si.Banka2Backend.cucumber.integration.future;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.raf.si.Banka2Backend.models.mariadb.Future;
import com.raf.si.Banka2Backend.services.FutureService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class FutureIntegrationSteps extends FutureIntegrationTestConfig {

  @Autowired private FutureService futureService;

  @Autowired protected MockMvc mockMvc;

  protected static String token;

  protected static Future testFuture =
      new Future(1L, "corn", 8000, "bushel", 2100, "AGRICULTURE", null, true);

  @Given("user logs in")
  public void user_logs_in() {
    try {
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
      fail("User failed to login");
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

  @Then("user gets all futures from database")
  public void user_gets_futures() {
    try {
      mockMvc
          .perform(
              get("/api/futures")
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

  // Test user gets currency by id
  @Then("user gets future by id from database")
  public void user_gets_future_by_id() {
    try {
      mockMvc
          .perform(
              get("/api/futures/" + testFuture.getId())
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

  @Then("user gets future by name from database")
  public void user_gets_future_by_name() {
    try {
      mockMvc
          .perform(
              get("/api/futures/name/" + testFuture.getFutureName())
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
}

package rs.edu.raf.si.bank2.otc.cucumber.integration.marginBalance;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.otc.dto.CommunicationDto;
import rs.edu.raf.si.bank2.otc.models.mongodb.MarginBalance;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.MarginBalanceRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.MarginTransactionRepository;
import rs.edu.raf.si.bank2.otc.requests.LoginRequest;
import rs.edu.raf.si.bank2.otc.services.UserCommunicationService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MarginBalanceIntegrationSteps extends MarginBalanceIntegrationTestConfig {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserCommunicationService userCommunicationService;

    @Autowired
    MarginBalanceRepository marginBalanceRepository;

    ObjectMapper mapper = new ObjectMapper();
    protected static String token;

    MvcResult result;

    String balanceId = "500";
    String nonExistentBalanceId = "900";

    @Given("balances exist in database")
    public void balances_exist_in_database() {

        MarginBalance marginBalance1 = MarginBalance.builder().id(balanceId).build();
        MarginBalance marginBalance2 = MarginBalance.builder().build();
        MarginBalance marginBalance3 = MarginBalance.builder().build();

        marginBalanceRepository.save(marginBalance1);
        marginBalanceRepository.save(marginBalance2);
        marginBalanceRepository.save(marginBalance3);
    }

    @Given("user is logged in")
    public void user_is_logged_in() {

        try {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("anesic3119rn+banka2backend+admin@raf.rs");
            loginRequest.setPassword("admin");
            String userJsonBody = mapper.writeValueAsString(loginRequest);
            CommunicationDto communicationDto =
                    userCommunicationService.sendPostLike("/auth/login", userJsonBody, null, "POST");
            String[] split = communicationDto.getResponseMsg().split("\"");
            System.out.println(split);
            token = split[3];

        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(token);
    }

    @Then("user gets all margin balances")
    public void user_gets_all_margin_balances() throws Exception {

        result = mockMvc.perform(get("/api/marginAccount").contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JSONArray actualJson = new JSONArray(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");
    }

    @Then("user gets margin balance by id")
    public void user_gets_margin_balance_by_id() throws Exception {

        result = mockMvc.perform(get("/api/marginAccount/" + balanceId).contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JSONObject actualJson = new JSONObject(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");
    }

    @Given("balance doesnt exist in database")
    public void balance_doesnt_exist_in_database() throws Exception {

        result = mockMvc.perform(get("/api/marginTransaction/" + nonExistentBalanceId).contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();

        assertNotNull(result);
    }

    @When("user creates margin balance")
    public void user_creates_margin_balance() throws Exception {

        result = mockMvc.perform(post("/api/marginAccount")
                        .contentType("application/json")
                        .content(
                                """
                                        {
                                          "id": "900",
                                          "accountType": "CASH",
                                          "currencyCode": "string",
                                          "listingGroup": null,
                                          "investedResources": 0,
                                          "loanedResources": 0,
                                          "maintenanceMargin": 0,
                                          "marginCall": true
                                        }
                                         """)
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andReturn();

        JSONObject actualJson = new JSONObject(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");
    }

    @Then("margin balance is saved in database")
    public void margin_balance_is_saved_in_database() throws Exception {

        result = mockMvc.perform(get("/api/marginAccount/" + nonExistentBalanceId).contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JSONObject actualJson = new JSONObject(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");
    }


    @Then("user updates margin balance")
    public void user_updates_margin_balance() throws Exception {

        result = mockMvc.perform(put("/api/marginAccount/" + balanceId)
                        .contentType("application/json")
                        .content(
                                """
                                        {
                                          "accountType": "CASH",
                                          "currencyCode": "string",
                                          "listingGroup": null,
                                          "investedResources": 0,
                                          "loanedResources": 0,
                                          "maintenanceMargin": 0,
                                          "marginCall": true
                                        }
                                         """)
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JSONObject actualJson = new JSONObject(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");
    }


    @Then("user deletes margin balance")
    public void user_deletes_margin_balance() throws Exception {

        result = mockMvc.perform(delete("/api/marginAccount/" + balanceId)
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent())
                .andReturn();

        assertNotNull(result);
    }
}

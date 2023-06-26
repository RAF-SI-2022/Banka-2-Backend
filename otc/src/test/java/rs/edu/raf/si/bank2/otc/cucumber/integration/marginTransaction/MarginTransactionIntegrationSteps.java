package rs.edu.raf.si.bank2.otc.cucumber.integration.marginTransaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.otc.dto.CommunicationDto;
import rs.edu.raf.si.bank2.otc.models.mariadb.User;
import rs.edu.raf.si.bank2.otc.models.mongodb.*;
import rs.edu.raf.si.bank2.otc.repositories.mariadb.PasswordResetTokenRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.CompanyRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.ContactRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.MarginTransactionRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.TransactionElementRepository;
import rs.edu.raf.si.bank2.otc.requests.LoginRequest;
import rs.edu.raf.si.bank2.otc.services.UserCommunicationService;
import rs.edu.raf.si.bank2.otc.services.interfaces.AuthorisationServiceInterface;
import rs.edu.raf.si.bank2.otc.services.interfaces.UserServiceInterface;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MarginTransactionIntegrationSteps extends MarginTransactionIntegrationTestConfig {

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

    @Autowired
    UserCommunicationService userCommunicationService;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    MarginTransactionRepository marginTransactionRepository;

    ObjectMapper mapper = new ObjectMapper();
    protected static String token;

    MvcResult result;

    String group = "ORDER_TYPE";
    String transactionId = "500";
    String nonExistentTransactionId = "501";
    String email = "test@transaction.com";

    @Given("transactions exist in database")
    public void transactions_exist_in_database() {

        MarginTransaction marginTransaction1 = MarginTransaction.builder().id(transactionId).orderType(group).userEmail(email).build();
        MarginTransaction marginTransaction2 = MarginTransaction.builder().orderType(group).build();
        MarginTransaction marginTransaction3 = MarginTransaction.builder().build();

        marginTransactionRepository.save(marginTransaction1);
        marginTransactionRepository.save(marginTransaction2);
        marginTransactionRepository.save(marginTransaction3);
    }
    @And("user is logged in")
    public void user_is_logged_in(){
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
    @Then("user gets all transactions")
    public void user_gets_all_transactions() throws Exception {

        result = mockMvc.perform(get("/api/marginTransaction").contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JSONArray actualJson = new JSONArray(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");
    }

    @Then("user gets all transactions by group")
    public void user_gets_all_transactions_by_group() throws Exception {

        result = mockMvc.perform(get("/api/marginTransaction/byGroup/" + group).contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JSONArray actualJson = new JSONArray(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");
    }

    @Then("user gets transaction by id")
    public void user_gets_transaction_by_id() throws Exception {

        result = mockMvc.perform(get("/api/marginTransaction/" + transactionId).contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JSONObject actualJson = new JSONObject(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");
    }

    @Given("transaction doesnt exist in database")
    public void transaction_doesnt_exist_in_database() throws Exception {

        result = mockMvc.perform(get("/api/marginTransaction/" + nonExistentTransactionId).contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();

        assertNotNull(result);
    }

    @When("user creates margin transaction")
    public void user_creates_margin_transaction() throws Exception {

        result = mockMvc.perform(post("/api/marginTransaction/makeTransaction")
                        .contentType("application/json")
                        .content(
                                """
                                        {
                                          "accountType": "CASH",
                                          "orderId": 500,
                                          "transactionComment": "string",
                                          "currencyCode": "string",
                                          "transactionType": "BUY",
                                          "initialMargin": 0,
                                          "maintenanceMargin": 0
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

    @Then("margin transaction is saved in database")
    public void margin_transaction_is_saved_in_database() throws Exception {

        result = mockMvc.perform(get("/api/marginTransaction/" + nonExistentTransactionId).contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JSONObject actualJson = new JSONObject(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");
    }

    @Then("user gets transaction by email")
    public void user_gets_transaction_by_email() throws Exception {

        result = mockMvc.perform(get("/api/marginTransaction/email/" + email).contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JSONObject actualJson = new JSONObject(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");
    }
}

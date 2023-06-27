package rs.edu.raf.si.bank2.otc.cucumber.integration.otc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.otc.dto.CommunicationDto;
import rs.edu.raf.si.bank2.otc.models.mariadb.User;
import rs.edu.raf.si.bank2.otc.models.mongodb.Company;
import rs.edu.raf.si.bank2.otc.models.mongodb.Contract;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContractElements;
import rs.edu.raf.si.bank2.otc.models.mongodb.TransactionElement;
import rs.edu.raf.si.bank2.otc.repositories.mariadb.PasswordResetTokenRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.CompanyRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.ContactRepository;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.TransactionElementRepository;
import rs.edu.raf.si.bank2.otc.requests.LoginRequest;
import rs.edu.raf.si.bank2.otc.services.UserCommunicationService;
import rs.edu.raf.si.bank2.otc.services.interfaces.AuthorisationServiceInterface;
import rs.edu.raf.si.bank2.otc.services.interfaces.UserServiceInterface;

public class OtcIntegrationSteps extends OtcIntegrationTestConfig {

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
    TransactionElementRepository transactionElementRepository;

    ObjectMapper mapper = new ObjectMapper();
    protected static String token;

    String elementId = "501";
    String companyId = "500";
    String contractId = "500";

    /**
     * MvcResult of the last executed mock request.
     */
    MvcResult result;

    @Autowired
    ContactRepository contactRepository;

    @Given("user logs in")
    public void user_logs_in() {
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

    @When("contracts exist in database")
    public void contracts_exist_in_database() {

        Company company = Company.builder().id(companyId).build();

        Contract contract1 = Contract.builder()
                .id(contractId)
                .contractStatus(ContractElements.BUY)
                .contractNumber("300")
                .companyId(companyId)
                .transactionElements(Arrays.asList(
                        TransactionElement.builder().id("502").build(),
                        TransactionElement.builder().id("503").build()))
                .build();
        Contract contract2 = Contract.builder()
                .contractStatus(ContractElements.BUY)
                .contractNumber("300")
                .companyId(companyId)
                .build();
        Contract contract3 = Contract.builder()
                .contractStatus(ContractElements.BUY)
                .contractNumber("300")
                .build();

        companyRepository.save(company);

        contactRepository.save(contract1);
        contactRepository.save(contract2);
        contactRepository.save(contract3);
    }

    @Then("user gets all contracts")
    public void user_gets_all_contracts() throws Exception {

        result = mockMvc.perform(get("/api/otc")
                        .contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JSONArray actualJson = new JSONArray(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");
        //        assertNotNull(result, "Json is not null");
    }

    @Then("user gets all contracts owned by that company")
    public void user_gets_all_contracts_owned_by_that_company() throws Exception {

        result = mockMvc.perform(get("/api/otc/byCompany/" + companyId)
                        .contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JSONArray actualJson = new JSONArray(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");

        //        assertNotNull(result, "Json is not null");
    }

    @Then("user gets contract with specified contract id")
    public void user_gets_contract_with_specified_contract_id() throws Exception {

        result = mockMvc.perform(get("/api/otc/" + contractId)
                        .contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JSONObject actualJson = new JSONObject(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");
    }

    @Then("user opens contract")
    public void user_opens_contract() throws Exception {

        result = mockMvc.perform(post("/api/otc/open")
                        .contentType("application/json")
                        .content(
                                """
                                        {
                                          "companyId": "500",
                                          "contractStatus": "DRAFT",
                                          "contractNumber": "300",
                                          "description": "string"
                                        }
                                         """)
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        assertEquals(response, "Ugovor je uspesno otvoren");

        //        assertNotNull(result, "Json is not null");
    }

    @Then("user edits contract")
    public void user_edits_contract() throws Exception {
        result = mockMvc.perform(patch("/api/otc/edit")
                        .contentType("application/json")
                        .content(
                                """
                                        {
                                          "companyId": "500",
                                          "contractStatus": "DRAFT",
                                          "contractNumber": "300",
                                          "description": "edited description"
                                        }
                                         """)
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()) // isOk()
                .andReturn();

        String response = result.getResponse().getContentAsString();

        assertEquals(response, "Ugovor je uspesno promenjen");

        //        assertNotNull(result, "Json is not null");
    }

    @Then("user finalizes contract by id")
    public void user_finalizes_contract_by_id() throws Exception {

        result = mockMvc.perform(patch("/api/otc/finalize/" + contractId)
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()) // isOk
                .andReturn();

        String response = result.getResponse().getContentAsString();

        assertEquals(response, "Ugovor uspesno kompletiran");

        //        assertNotNull(result, "Json is not null");
    }

    @Then("user deletes contract by id")
    public void user_deletes_contract_by_id() throws Exception {

        result = mockMvc.perform(delete("/api/otc/delete/" + contractId)
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()) // isOk
                .andReturn();

        String response = result.getResponse().getContentAsString();

        assertEquals(response, "Ugovor uspesno izbrisan");

        //        assertNotNull(result, "Json is not null");
    }

    @When("elements exist in database")
    public void elements_exist_in_database() {

        TransactionElement transactionElement1 =
                TransactionElement.builder().id("501").build();
        TransactionElement transactionElement2 = TransactionElement.builder().build();
        TransactionElement transactionElement3 = TransactionElement.builder().build();

        transactionElementRepository.save(transactionElement1);
        transactionElementRepository.save(transactionElement2);
        transactionElementRepository.save(transactionElement3);
    }

    @Then("user gets all elements")
    public void user_gets_all_elements() throws Exception {

        result = mockMvc.perform(get("/api/otc/elements")
                        .contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()) // isOk()
                .andReturn();

        JSONArray actualJson = new JSONArray(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");

        //        assertNotNull(result, "Json is not null");

    }

    @Then("user gets element by id")
    public void user_gets_element_by_id() throws Exception {

        result = mockMvc.perform(get("/api/otc/element/" + elementId)
                        .contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()) // isOk()
                .andReturn();

        JSONObject actualJson = new JSONObject(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");

        //        assertNotNull(result, "Json is not null");

    }

    @Then("user gets elements for contract")
    public void user_gets_elements_for_contract() throws Exception {

        result = mockMvc.perform(get("/api/otc/contract_elements/" + contractId)
                        .contentType("application/json")
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()) // isOk()
                .andReturn();

        JSONArray actualJson = new JSONArray(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");

        //        assertNotNull(result, "Json is not null");
    }

    @Then("user adds element to contract")
    public void user_adds_element_to_contract() throws Exception {

        result = mockMvc.perform(post("/api/otc/add_element")
                        .contentType("application/json")
                        .content(
                                """
                                        {
                                          "contractId": "500",
                                          "elementId": "504",
                                          "buyOrSell": "DRAFT",
                                          "transactionElement": "STOCK",
                                          "balance": "DRAFT",
                                          "currency": "string",
                                          "amount": 0,
                                          "priceOfOneElement": 0,
                                          "userId": 0,
                                          "mariaDbId": 0,
                                          "futureStorageField": "string"
                                        }
                                         """)
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()) // isOk()
                .andReturn();

        JSONObject actualJson = new JSONObject(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");

        //        assertNotNull(result, "Json is not null");
    }

    @Then("user deletes element from contract")
    public void user_deletes_element_from_contract() throws Exception {

        result = mockMvc.perform(delete("/api/otc/remove_element/" + contractId + "/" + elementId)
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()) // isOk
                .andReturn();

        JSONObject actualJson = new JSONObject(result.getResponse().getContentAsString());

        assertNotNull(actualJson, "Json is not null");

        //        assertNotNull(result, "Json is not null");

    }
}

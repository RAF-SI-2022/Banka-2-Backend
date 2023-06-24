package rs.edu.raf.si.bank2.client.cucumber.integration.payment;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.client.dto.*;
import rs.edu.raf.si.bank2.client.models.mongodb.Client;
import rs.edu.raf.si.bank2.client.models.mongodb.PaymentReceiver;
import rs.edu.raf.si.bank2.client.repositories.mongodb.ClientRepository;
import rs.edu.raf.si.bank2.client.repositories.mongodb.PaymentReceiverRepository;
import rs.edu.raf.si.bank2.client.services.ClientService;

public class PaymentIntegrationSteps extends PaymentIntegrationTestConfig {

    @Autowired
    ClientService clientService;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    PaymentReceiverRepository paymentReceiverRepository;

    protected static String token;

    protected static String testClientId;

    ObjectMapper mapper = new ObjectMapper();

    @Given("test client is logged in")
    public void test_client_is_logged_in() {
        Optional<Client> testClient = clientRepository.findClientByEmail("test@gmail.com");
        if (testClient.isEmpty()) {
            Client newClient = new Client(
                    "Test",
                    "Testic",
                    "b-day",
                    "nonb",
                    "test@gmail.com",
                    "123123123",
                    "addres",
                    "password",
                    new ArrayList<>());
            clientRepository.save(newClient);
        }

        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/api/client/login")
                                    .contentType("application/json")
                                    .content(
                                            """
                                                    {
                                                      "email": "test@gmail.com",
                                                      "password": "password"
                                                    }
                                                    """))
                    .andExpect(status().isOk())
                    .andReturn();
            token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");

            System.out.println(token);
        } catch (Exception e) {
            fail("User failed to login");
        }
    }
    //    private String senderEmail;
    //    private String receiverName;
    //    private String fromBalanceRegNum;
    //    private String toBalanceRegNum;
    //    private Double amount;
    //    private String referenceNumber;
    //    private String paymentNumber;
    //    private String paymentDescription;

    @Then("make payment")
    public void make_payment() throws JsonProcessingException {
        PaymentDto paymentDto = new PaymentDto("asdfasf", "13212331", "name", 1.0, "2312321", "12321", "asdfasdf");
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(paymentDto);
        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/payment/removeMoney")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("add payment receiver failed");
        }
    }

    @Then("remove money")
    public void remove_money() throws JsonProcessingException {
        RemoveMoneyDto paymentDto = new RemoveMoneyDto("13212331", 1.0);
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(paymentDto);
        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/payment/makePayment")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("add payment receiver failed");
        }
    }

    @Then("transfer money")
    public void transfer_money() throws JsonProcessingException {
        TransferDto paymentDto = new TransferDto("2131231", "13212331", 1.0);
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(paymentDto);
        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/payment/transferMoney")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("add payment receiver failed");
        }
    }

    @Then("exchange money")
    public void exchange_money() throws JsonProcessingException {
        ExchangeDto paymentDto = new ExchangeDto("2131231", "13212331", "USD_RSD", 1.0);
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(paymentDto);
        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/payment/exchangeMoney")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
            fail("add payment receiver failed");
        }
    }

    @Then("get payments for client")
    public void get_payments_for_client() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/payment/payments/test@gmail.com")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("get payments for client failed");
        }
    }

    @Then("add payment receiver")
    public void add_payment_receiver() throws JsonProcessingException {
        PaymentReceiverDto creditRequestDto =
                new PaymentReceiverDto("test@gmail.com", "name", "13232", "12312", "12312", "asdfasdf");
        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(creditRequestDto);
        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/payment/addReceiver")
                            .contentType("application/json")
                            .content(body)
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("add payment receiver failed");
        }
    }

    @Then("get all saved receivers for client")
    public void get_all_saved_receivers_for_client() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/api/payment/getReceivers/test@gmail.com")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("get all saved receivers for client failed");
        }
    }

    @Then("edit receiver")
    public void edit_receiver() throws JsonProcessingException {
        List<PaymentReceiver> receivers = paymentReceiverRepository.findAll();

        if (receivers.size() != 0) {
            PaymentReceiverDto creditRequestDto =
                    new PaymentReceiverDto("test@gmail.com", "name", "13232", "12312", "12312", "asdfasdf");
            String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(creditRequestDto);
            try {

                System.out.println(receivers);
                System.out.println(receivers.size());
                System.out.println(receivers.get(0).getId());

                MvcResult mvcResult = mockMvc.perform(patch("/api/payment/editReceiver/"
                                        + receivers.get(0).getId())
                                .contentType("application/json")
                                .content(body)
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            } catch (Exception e) {
                e.printStackTrace();
                fail("edit receiver failed");
            }
        }
    }

    @Then("delete receiver")
    public void delete_receiver() {
        List<PaymentReceiver> receivers = paymentReceiverRepository.findAll();
        if (receivers.size() != 0) {
            try {
                MvcResult mvcResult = mockMvc.perform(delete("/api/payment/deleteReceivers/ "
                                        + receivers.get(0).getId())
                                .header("Content-Type", "application/json")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();
            } catch (Exception e) {
                fail("edit receiver failed");
            }
        }
    }
}

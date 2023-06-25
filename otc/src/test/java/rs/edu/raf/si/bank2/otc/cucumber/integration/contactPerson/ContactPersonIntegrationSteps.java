package rs.edu.raf.si.bank2.otc.cucumber.integration.contactPerson;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.concurrent.atomic.AtomicLong;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import rs.edu.raf.si.bank2.otc.exceptions.ContactPersonNotFoundException;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContactPerson;
import rs.edu.raf.si.bank2.otc.services.CompanyBankAccountService;
import rs.edu.raf.si.bank2.otc.services.CompanyService;
import rs.edu.raf.si.bank2.otc.services.ContactPersonService;
import rs.edu.raf.si.bank2.otc.services.interfaces.AuthorisationServiceInterface;
import rs.edu.raf.si.bank2.otc.services.interfaces.UserServiceInterface;

public class ContactPersonIntegrationSteps extends ContactPersonIntegrationTestConfiguration {

    /**
     * Token for testing the validity.
     */
    String token;

    /**
     * ResultActions of the last executed mock request.
     */
    ResultActions resultActions;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserServiceInterface userServiceInterface;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    @InjectMocks
    AuthorisationServiceInterface authorisationServiceInterface;

    @Autowired
    CompanyBankAccountService bankAccountService;

    @Autowired
    CompanyService companyService;

    @Autowired
    ContactPersonService contactPersonService;

    ContactPerson contactPerson;
    AtomicLong atomicId = new AtomicLong(System.currentTimeMillis());

    @Given("user logs in")
    public void user_logs_in() {
        this.token = null;
        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/api/auth/login")
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
            this.token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");
        } catch (Exception e) {
            fail("Test user failed to login");
        }
    }

    @When("user is logged in")
    public void user_is_logged_in() {
        try {
            assertNotEquals(this.token, null);
            assertNotEquals(this.token, "");
        } catch (Exception e) {
            fail("User token null or empty - not logged in properly");
        }
    }

    @Then("user gets all contact persons")
    public void userGetsAllContactPersons() {
        try {
            mockMvc.perform(get("/api/contact")
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + this.token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Given("contact person exists in db")
    public void contactPersonExistsInDb() {
        try {
            this.contactPerson = this.contactPersonService.getContactPersonById(String.valueOf(atomicId.get()));
        } catch (ContactPersonNotFoundException e) {
            ContactPerson cp = ContactPerson.builder()
                    .id(String.valueOf(atomicId.getAndIncrement()))
                    .firstName("Miladin")
                    .lastName("Miladinovic")
                    .phoneNumber("0657079121")
                    .email("miladiniski" + atomicId.get() + "@gmail.com")
                    .position("Administrator")
                    .note("Note")
                    .build();
            this.contactPerson = this.contactPersonService.addContactPerson(cp);
        }
    }

    @Then("user gets contact person by id")
    public void userGetsContactPersonById() {
        try {
            mockMvc.perform(get("/api/contact/id/" + this.contactPerson.getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + this.token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user creates contact person")
    public void userCreatesContactPerson() {
        try {
            mockMvc.perform(post("/api/contact")
                            .contentType("application/json")
                            .content(String.format(
                                    """
                                {
                                    "id": "%s",
                                    "firstName": "Ljuba",
                                    "lastName": "Antic",
                                    "phoneNumber": "065888123",
                                    "email": "%s",
                                    "position": "Administrator",
                                    "note": "note"
                                }
                                """,
                                    this.atomicId.getAndIncrement(), "ljuba" + this.atomicId.get() + "@gmail.com"))
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + this.token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user edits contact person")
    public void userEditsContactPerson() {
        try {
            mockMvc.perform(post("/api/contact/edit")
                            .contentType("application/json")
                            .content(String.format(
                                    """
                                {
                                    "id": "%s",
                                    "firstName": "Miladin",
                                    "lastName": "Miladinovic",
                                    "phoneNumber": "065843123",
                                    "email": "%s",
                                    "position": "Administrator",
                                    "note": "note"
                                }
                                """,
                                    this.contactPerson.getId(), "milandin" + this.contactPerson.getId() + "@gmail.com"))
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + this.token))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("user deletes contact person")
    public void userDeletesContactPerson() {
        try {
            mockMvc.perform(delete("/api/contact/" + this.contactPerson.getId())
                            .contentType("application/json")
                            .header("Content-Type", "application/json")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Authorization", "Bearer " + this.token))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}

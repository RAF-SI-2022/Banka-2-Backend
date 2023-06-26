package rs.edu.raf.si.bank2.otc.cucumber.integration.marginBalance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rs.edu.raf.si.bank2.otc.repositories.mongodb.MarginTransactionRepository;
import rs.edu.raf.si.bank2.otc.services.UserCommunicationService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class MarginBalanceIntegrationSteps extends MarginBalanceIntegrationTestConfig {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserCommunicationService userCommunicationService;

    @Autowired
    MarginTransactionRepository marginTransactionRepository;

    ObjectMapper mapper = new ObjectMapper();
    protected static String token;

    MvcResult result;


}

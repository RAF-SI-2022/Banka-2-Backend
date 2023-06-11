package rs.edu.raf.si.bank2.client.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.client.dto.*;
import rs.edu.raf.si.bank2.client.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.client.models.mariadb.User;
import rs.edu.raf.si.bank2.client.models.mongodb.Client;
import rs.edu.raf.si.bank2.client.models.mongodb.DevizniRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.PoslovniRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.TekuciRacun;
import rs.edu.raf.si.bank2.client.services.BalanceService;
import rs.edu.raf.si.bank2.client.services.ClientService;
import rs.edu.raf.si.bank2.client.services.interfaces.UserCommunicationInterface;

import java.util.Optional;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RestController
@CrossOrigin
@RequestMapping("/api/client")
public class ClientController {

    private final UserCommunicationInterface userCommunicationInterface;
    private final ClientService clientService;
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public ClientController(UserCommunicationInterface userCommunicationInterface, ClientService clientService) {
        this.userCommunicationInterface = userCommunicationInterface;
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<?> getAllClients() {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        User user = null;
        CommunicationDto response = userCommunicationInterface.sendGet(signedInUserEmail, "/findByEmail");

        if (response.getResponseCode() == 200) {
            try {
                user = mapper.readValue(response.getResponseMsg(), User.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());

//        System.err.println(user);

        return ResponseEntity.ok().body(clientService.getAllClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClient(@PathVariable(name = "id") String id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        Optional<Client> client = clientService.getClient(id);
        if (client.isEmpty()) return ResponseEntity.status(404).body("Trazeni klijent ne postoji");
        return ResponseEntity.ok().body(client.get());
    }

    @PostMapping("/createClient")
    public ResponseEntity<?> createClient(@RequestBody ClientDto clientDto) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        User user = null;
        CommunicationDto userResponse = userCommunicationInterface.sendGet(signedInUserEmail, "/findByEmail");

        if (userResponse.getResponseCode() == 200) {
            try {
                user = mapper.readValue(userResponse.getResponseMsg(), User.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else return ResponseEntity.status(userResponse.getResponseCode()).body(userResponse.getResponseMsg());

        CommunicationDto response = clientService.createClient(user.getId(), clientDto);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }
}

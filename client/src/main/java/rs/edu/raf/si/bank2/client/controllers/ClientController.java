package rs.edu.raf.si.bank2.client.controllers;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.client.dto.*;
import rs.edu.raf.si.bank2.client.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.client.models.mongodb.Client;
import rs.edu.raf.si.bank2.client.requests.LoginRequest;
import rs.edu.raf.si.bank2.client.responses.ClientLoginResponse;
import rs.edu.raf.si.bank2.client.services.ClientService;
import rs.edu.raf.si.bank2.client.services.MailingService;
import rs.edu.raf.si.bank2.client.services.interfaces.UserCommunicationInterface;
import rs.edu.raf.si.bank2.client.utils.JwtUtil;

import java.util.Optional;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RestController
@CrossOrigin
@RequestMapping("/api/client")
@Timed
public class ClientController {

    private final UserCommunicationInterface userCommunicationInterface;
    private final ClientService clientService;
    private final JwtUtil jwtUtil;
    private final MailingService mailingService;

    @Autowired
    public ClientController(UserCommunicationInterface userCommunicationInterface, ClientService clientService, JwtUtil jwtUtil, MailingService mailingService) {
        this.userCommunicationInterface = userCommunicationInterface;
        this.clientService = clientService;
        this.jwtUtil = jwtUtil;
        this.mailingService = mailingService;
    }

    @Timed("controllers.client.getAllClients")
    @GetMapping
    public ResponseEntity<?> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @Timed("controllers.client.mailFromToken")
    @GetMapping("/mailFromToken")
    public ResponseEntity<?> getClientMailFromToken(){
        String signedInUserEmail = getContext().getAuthentication().getName();
        return ResponseEntity.ok(signedInUserEmail);
    }

    @Timed("controllers.client.getClient")
    @GetMapping("/{id}")
    public ResponseEntity<?> getClient(@PathVariable(name = "id") String id) {
        Optional<Client> client = clientService.getClient(id);
        if (client.isEmpty()) return ResponseEntity.status(404).body("Trazeni klijent ne postoji");
        return ResponseEntity.ok(client.get());
    }

    @Timed("controllers.client.createClient")
    @PostMapping("/createClient")
    public ResponseEntity<?> createClient(@RequestBody ClientDto clientDto) {
        System.out.println(clientDto);
        return ResponseEntity.ok(clientService.createClient(clientDto));
    }

    @Timed("controllers.client.loginClient")
    @PostMapping("/login")
    public ResponseEntity<?> loginClient(@RequestBody LoginRequest loginRequest){
        String token = clientService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
        if (token == null) return ResponseEntity.status(403).body("Bad credentials");
        return ResponseEntity.ok(new ClientLoginResponse(token));
    }

    @Timed("controllers.client.sendTokenToMail")
    @PostMapping("/sendToken/{email}")
    public ResponseEntity<?> sendTokenToMail(@PathVariable String email){
        mailingService.sendRegistrationToken(email);//todo dodaj email
        return ResponseEntity.ok("Token sent");
    }

    @Timed("controllers.client.checkToken")
    @GetMapping("/checkToken/{token}")
    public ResponseEntity<?> checkToken(@PathVariable String token){
        if (mailingService.checkIfTokenGood(token))
            return ResponseEntity.ok("Valid");
        return ResponseEntity.status(404).body("Not valid");
    }

}

package rs.edu.raf.si.bank2.client.controllers;

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

    @GetMapping
    public ResponseEntity<?> getAllClients() {
        String signedInUserEmail = getContext().getAuthentication().getName();
//        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
//            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
//        }
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/mailFromToken")
    public ResponseEntity<?> getClientMailFromToken(){
        String signedInUserEmail = getContext().getAuthentication().getName();
        return ResponseEntity.ok(signedInUserEmail);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClient(@PathVariable(name = "id") String id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        Optional<Client> client = clientService.getClient(id);
        if (client.isEmpty()) return ResponseEntity.status(404).body("Trazeni klijent ne postoji");
        return ResponseEntity.ok(client.get());
    }

    @PostMapping("/createClient")
    public ResponseEntity<?> createClient(@RequestBody ClientDto clientDto) {
        System.out.println(clientDto);
        return ResponseEntity.ok(clientService.createClient(clientDto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginClient(@RequestBody LoginRequest loginRequest){
        String token = clientService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
        if (token == null) return ResponseEntity.status(403).body("Bad credentials");
        return ResponseEntity.ok(new ClientLoginResponse(token));
    }

    @PostMapping("/sendToken/{email}")
    public ResponseEntity<?> sendTokenToMail(@PathVariable String email){
        mailingService.sendRegistrationToken(email);//todo dodaj email
        return ResponseEntity.ok("Token sent");
    }

    @GetMapping("/checkToken/{token}")
    public ResponseEntity<?> checkToken(@PathVariable String token){
        if (mailingService.checkIfTokenGood(token))
            return ResponseEntity.ok("Valid");
        return ResponseEntity.status(404).body("Not valid");
    }

}

package rs.edu.raf.si.bank2.client.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import rs.edu.raf.si.bank2.client.repositories.mongodb.ClientRepository;
import rs.edu.raf.si.bank2.client.services.BalanceService;
import rs.edu.raf.si.bank2.client.services.interfaces.UserCommunicationInterface;

import java.util.Optional;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RestController
@CrossOrigin
@RequestMapping("/api/balance")
public class BalanceController {

    private final UserCommunicationInterface userCommunicationInterface;
    private final BalanceService balanceService;
    private final ClientRepository clientRepository;

    @Autowired
    public BalanceController(UserCommunicationInterface userCommunicationInterface, BalanceService balanceService, ClientRepository clientRepository) {
        this.userCommunicationInterface = userCommunicationInterface;
        this.balanceService = balanceService;
        this.clientRepository = clientRepository;
    }

    @GetMapping("/tekuci")
    public ResponseEntity<?> getAllTekuciRacuni() {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }
        return ResponseEntity.ok(balanceService.getAllTekuciRacuni());
    }

    @GetMapping("/poslovni")
    public ResponseEntity<?> getAllPoslovniRacuni() {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }
        return ResponseEntity.ok(balanceService.getAllPoslovniRacuni());
    }

    @GetMapping("/devizni")
    public ResponseEntity<?> getAllDevizniRacuni() {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }
        return ResponseEntity.ok().body(balanceService.getAllDevizniRacuni());
    }

    @GetMapping("/devizni/{devizniRacunId}")
    public ResponseEntity<?> getDevizniRacun(@PathVariable(name = "devizniRacunId") String id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        Optional<DevizniRacun> devizniRacun = balanceService.getDevizniRacun(id);
        if (devizniRacun.isEmpty()) return ResponseEntity.status(404).body("Trazeni racun ne postoji");
        return ResponseEntity.ok(devizniRacun.get());
    }

    @GetMapping("/tekuci/{tekuciRacunId}")
    public ResponseEntity<?> getTekuciRacun(@PathVariable(name = "tekuciRacunId") String id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        Optional<TekuciRacun> tekuciRacun = balanceService.getTekuciRacun(id);
        if (tekuciRacun.isEmpty()) return ResponseEntity.status(404).body("Trazeni racun ne postoji");
        return ResponseEntity.ok(tekuciRacun.get());
    }

    @GetMapping("/poslovni/{poslovniRacunId}")
    public ResponseEntity<?> getPoslovniRacun(@PathVariable(name = "poslovniRacunId") String id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        Optional<PoslovniRacun> poslovniRacun = balanceService.getPoslovniRacun(id);
        if (poslovniRacun.isEmpty()) return ResponseEntity.status(404).body("Trazeni racun ne postoji");
        return ResponseEntity.ok(poslovniRacun.get());
    }


    @PostMapping("/openDevizniRacun")
    public ResponseEntity<?> openDevizniRacun(@RequestBody DevizniRacunDto devizniRacunDto) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        BalanceDto response = balanceService.openDevizniRacun(devizniRacunDto);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }

    @PostMapping("/openTekuciRacun")
    public ResponseEntity<?> openTekuciRacun(@RequestBody TekuciRacunDto tekuciRacunDto) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }
        BalanceDto response = balanceService.openTekuciRacun(tekuciRacunDto);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }

    @PostMapping("/openPoslovniRacun")
    public ResponseEntity<?> openPoslovniRacun(@RequestBody PoslovniRacunDto poslovniRacunDto) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        BalanceDto response = balanceService.openPoslovniRacun(poslovniRacunDto);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }
}

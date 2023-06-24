package rs.edu.raf.si.bank2.client.controllers;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.client.dto.*;
import rs.edu.raf.si.bank2.client.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.client.models.mongodb.*;
import rs.edu.raf.si.bank2.client.repositories.mongodb.ClientRepository;
import rs.edu.raf.si.bank2.client.repositories.mongodb.DevizniRacunRepository;
import rs.edu.raf.si.bank2.client.repositories.mongodb.PoslovniRacunRepository;
import rs.edu.raf.si.bank2.client.repositories.mongodb.TekuciRacunRepository;
import rs.edu.raf.si.bank2.client.services.BalanceService;
import rs.edu.raf.si.bank2.client.services.interfaces.UserCommunicationInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RestController
@CrossOrigin
@RequestMapping("/api/balance")
@Timed
public class BalanceController {

    private final UserCommunicationInterface userCommunicationInterface;
    private final BalanceService balanceService;
    private final ClientRepository clientRepository;
    private final DevizniRacunRepository devizniRacunRepository;
    private final TekuciRacunRepository tekuciRacunRepository;
    private final PoslovniRacunRepository poslovniRacunRepository;

    @Autowired
    public BalanceController(UserCommunicationInterface userCommunicationInterface, BalanceService balanceService, ClientRepository clientRepository, DevizniRacunRepository devizniRacunRepository, TekuciRacunRepository tekuciRacunRepository, PoslovniRacunRepository poslovniRacunRepository) {
        this.userCommunicationInterface = userCommunicationInterface;
        this.balanceService = balanceService;
        this.clientRepository = clientRepository;
        this.devizniRacunRepository = devizniRacunRepository;
        this.tekuciRacunRepository = tekuciRacunRepository;
        this.poslovniRacunRepository = poslovniRacunRepository;
    }

    @Timed("controllers.balance.getAllTekuciRacuni")
    @GetMapping("/tekuci")
    public ResponseEntity<?> getAllTekuciRacuni() {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }
        return ResponseEntity.ok(balanceService.getAllTekuciRacuni());
    }

    @Timed("controllers.balance.getAllPoslovniRacuni")
    @GetMapping("/poslovni")
    public ResponseEntity<?> getAllPoslovniRacuni() {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }
        return ResponseEntity.ok(balanceService.getAllPoslovniRacuni());
    }

    @Timed("controllers.balance.getAllDevizniRacuni")
    @GetMapping("/devizni")
    public ResponseEntity<?> getAllDevizniRacuni() {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }
        return ResponseEntity.ok().body(balanceService.getAllDevizniRacuni());
    }

    @Timed("controllers.balance.getAllClientBalances")
    @GetMapping("/forClient/{email}")
    public ResponseEntity<?> getAllClientBalances(@PathVariable String email){
        Optional<Client> client = clientRepository.findClientByEmail(email);
        if (client.isEmpty()) return ResponseEntity.status(404).body("Client not found");

        List<Racun> allRacuni = new ArrayList<>();
        allRacuni.addAll(tekuciRacunRepository.findTekuciRacunByOwnerId(client.get().getId()));
        allRacuni.addAll(poslovniRacunRepository.findPoslovniRacunByOwnerId(client.get().getId()));
        allRacuni.addAll(devizniRacunRepository.findDevizniRacunByOwnerId(client.get().getId()));
        return ResponseEntity.ok(allRacuni);
    }

    @Timed("controllers.balance.getDevizniRacun")
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

    @Timed("controllers.balance.getTekuciRacun")
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

    @Timed("controllers.balance.getPoslovniRacun")
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


    //todo trenutno je na frontu hard code interest rade i acc maintenance
    @Timed("controllers.balance.openDevizniRacun")
    @PostMapping("/openDevizniRacun")
    public ResponseEntity<?> openDevizniRacun(@RequestBody DevizniRacunDto devizniRacunDto) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        CommunicationDto response = balanceService.openDevizniRacun(devizniRacunDto);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }

    @Timed("controllers.balance.openTekuciRacun")
    @PostMapping("/openTekuciRacun")
    public ResponseEntity<?> openTekuciRacun(@RequestBody TekuciRacunDto tekuciRacunDto) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }
        CommunicationDto response = balanceService.openTekuciRacun(tekuciRacunDto);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }

    @Timed("controllers.balance.openPoslovniRacun")
    @PostMapping("/openPoslovniRacun")
    public ResponseEntity<?> openPoslovniRacun(@RequestBody PoslovniRacunDto poslovniRacunDto) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        CommunicationDto response = balanceService.openPoslovniRacun(poslovniRacunDto);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }

}

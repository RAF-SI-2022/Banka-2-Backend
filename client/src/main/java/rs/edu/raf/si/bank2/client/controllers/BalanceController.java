package rs.edu.raf.si.bank2.client.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.client.dto.*;
import rs.edu.raf.si.bank2.client.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.client.models.mariadb.User;
import rs.edu.raf.si.bank2.client.models.mongodb.DevizniRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.PoslovniRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.TekuciRacun;
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
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public BalanceController(UserCommunicationInterface userCommunicationInterface, BalanceService balanceService) {
        this.userCommunicationInterface = userCommunicationInterface;
        this.balanceService = balanceService;
    }

    //    @GetMapping
//    public void test(){
//        System.err.println("kurac");
//
////        TekuciRacun tekuciRacun = new TekuciRacun(
////                "regNum", "ownerId", 5000.0, 5000.0, 1L, "creationDate",
////                "expDate", "USD", BalanceStatus.ACTIVE, BalanceType.STEDNI, 1, 20.0);
////        tekuciRacunRepository.save(tekuciRacun);
//
//        DevizniRacun devizniRacun =
//                new DevizniRacun("regNum", "ownerId", 5000.0, 5000.0, 1L,
//                        "creationDate", "expDate", "USD", BalanceStatus.ACTIVE, BalanceType.STEDNI,
//                        1, 20.0, true, 4);
//        devizniRacunRepository.save(devizniRacun);
//
//        PoslovniRacun poslovniRacun = new PoslovniRacun("regNum", "ownerId", 5000.0, 5000.0, 1L,
//                "creationDate", "expDate", "USD", BalanceStatus.ACTIVE, BussinessAccountType.KUPOVNI);
//        poslovniRacunRepository.save(poslovniRacun);
//    }

    @GetMapping
    public ResponseEntity<?> getAllTekuciRacuni() {
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

        return ResponseEntity.ok().body(balanceService.getAllTekuciRacuni());

//        Permission permission = user.getPermissions().get(0);
//        if (permission.getPermissionName() == PermissionName.ADMIN_USER){
//            return ResponseEntity.ok().body(balanceService.getAllTekuciRacuni());
//        }
//
//        if (user.getPermissions().size() > 1) {
//            return ResponseEntity.ok().body(balanceService.getAllTekuciRacuni());
//        } else return ResponseEntity.ok().body(balanceService.getAllDraftContractsForUserId(user.getId()));
    }

    @GetMapping
    public ResponseEntity<?> getAllPoslovniRacuni() {
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

        return ResponseEntity.ok().body(balanceService.getAllPoslovniRacuni());

//        Permission permission = user.getPermissions().get(0);
//        if (permission.getPermissionName() == PermissionName.ADMIN_USER){
//            return ResponseEntity.ok().body(balanceService.getAllTekuciRacuni());
//        }
//
//        if (user.getPermissions().size() > 1) {
//            return ResponseEntity.ok().body(balanceService.getAllTekuciRacuni());
//        } else return ResponseEntity.ok().body(balanceService.getAllDraftContractsForUserId(user.getId()));
    }

    @GetMapping
    public ResponseEntity<?> getAllDevizniRacuni() {
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

        return ResponseEntity.ok().body(balanceService.getAllDevizniRacuni());

//        Permission permission = user.getPermissions().get(0);
//        if (permission.getPermissionName() == PermissionName.ADMIN_USER){
//            return ResponseEntity.ok().body(balanceService.getAllTekuciRacuni());
//        }
//
//        if (user.getPermissions().size() > 1) {
//            return ResponseEntity.ok().body(balanceService.getAllTekuciRacuni());
//        } else return ResponseEntity.ok().body(balanceService.getAllDraftContractsForUserId(user.getId()));
    }

    @GetMapping("/{devizniRacunId}")
    public ResponseEntity<?> getDevizniRacun(@PathVariable(name = "devizniRacunId") String id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        Optional<DevizniRacun> devizniRacun = balanceService.getDevizniRacun(id);
        if (devizniRacun.isEmpty()) return ResponseEntity.status(404).body("Trazeni racun ne postoji");
        return ResponseEntity.ok().body(devizniRacun.get());
    }

    @GetMapping("/{tekuciRacunId}")
    public ResponseEntity<?> getTekuciRacun(@PathVariable(name = "tekuciRacunId") String id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        Optional<TekuciRacun> tekuciRacun = balanceService.getTekuciRacun(id);
        if (tekuciRacun.isEmpty()) return ResponseEntity.status(404).body("Trazeni racun ne postoji");
        return ResponseEntity.ok().body(tekuciRacun.get());
    }

    @GetMapping("/{poslovniRacunId}")
    public ResponseEntity<?> getPoslovniRacun(@PathVariable(name = "poslovniRacunId") String id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        Optional<PoslovniRacun> poslovniRacun = balanceService.getPoslovniRacun(id);
        if (poslovniRacun.isEmpty()) return ResponseEntity.status(404).body("Trazeni racun ne postoji");
        return ResponseEntity.ok().body(poslovniRacun.get());
    }

    @PostMapping("/openDevizniRacun")
    public ResponseEntity<?> openDevizniRacun(@RequestBody DevizniRacunDto devizniRacunDto) {
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

        BalanceDto response = balanceService.openDevizniRacun(user.getId(), devizniRacunDto);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }

    @PostMapping("/openTekuciRacun")
    public ResponseEntity<?> openTekuciRacun(@RequestBody TekuciRacunDto tekuciRacunDto) {
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

        BalanceDto response = balanceService.openTekuciRacun(user.getId(), tekuciRacunDto);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }

    @PostMapping("/openPoslovniRacun")
    public ResponseEntity<?> openPoslovniRacun(@RequestBody PoslovniRacunDto poslovniRacunDto) {
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

        BalanceDto response = balanceService.openPoslovniRacun(user.getId(), poslovniRacunDto);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }
}

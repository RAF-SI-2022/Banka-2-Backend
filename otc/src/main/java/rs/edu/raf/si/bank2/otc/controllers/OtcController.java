package rs.edu.raf.si.bank2.otc.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.otc.dto.CommunicationDto;
import rs.edu.raf.si.bank2.otc.dto.ContractDto;
import rs.edu.raf.si.bank2.otc.dto.OtcResponseDto;
import rs.edu.raf.si.bank2.otc.dto.TransactionElementDto;
import rs.edu.raf.si.bank2.otc.models.mariadb.Permission;
import rs.edu.raf.si.bank2.otc.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.otc.models.mariadb.User;
import rs.edu.raf.si.bank2.otc.models.mongodb.Contract;
import rs.edu.raf.si.bank2.otc.models.mongodb.TransactionElement;
import rs.edu.raf.si.bank2.otc.services.OtcService;
import rs.edu.raf.si.bank2.otc.services.UserCommunicationService;
import rs.edu.raf.si.bank2.otc.services.interfaces.UserCommunicationInterface;

import java.util.Optional;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

/**
 * Controller for validating tokens from other services.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/otc")
public class OtcController {

    private final OtcService otcService;
    private final UserCommunicationInterface userCommunicationInterface;
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public OtcController(OtcService otcService, UserCommunicationService communicationService) {
        this.otcService = otcService;
        this.userCommunicationInterface = communicationService;
    }


    @GetMapping
    public ResponseEntity<?> getAllContracts() {
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

        System.err.println(user);

        Permission permission = user.getPermissions().get(0);
        if (permission.getPermissionName() == PermissionName.ADMIN_USER){
            return ResponseEntity.ok().body(otcService.getAllDraftContracts());
        }

        if (user.getPermissions().size() > 1) {
            return ResponseEntity.ok().body(otcService.getAllDraftContracts());
        } else return ResponseEntity.ok().body(otcService.getAllDraftContractsForUserId(user.getId()));
    }

    @GetMapping("/byCompany/{companyId}")
    public ResponseEntity<?> getAllContracts(@PathVariable(name = "companyId") String companyId) {
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

        System.err.println(user);

        Permission permission = user.getPermissions().get(0);
        if (permission.getPermissionName() == PermissionName.ADMIN_USER){
            return ResponseEntity.ok().body(otcService.getAllContractsByCompanyId(companyId));
        }

        if (user.getPermissions().size() > 1) {
            return ResponseEntity.ok().body(otcService.getAllContractsByCompanyId(companyId));
        } else return ResponseEntity.ok().body(otcService.getAllContractsForUserIdAndCompany(user.getId(), companyId));

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getContract(@PathVariable(name = "id") String id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        Optional<Contract> contract = otcService.getContract(id);
        if (contract.isEmpty()) return ResponseEntity.status(404).body("Trazeni ugovor ne postoji");
        return ResponseEntity.ok().body(contract.get());
    }

    @PostMapping("/open")
    public ResponseEntity<?> openContract(@RequestBody ContractDto contractDto) {
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

        OtcResponseDto response = otcService.openContract(user.getId(), contractDto);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }


    @PatchMapping("/edit")
    public ResponseEntity<?> editContract(@RequestBody ContractDto contractDto) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        OtcResponseDto response = otcService.editContract(contractDto);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }


    @PatchMapping("/finalize/{id}")
    public ResponseEntity<?> finalizeContract(@PathVariable(name = "id") String id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.CREATE_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        OtcResponseDto response = otcService.closeContract(id);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteContract(@PathVariable(name = "id") String id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.CREATE_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        OtcResponseDto response = otcService.deleteContract(id);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }

    //ISPOD SU TRANSACTION ELEMENTI

    @GetMapping("/elements")
    public ResponseEntity<?> getAllElements() {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        return ResponseEntity.ok().body(otcService.getAllElements());
    }

    @GetMapping("/element/{id}")
    public ResponseEntity<?> getElement(@PathVariable(name = "id") String id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        Optional<TransactionElement> transactionElement = otcService.getElementById(id);
        if (transactionElement.isEmpty())
            return ResponseEntity.status(404).body("Ne postoji element u bazi");
        return ResponseEntity.ok().body(transactionElement.get());
    }

    @GetMapping("contract_elements/{id}")
    public ResponseEntity<?> getElementsForContract(@PathVariable(name = "id") String contractId) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        return ResponseEntity.ok().body(otcService.getElementsForContract(contractId));
    }

    @PostMapping("/add_element")
    public ResponseEntity<?> addTransactionElement(@RequestBody TransactionElementDto transactionElementDto) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        OtcResponseDto response = otcService.addTransactionElementToContract(transactionElementDto);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }


    @DeleteMapping("/remove_element/{contractId}/{elementId}")
    public ResponseEntity<?> removeTransactionElement(@PathVariable(name = "contractId") String contractId, @PathVariable(name = "elementId") String elementId) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        OtcResponseDto response = otcService.removeTransactionElement(contractId, elementId);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }

}

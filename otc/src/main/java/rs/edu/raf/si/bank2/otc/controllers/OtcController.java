package rs.edu.raf.si.bank2.otc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.otc.dto.ContractDto;
import rs.edu.raf.si.bank2.otc.dto.OtcResponseDto;
import rs.edu.raf.si.bank2.otc.dto.TransactionElementDto;
import rs.edu.raf.si.bank2.otc.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.otc.models.mongodb.Contract;
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

    @Autowired
    public OtcController(OtcService otcService, UserCommunicationService communicationService) {
        this.otcService = otcService;
        this.userCommunicationInterface = communicationService;
    }


    @GetMapping
    public ResponseEntity<?> getAllContracts(){
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        return ResponseEntity.ok().body(otcService.getAllContracts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getContract(@PathVariable(name = "id") String id){
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        Optional<Contract> contract = otcService.getContract(id);
        if (contract.isEmpty()) return ResponseEntity.status(404).body("Trazeni ugovor ne postoji");
        return ResponseEntity.ok().body(contract.get());
    }

    @PostMapping("/open")
    public ResponseEntity<?> openContract(@RequestBody ContractDto contractDto){
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        OtcResponseDto response = otcService.openContract(contractDto);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }


    @PatchMapping("/edit")
    public ResponseEntity<?> editContract(@RequestBody ContractDto contractDto){
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        OtcResponseDto response = otcService.editContract(contractDto);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }


    @DeleteMapping("/close/{id}")
    public ResponseEntity<?> closeContract(@PathVariable(name = "id") String id){
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.CREATE_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        OtcResponseDto response = otcService.deleteContract(id);
        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
    }

    //todo ispod je smece

    @GetMapping("GET_ALL")
    public ResponseEntity<?> getAllElements(){
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        return ResponseEntity.ok().body(otcService.getAllContracts());
    }

    @GetMapping("GET_ELENT")
    public ResponseEntity<?> getElement(){
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        return ResponseEntity.ok().body(otcService.getAllContracts());
    }

    @GetMapping("GET_EL_FOR_CONTR")
    public ResponseEntity<?> getElementsForContract(){
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

        return ResponseEntity.ok().body(otcService.getAllContracts());
    }

    @PostMapping("/add_element")
    public ResponseEntity<?> addTransactionElement(@RequestBody TransactionElementDto transactionElementDto){
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

//        OtcResponseDto response = otcService.editContract(contractDto);
//        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
        return null;
    }

    @PatchMapping("/edit_element")
    public ResponseEntity<?> editTransactionElement(@RequestBody TransactionElementDto transactionElementDto){
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

//        OtcResponseDto response = otcService.editContract(contractDto);
//        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
        return null;

    }

    @DeleteMapping("/remove_element/{id}")
    public ResponseEntity<?> removeTransactionElement(@PathVariable(name = "id") String id){
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu pristupa.");
        }

//        OtcResponseDto response = otcService.editContract(contractDto);
//        return ResponseEntity.status(response.getResponseCode()).body(response.getResponseMsg());
        return null;

    }

}

package rs.edu.raf.si.bank2.otc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.otc.services.OtcService;

/**
 * Controller for validating tokens from other services.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/otc")
public class OtcController {

    private final OtcService otcService;

    @Autowired
    public OtcController(OtcService otcService) {
        this.otcService = otcService;
    }

    @GetMapping
    public ResponseEntity<?> getContract(){

        return null;
    }

    @GetMapping
    public ResponseEntity<?> getAllContracts(){

        return null;
    }


    @PostMapping
    public ResponseEntity<?> openContract(){

        return null;
    }

    @PostMapping
    public ResponseEntity<?> editContract(){

        return null;
    }



    @PostMapping
    public ResponseEntity<?> closeContract(){

        return null;
    }

}

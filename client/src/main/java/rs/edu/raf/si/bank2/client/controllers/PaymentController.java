package rs.edu.raf.si.bank2.client.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.client.dto.*;
import rs.edu.raf.si.bank2.client.models.mongodb.PaymentReceiver;
import rs.edu.raf.si.bank2.client.repositories.mongodb.PaymentReceiverRepository;
import rs.edu.raf.si.bank2.client.services.PaymentService;

@RestController
@CrossOrigin
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentReceiverRepository paymentReceiverRepository;
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentReceiverRepository paymentReceiverRepository, PaymentService paymentService) {
        this.paymentReceiverRepository = paymentReceiverRepository;
        this.paymentService = paymentService;
    }


//    Payment

    @PostMapping("/makePayment")
    public ResponseEntity<?> makePayment(@RequestBody PaymentDto paymentDto) {
        //todo validacija
        CommunicationDto communicationDto = paymentService.makePayment(paymentDto);
        return ResponseEntity.status(communicationDto.getResponseCode()).body(communicationDto.getResponseMsg());
    }

    @PostMapping("/transferMoney")
    public ResponseEntity<?> transferMoney(@RequestBody TransferDto transferDto) {
        //todo validacija
        return ResponseEntity.ok(paymentService.transferMoney(transferDto));
    }

    @PostMapping("/exchangeMoney")
    public ResponseEntity<?> exchangeMoney(@RequestBody ExchangeDto exchangeDto) {
        //todo validacija
        return null;
    }


    //Payment receivers

    @PostMapping("/addReceiver")
    public ResponseEntity<?> addPaymentReceiver(@RequestBody PaymentReceiverDto paymentReceiverDto) {
        //todo verifikaija

        return ResponseEntity.ok(paymentReceiverRepository.save(new PaymentReceiver(
                paymentReceiverDto.getName(),
                paymentReceiverDto.getBalanceRegistrationNumber(),
                paymentReceiverDto.getSavedByClientId()))
        );
    }

    @GetMapping("/getReceivers/{clientId}")
    public ResponseEntity<?> getAllSavedReceiversForClient(@PathVariable String clientId) {
        //todo verifikacija
        return ResponseEntity.ok(paymentReceiverRepository.findPaymentReceiversBySavedByClientId(clientId));
    }

}

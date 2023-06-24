package rs.edu.raf.si.bank2.client.controllers;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.client.dto.*;
import rs.edu.raf.si.bank2.client.models.mongodb.PaymentReceiver;
import rs.edu.raf.si.bank2.client.repositories.mongodb.PaymentReceiverRepository;
import rs.edu.raf.si.bank2.client.repositories.mongodb.PaymentRepository;
import rs.edu.raf.si.bank2.client.services.PaymentService;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/payment")
@Timed
public class PaymentController {
    private final PaymentReceiverRepository paymentReceiverRepository;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentController(PaymentReceiverRepository paymentReceiverRepository, PaymentService paymentService, PaymentRepository paymentRepository) {
        this.paymentReceiverRepository = paymentReceiverRepository;
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }


//    Payment

    @Timed("controllers.payment.makePayment")
    @PostMapping("/makePayment")
    public ResponseEntity<?> makePayment(@RequestBody PaymentDto paymentDto) {
        CommunicationDto communicationDto = paymentService.makePayment(paymentDto);
        return ResponseEntity.status(communicationDto.getResponseCode()).body(communicationDto.getResponseMsg());
    }

    @Timed("controllers.payment.removeMoney")
    @PostMapping("/removeMoney")
    public ResponseEntity<?> removeMoney(@RequestBody RemoveMoneyDto removeMoneyDto){
        return ResponseEntity.ok( paymentService.removeMoney(removeMoneyDto));
    }

    @Timed("controllers.payment.transferMoney")
    @PostMapping("/transferMoney")
    public ResponseEntity<?> transferMoney(@RequestBody TransferDto transferDto) {
        return ResponseEntity.ok(paymentService.transferMoney(transferDto));
    }

    @Timed("controllers.payment.exchangeMoney")
    @PostMapping("/exchangeMoney")
    public ResponseEntity<?> exchangeMoney(@RequestBody ExchangeDto exchangeDto) {
        return ResponseEntity.ok(paymentService.exchangeMoney(exchangeDto));
    }

    @Timed("controllers.payment.getPaymentsForClient")
    @GetMapping("/payments/{email}")
    public ResponseEntity<?> getPaymentsForClient(@PathVariable String email){
        return ResponseEntity.ok(paymentRepository.findAllBySenderEmail(email));
    }


    //Payment receivers

    @Timed("controllers.payment.addPaymentReceiver")
    @PostMapping("/addReceiver")
    public ResponseEntity<?> addPaymentReceiver(@RequestBody PaymentReceiverDto dto) {
        //todo verifikaija
        PaymentReceiver paymentReceiver = new PaymentReceiver(
                dto.getSavedByClientEmail(), dto.getReceiverName(), dto.getBalanceRegistrationNumber(),
                dto.getReferenceNumber(), dto.getPaymentNumber(), dto.getPaymentDescription());
        return ResponseEntity.ok(paymentReceiverRepository.save(paymentReceiver));
    }

    @Timed("controllers.payment.getAllSavedReceiversForClient")
    @GetMapping("/getReceivers/{clientEmail}")
    public ResponseEntity<?> getAllSavedReceiversForClient(@PathVariable String clientEmail) {
        //todo verifikacija
        return ResponseEntity.ok(paymentReceiverRepository.findPaymentReceiversBySavedByClientEmail(clientEmail));
    }

    @Timed("controllers.payment.editReceiver")
    @PatchMapping("/editReceiver/{receiverId}")
    public ResponseEntity<?> editReceiver(@PathVariable String receiverId, @RequestBody PaymentReceiverDto dto) {
        //todo verifikacija

        Optional<PaymentReceiver> paymentReceiver = paymentReceiverRepository.findById(receiverId);
        PaymentReceiver pr = paymentReceiver.get();
        pr.setReceiverName(dto.getReceiverName());
        pr.setBalanceRegistrationNumber(dto.getBalanceRegistrationNumber());
        pr.setReferenceNumber(dto.getReferenceNumber());
        pr.setPaymentNumber(dto.getPaymentNumber());
        pr.setPaymentDescription(dto.getPaymentDescription());

        return ResponseEntity.ok(paymentReceiverRepository.save(pr));
    }

    @Timed("controllers.payment.deleteReceiver")
    @DeleteMapping("/deleteReceivers/{receiverId}")
    public ResponseEntity<?> deleteReceiver(@PathVariable String receiverId) {
        //todo verifikacija
        paymentReceiverRepository.deleteById(receiverId);
        return ResponseEntity.ok("Receiver deleted");
    }

}

package rs.edu.raf.si.bank2.client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.client.dto.PaymentDto;
import rs.edu.raf.si.bank2.client.dto.TransferDto;
import rs.edu.raf.si.bank2.client.repositories.mongodb.ClientRepository;
import rs.edu.raf.si.bank2.client.repositories.mongodb.PaymentRepository;

import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, ClientRepository clientRepository) {
        this.paymentRepository = paymentRepository;
        this.clientRepository = clientRepository;
    }


    public String makePayment(PaymentDto paymentDto){
//        Optional<>


        return null;
    }

    public String transferMoney(TransferDto transferDto){


        return null;
    }

    public void exchangeMoney(){

    }


}

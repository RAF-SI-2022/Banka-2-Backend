package rs.edu.raf.si.bank2.client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.client.dto.PaymentDto;
import rs.edu.raf.si.bank2.client.dto.TransferDto;
import rs.edu.raf.si.bank2.client.models.mongodb.RacunStorage;
import rs.edu.raf.si.bank2.client.repositories.mongodb.*;

import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ClientRepository clientRepository;
    private final TekuciRacunRepository tekuciRacunRepository;
    private final DevizniRacunRepository devizniRacunRepository;
    private final PoslovniRacunRepository poslovniRacunRepository;
    private final RacunStorageRepository racunStorageRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, ClientRepository clientRepository, TekuciRacunRepository tekuciRacunRepository, DevizniRacunRepository devizniRacunRepository, PoslovniRacunRepository poslovniRacunRepository, RacunStorageRepository racunStorageRepository) {
        this.paymentRepository = paymentRepository;
        this.clientRepository = clientRepository;
        this.tekuciRacunRepository = tekuciRacunRepository;
        this.devizniRacunRepository = devizniRacunRepository;
        this.poslovniRacunRepository = poslovniRacunRepository;
        this.racunStorageRepository = racunStorageRepository;
    }


    public String makePayment(PaymentDto paymentDto){
        Optional<RacunStorage> fromRacunInfo = racunStorageRepository.findRacunStorageByBalanceRegistrationNumberAndType(paymentDto.getToBalanceRegNum(), paymentDto.getType());
        Optional<RacunStorage> toRacunInfo = racunStorageRepository.findRacunStorageByBalanceRegistrationNumberAndType(paymentDto.getFromBalanceRegNum(), paymentDto.getType());


        return null;
    }

    public String transferMoney(TransferDto transferDto){


        return null;
    }

    public void exchangeMoney(){

    }


}

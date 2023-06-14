package rs.edu.raf.si.bank2.client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.client.dto.CommunicationDto;
import rs.edu.raf.si.bank2.client.dto.PaymentDto;
import rs.edu.raf.si.bank2.client.dto.TransferDto;
import rs.edu.raf.si.bank2.client.models.mongodb.*;
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


    public CommunicationDto makePayment(PaymentDto paymentDto) {
        Optional<RacunStorage> fromRacunInfo = racunStorageRepository.findRacunStorageByBalanceRegistrationNumberAndType(paymentDto.getToBalanceRegNum(), paymentDto.getType());
        Optional<RacunStorage> toRacunInfo = racunStorageRepository.findRacunStorageByBalanceRegistrationNumberAndType(paymentDto.getFromBalanceRegNum(), paymentDto.getType());

        //todo mozda da se skloni provera za tudji racun
        if (fromRacunInfo.isEmpty()) return new CommunicationDto(404, "Klijentski racun nije pronadjen");
        if (toRacunInfo.isEmpty()) return new CommunicationDto(404, "Razun na koji saljete nije pronadjen");

        switch (paymentDto.getType()) {
            case TEKUCI -> payFromTekuci(paymentDto.getFromBalanceRegNum(), paymentDto.getAmount());
            case DEVIZNI -> payFromPoslovni(paymentDto.getFromBalanceRegNum(), paymentDto.getAmount());
            case POSLOVNI -> payFromDevizni(paymentDto.getFromBalanceRegNum(), paymentDto.getAmount());
        }

        switch (toRacunInfo.get().getType()) {
            case TEKUCI -> addToTekuci(paymentDto.getFromBalanceRegNum(), paymentDto.getAmount());
            case DEVIZNI -> addToDevizni(paymentDto.getFromBalanceRegNum(), paymentDto.getAmount());
            case POSLOVNI -> addToPoslovni(paymentDto.getFromBalanceRegNum(), paymentDto.getAmount());
        }

        paymentRepository.save(new Payment(
                paymentDto.getSenderId(), paymentDto.getReceiverName(), paymentDto.getFromBalanceRegNum(),
                paymentDto.getToBalanceRegNum(), paymentDto.getAmount(), paymentDto.getReferenceNumber(),
                paymentDto.getPaymentNumber(), paymentDto.getPaymentDescription()
        ));

        return new CommunicationDto(200, "Placanje uspesno izvrseno");
    }

    //todo U OVIM PLACANJIMA TREBA DA SE STAVI NA NEKI WAIT ILI NESTO ZA AVAILABLE
    //todo uradi verifikaciju da li imamo dovoljno para
    private void payFromTekuci(String balanceRegNm, Double amountToReduce) {
        System.err.println("usli smo u pay");
        Optional<TekuciRacun> tekuciRacun = tekuciRacunRepository.findTekuciRacunByRegistrationNumber(balanceRegNm);
        TekuciRacun tekuciRacun1 = tekuciRacun.get();
        System.err.println(tekuciRacun.get());
//        tekuciRacun.get().setBalance(tekuciRacun.get().getBalance() - amountToReduce);
//        tekuciRacun.get().setAvailableBalance(tekuciRacun.get().getAvailableBalance() - amountToReduce);
//        System.err.println(tekuciRacun.get());
        tekuciRacun1.setBalance(2222.00);
        tekuciRacun1.setAvailableBalance(2222.00);
        tekuciRacunRepository.save(tekuciRacun1);
    }

    private void payFromDevizni(String balanceRegNm, Double amountToReduce) {
        Optional<DevizniRacun> devizniRacun = devizniRacunRepository.findDevizniRacunByRegistrationNumber(balanceRegNm);
        devizniRacun.get().setBalance(devizniRacun.get().getBalance() - amountToReduce);
        devizniRacun.get().setAvailableBalance(devizniRacun.get().getAvailableBalance() - amountToReduce);
        devizniRacunRepository.save(devizniRacun.get());
    }

    private void payFromPoslovni(String balanceRegNm, Double amountToReduce) {
        Optional<PoslovniRacun> poslovniRacun = poslovniRacunRepository.findPoslovniRacunByRegistrationNumber(balanceRegNm);
        poslovniRacun.get().setBalance(poslovniRacun.get().getBalance() - amountToReduce);
        poslovniRacun.get().setAvailableBalance(poslovniRacun.get().getAvailableBalance() - amountToReduce);
        poslovniRacunRepository.save(poslovniRacun.get());
    }

    private void addToTekuci(String balanceRegNm, Double amountToIncrease) {
        System.err.println("usli smo u add");
        Optional<TekuciRacun> tekuciRacun = tekuciRacunRepository.findTekuciRacunByRegistrationNumber(balanceRegNm);
        tekuciRacun.get().setBalance(tekuciRacun.get().getBalance() + amountToIncrease);
        tekuciRacun.get().setAvailableBalance(tekuciRacun.get().getAvailableBalance() + amountToIncrease);
        tekuciRacunRepository.save(tekuciRacun.get());
    }

    private void addToDevizni(String balanceRegNm, Double amountToIncrease) {
        Optional<DevizniRacun> devizniRacun = devizniRacunRepository.findDevizniRacunByRegistrationNumber(balanceRegNm);
        devizniRacun.get().setBalance(devizniRacun.get().getBalance() + amountToIncrease);
        devizniRacun.get().setAvailableBalance(devizniRacun.get().getAvailableBalance() + amountToIncrease);
        devizniRacunRepository.save(devizniRacun.get());
    }

    private void addToPoslovni(String balanceRegNm, Double amountToIncrease) {
        Optional<PoslovniRacun> poslovniRacun = poslovniRacunRepository.findPoslovniRacunByRegistrationNumber(balanceRegNm);
        poslovniRacun.get().setBalance(poslovniRacun.get().getBalance() + amountToIncrease);
        poslovniRacun.get().setAvailableBalance(poslovniRacun.get().getAvailableBalance() + amountToIncrease);
        poslovniRacunRepository.save(poslovniRacun.get());
    }


    public String transferMoney(TransferDto transferDto) {


        return null;
    }

    public void exchangeMoney() {

    }


}

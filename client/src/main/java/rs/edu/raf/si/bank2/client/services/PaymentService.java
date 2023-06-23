package rs.edu.raf.si.bank2.client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.client.dto.CommunicationDto;
import rs.edu.raf.si.bank2.client.dto.ExchangeDto;
import rs.edu.raf.si.bank2.client.dto.PaymentDto;
import rs.edu.raf.si.bank2.client.dto.TransferDto;
import rs.edu.raf.si.bank2.client.models.mongodb.*;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.Balance;
import rs.edu.raf.si.bank2.client.repositories.mongodb.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ClientRepository clientRepository;
    private final TekuciRacunRepository tekuciRacunRepository;
    private final DevizniRacunRepository devizniRacunRepository;
    private final PoslovniRacunRepository poslovniRacunRepository;
    private final RacunStorageRepository racunStorageRepository;
    private final Map<String, Double> exchangeRates;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, ClientRepository clientRepository, TekuciRacunRepository tekuciRacunRepository, DevizniRacunRepository devizniRacunRepository, PoslovniRacunRepository poslovniRacunRepository, RacunStorageRepository racunStorageRepository) {
        this.paymentRepository = paymentRepository;
        this.clientRepository = clientRepository;
        this.tekuciRacunRepository = tekuciRacunRepository;
        this.devizniRacunRepository = devizniRacunRepository;
        this.poslovniRacunRepository = poslovniRacunRepository;
        this.racunStorageRepository = racunStorageRepository;
        this.exchangeRates = new HashMap<>();
    }


    public CommunicationDto makePayment(PaymentDto paymentDto) {
        Optional<RacunStorage> fromRacunInfo = racunStorageRepository.findRacunStorageByBalanceRegistrationNumber(paymentDto.getFromBalanceRegNum());
        Optional<RacunStorage> toRacunInfo = racunStorageRepository.findRacunStorageByBalanceRegistrationNumber(paymentDto.getToBalanceRegNum());

        //todo mozda da se skloni provera za tudji racun
        if (fromRacunInfo.isEmpty()) return new CommunicationDto(404, "Klijentski racun nije pronadjen");
        if (toRacunInfo.isEmpty()) return new CommunicationDto(404, "Razun na koji saljete nije pronadjen");

        subtract(fromRacunInfo.get().getType(), fromRacunInfo.get().getBalanceRegistrationNumber(), paymentDto.getAmount());
        add(toRacunInfo.get().getType(), toRacunInfo.get().getBalanceRegistrationNumber(), paymentDto.getAmount());

        paymentRepository.save(new Payment(paymentDto.getSenderEmail(), paymentDto.getReceiverName(),
                fromRacunInfo.get().getBalanceRegistrationNumber(), toRacunInfo.get().getBalanceRegistrationNumber(), paymentDto.getAmount(), paymentDto.getReferenceNumber(),
                paymentDto.getPaymentNumber(), paymentDto.getPaymentDescription()));

        return new CommunicationDto(200, "Placanje uspesno izvrseno");
    }


    //todo U OVIM PLACANJIMA TREBA DA SE STAVI NA NEKI WAIT ILI NESTO ZA AVAILABLE
    //todo uradi verifikaciju da li imamo dovoljno para

    private void subtract(Balance type, String regNum, double amountToReduce) {
        switch (type) {
            case TEKUCI -> {
                Optional<TekuciRacun> tekuciRacun = tekuciRacunRepository.findTekuciRacunByRegistrationNumber(regNum);
                double newValue = tekuciRacun.get().getBalance() - amountToReduce;
                tekuciRacun.get().setBalance(newValue);
                tekuciRacun.get().setAvailableBalance(newValue);
                tekuciRacunRepository.save(tekuciRacun.get());
            }
            case DEVIZNI -> {
                Optional<DevizniRacun> devizniRacun = devizniRacunRepository.findDevizniRacunByRegistrationNumber(regNum);
                double newValue = devizniRacun.get().getBalance() - amountToReduce;
                devizniRacun.get().setBalance(newValue);
                devizniRacun.get().setAvailableBalance(newValue);
                devizniRacunRepository.save(devizniRacun.get());
            }
            case POSLOVNI -> {
                Optional<PoslovniRacun> poslovniRacun = poslovniRacunRepository.findPoslovniRacunByRegistrationNumber(regNum);
                double newValue = poslovniRacun.get().getBalance() - amountToReduce;
                poslovniRacun.get().setBalance(newValue);
                poslovniRacun.get().setAvailableBalance(newValue);
                poslovniRacunRepository.save(poslovniRacun.get());
            }
        }
    }

    private void add(Balance type, String regNum, double amountToIncrease) {
        switch (type) {
            case TEKUCI -> {
                Optional<TekuciRacun> tekuciRacun = tekuciRacunRepository.findTekuciRacunByRegistrationNumber(regNum);
                double newValue = tekuciRacun.get().getBalance() + amountToIncrease;
                tekuciRacun.get().setBalance(newValue);
                tekuciRacun.get().setAvailableBalance(newValue);
                tekuciRacunRepository.save(tekuciRacun.get());
            }
            case DEVIZNI -> {
                Optional<DevizniRacun> devizniRacun = devizniRacunRepository.findDevizniRacunByRegistrationNumber(regNum);
                double newValue = devizniRacun.get().getBalance() + amountToIncrease;
                devizniRacun.get().setBalance(newValue);
                devizniRacun.get().setAvailableBalance(newValue);
                devizniRacunRepository.save(devizniRacun.get());
            }
            case POSLOVNI -> {
                Optional<PoslovniRacun> poslovniRacun = poslovniRacunRepository.findPoslovniRacunByRegistrationNumber(regNum);
                double newValue = poslovniRacun.get().getBalance() + amountToIncrease;
                poslovniRacun.get().setBalance(newValue);
                poslovniRacun.get().setAvailableBalance(newValue);
                poslovniRacunRepository.save(poslovniRacun.get());
            }
        }
    }

    //todo mozda postaviti check a mozda ne
    public CommunicationDto transferMoney(TransferDto transferDto) {
        Optional<RacunStorage> fromRacunInfo = racunStorageRepository.findRacunStorageByBalanceRegistrationNumber(transferDto.getFromBalanceRegNum());
        Optional<RacunStorage> toRacunInfo = racunStorageRepository.findRacunStorageByBalanceRegistrationNumber(transferDto.getToBalanceRegNum());
        if (fromRacunInfo.isEmpty()) return new CommunicationDto(404, "Klijentski racun nije pronadjen");
        if (toRacunInfo.isEmpty()) return new CommunicationDto(404, "Razun na koji saljete nije pronadjen");

        subtract(fromRacunInfo.get().getType(), fromRacunInfo.get().getBalanceRegistrationNumber(), transferDto.getAmount());
        add(toRacunInfo.get().getType(), toRacunInfo.get().getBalanceRegistrationNumber(), transferDto.getAmount());

        return new CommunicationDto(200, "Placanje uspesno izvrseno");
    }


    public CommunicationDto exchangeMoney(ExchangeDto exchangeDto) {
        Optional<RacunStorage> fromRacunInfo = racunStorageRepository.findRacunStorageByBalanceRegistrationNumber(exchangeDto.getFromBalanceRegNum());
        Optional<RacunStorage> toRacunInfo = racunStorageRepository.findRacunStorageByBalanceRegistrationNumber(exchangeDto.getToBalanceRegNum());
        if (fromRacunInfo.isEmpty()) return new CommunicationDto(404, "Klijentski racun nije pronadjen");
        if (toRacunInfo.isEmpty()) return new CommunicationDto(404, "Razun na koji saljete nije pronadjen");

        double rate = exchangeRates.get(exchangeDto.getExchange());
        double newAmount = exchangeDto.getAmount() * rate;

        subtract(fromRacunInfo.get().getType(), fromRacunInfo.get().getBalanceRegistrationNumber(), exchangeDto.getAmount());
        add(toRacunInfo.get().getType(), toRacunInfo.get().getBalanceRegistrationNumber(), newAmount);

        return new CommunicationDto(200, "Placanje uspesno izvrseno");
    }

    public Map<String, Double> getExchangeRates() {
        return exchangeRates;
    }
}

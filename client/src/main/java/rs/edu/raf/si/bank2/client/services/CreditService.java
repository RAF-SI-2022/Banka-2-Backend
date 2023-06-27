package rs.edu.raf.si.bank2.client.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.client.dto.CommunicationDto;
import rs.edu.raf.si.bank2.client.dto.CreditDto;
import rs.edu.raf.si.bank2.client.dto.CreditRequestDto;
import rs.edu.raf.si.bank2.client.models.mongodb.*;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.CreditApproval;
import rs.edu.raf.si.bank2.client.repositories.mongodb.*;

@Service
public class CreditService {

    private final CreditRepository creditRepository;
    private final CreditRequestRepository creditRequestRepository;
    private final PayedInterestRepository payedInterestRepository;
    private final TekuciRacunRepository tekuciRacunRepository;
    private final DevizniRacunRepository devizniRacunRepository;
    private final PoslovniRacunRepository poslovniRacunRepository;
    private final RacunStorageRepository racunStorageRepository;

    @Autowired
    public CreditService(
            CreditRepository creditRepository,
            CreditRequestRepository creditRequestRepository,
            PayedInterestRepository payedInterestRepository,
            TekuciRacunRepository tekuciRacunRepository,
            DevizniRacunRepository devizniRacunRepository,
            PoslovniRacunRepository poslovniRacunRepository,
            RacunStorageRepository racunStorageRepository) {
        this.creditRepository = creditRepository;
        this.creditRequestRepository = creditRequestRepository;
        this.payedInterestRepository = payedInterestRepository;
        this.tekuciRacunRepository = tekuciRacunRepository;
        this.devizniRacunRepository = devizniRacunRepository;
        this.poslovniRacunRepository = poslovniRacunRepository;
        this.racunStorageRepository = racunStorageRepository;
    }

    // credit

    public List<Credit> getCreditsAllForClient(String clientEmail) {
        return creditRepository.findAllByClientEmail(clientEmail);
    }

    public CommunicationDto payOffOneMonthsInterest(String creditId) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Optional<Credit> credit = creditRepository.findById(creditId);
        if (credit.isEmpty()) return new CommunicationDto(404, "Kredit nije nadjen");
        double monthlyRate = credit.get().getMonthlyRate();

        increaseOrDecreaseUserBalance(monthlyRate, credit.get().getAccountRegNumber(), true);
        credit.get().setRemainingAmount(credit.get().getRemainingAmount() - monthlyRate);
        creditRepository.save(credit.get());
        payedInterestRepository.save(new PayedInterest("Kamata", creditId, dtf.format(LocalDate.now()), monthlyRate));

        return new CommunicationDto(200, "Mesecna kamata je placena");
    }

    // request

    public CreditRequest saveRequest(CreditRequestDto dto) {
        return creditRequestRepository.save(new CreditRequest(
                dto.getClientEmail(),
                CreditApproval.WAITING,
                dto.getAmount(),
                dto.getUsedFor(),
                dto.getMonthlyRate(),
                dto.getClientHasJob(),
                dto.getJobLocation(),
                dto.getCurrentJobDuration(),
                dto.getDueDateInMonths(),
                dto.getPhoneNumber()));
    }

    public CommunicationDto approveCredit(String creditId, CreditDto dto) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Optional<CreditRequest> creditRequest = creditRequestRepository.findById(creditId);
        if (creditRequest.isEmpty()) return new CommunicationDto(404, "Request requested not found");
        if (creditRequest.get().getCreditApproval() != CreditApproval.WAITING)
            return new CommunicationDto(500, "Zahtev nije na cekanju");
        creditRequest.get().setCreditApproval(CreditApproval.APPROVED);
        creditRequestRepository.save(creditRequest.get());

        double monthlyRate = (dto.getRatePercentage() / 100.0) * dto.getAmount();

        Credit credit = new Credit(
                dto.getClientEmail(),
                dto.getName(),
                dto.getAccountRegNumber(),
                dto.getAmount(),
                dto.getAmount(),
                dto.getRatePercentage(),
                monthlyRate, // todo porveri dal je ok
                dtf.format(LocalDate.now()),
                dto.getDueDate(),
                dto.getCurrency());
        creditRepository.save(credit);

        increaseOrDecreaseUserBalance(dto.getAmount(), dto.getAccountRegNumber(), false);
        return new CommunicationDto(200, "Kredit je dozvoljen i novac je prosledjen");
    }

    private void increaseOrDecreaseUserBalance(Double amount, String racunRegNum, boolean decrease) {
        Optional<RacunStorage> racunInfo =
                racunStorageRepository.findRacunStorageByBalanceRegistrationNumber(racunRegNum);
        if (racunInfo.isEmpty()) System.err.println("NIJE GA NASAO");

        double amountToChange = amount;
        if (decrease) amountToChange = -amount;

        switch (racunInfo.get().getType()) {
            case TEKUCI -> {
                Optional<TekuciRacun> tekuciRacun =
                        tekuciRacunRepository.findTekuciRacunByRegistrationNumber(racunRegNum);
                double newValue = tekuciRacun.get().getBalance() + amountToChange;
                tekuciRacun.get().setBalance(newValue);
                tekuciRacun.get().setAvailableBalance(newValue);
                tekuciRacunRepository.save(tekuciRacun.get());
            }
            case DEVIZNI -> {
                Optional<DevizniRacun> devizniRacun =
                        devizniRacunRepository.findDevizniRacunByRegistrationNumber(racunRegNum);
                double newValue = devizniRacun.get().getBalance() + amountToChange;
                devizniRacun.get().setBalance(newValue);
                devizniRacun.get().setAvailableBalance(newValue);
                devizniRacunRepository.save(devizniRacun.get());
            }
            case POSLOVNI -> {
                Optional<PoslovniRacun> poslovniRacun =
                        poslovniRacunRepository.findPoslovniRacunByRegistrationNumber(racunRegNum);
                double newValue = poslovniRacun.get().getBalance() + amountToChange;
                poslovniRacun.get().setBalance(newValue);
                poslovniRacun.get().setAvailableBalance(newValue);
                poslovniRacunRepository.save(poslovniRacun.get());
            }
        }
    }

    public CommunicationDto denyCredit(String creditId) {
        Optional<CreditRequest> creditRequest = creditRequestRepository.findById(creditId);
        if (creditRequest.isEmpty()) return new CommunicationDto(404, "Request requested not found");
        if (creditRequest.get().getCreditApproval() != CreditApproval.WAITING)
            return new CommunicationDto(500, "Zahtev nije na cekanju");
        creditRequest.get().setCreditApproval(CreditApproval.DENIED);
        creditRequestRepository.save(creditRequest.get());
        return new CommunicationDto(200, "Kredit je odbijen");
    }
}

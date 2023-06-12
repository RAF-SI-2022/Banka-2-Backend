package rs.edu.raf.si.bank2.client.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.client.dto.*;
import rs.edu.raf.si.bank2.client.models.mongodb.DevizniRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.PoslovniRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.TekuciRacun;
import rs.edu.raf.si.bank2.client.repositories.mongodb.DevizniRacunRepository;
import rs.edu.raf.si.bank2.client.repositories.mongodb.PoslovniRacunRepository;
import rs.edu.raf.si.bank2.client.repositories.mongodb.TekuciRacunRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class BalanceService {

    private final TekuciRacunRepository tekuciRacunRepository;
    private final DevizniRacunRepository devizniRacunRepository;
    private final PoslovniRacunRepository poslovniRacunRepository;

    @Autowired
    public BalanceService(TekuciRacunRepository tekuciRacunRepository, DevizniRacunRepository devizniRacunRepository, PoslovniRacunRepository poslovniRacunRepository) {
        this.tekuciRacunRepository = tekuciRacunRepository;
        this.devizniRacunRepository = devizniRacunRepository;
        this.poslovniRacunRepository = poslovniRacunRepository;
    }

    public Optional<DevizniRacun> getDevizniRacun(String id) {
        return devizniRacunRepository.findById(id);
    }

    public Optional<TekuciRacun> getTekuciRacun(String id) {
        return tekuciRacunRepository.findById(id);
    }

    public Optional<PoslovniRacun> getPoslovniRacun(String id) {
        return poslovniRacunRepository.findById(id);
    }

    public List<TekuciRacun> getAllTekuciRacuni() {
        return tekuciRacunRepository.findAll();
    }
    public List<DevizniRacun> getAllDevizniRacuni() {
        return devizniRacunRepository.findAll();
    }
    public List<PoslovniRacun> getAllPoslovniRacuni() {
        return poslovniRacunRepository.findAll();}


    public BalanceDto openDevizniRacun(String ownerId, DevizniRacunDto devizniRacunDto) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate now = LocalDate.now();
        DevizniRacun newDevizniRacun = new DevizniRacun(devizniRacunDto.getRegistrationNumber(), ownerId,
                devizniRacunDto.getBalance(), devizniRacunDto.getAvailableBalance(), devizniRacunDto.getAssignedAgentId(),
                dtf.format(now), null, devizniRacunDto.getCurrency(), devizniRacunDto.getBalanceStatus(),
                devizniRacunDto.getBalanceType(), devizniRacunDto.getInterestRatePercentage(), devizniRacunDto.getAccountMaintenance(),
                devizniRacunDto.getDefaultCurrency(), devizniRacunDto.getAllowedNumOfCurrencies());
        devizniRacunRepository.save(newDevizniRacun);

        return new BalanceDto(200, "Devizni racun uspeno napravljen.");
    }
    public BalanceDto openTekuciRacun(String ownerId, TekuciRacunDto tekuciRacunDto) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate now = LocalDate.now();
        TekuciRacun newTekuciRacun = new TekuciRacun(tekuciRacunDto.getRegistrationNumber(), ownerId,
                tekuciRacunDto.getBalance(), tekuciRacunDto.getAvailableBalance(), tekuciRacunDto.getAssignedAgentId(),
                dtf.format(now), null, tekuciRacunDto.getCurrency(), tekuciRacunDto.getBalanceStatus(),
                tekuciRacunDto.getBalanceType(), tekuciRacunDto.getInterestRatePercentage(), tekuciRacunDto.getAccountMaintenance());
        tekuciRacunRepository.save(newTekuciRacun);

        return new BalanceDto(200, "Tekuci racun uspeno napravljen.");
    }
    public BalanceDto openPoslovniRacun(String ownerId, PoslovniRacunDto poslovniRacunDto) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate now = LocalDate.now();
        PoslovniRacun newPoslovniRacun = new PoslovniRacun(poslovniRacunDto.getRegistrationNumber(), ownerId,
                poslovniRacunDto.getBalance(), poslovniRacunDto.getAvailableBalance(), poslovniRacunDto.getAssignedAgentId(),
                dtf.format(now), null, poslovniRacunDto.getCurrency(), poslovniRacunDto.getBalanceStatus(),
                poslovniRacunDto.getBussinessAccountType());
        poslovniRacunRepository.save(newPoslovniRacun);

        return new BalanceDto(200, "Poslovni racun uspeno napravljen.");
    }
    //TODO ubaciti edit i delete opcije
}

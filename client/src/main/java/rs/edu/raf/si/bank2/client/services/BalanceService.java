package rs.edu.raf.si.bank2.client.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.client.dto.*;
import rs.edu.raf.si.bank2.client.models.mongodb.DevizniRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.PoslovniRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.TekuciRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BalanceStatus;
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


    public BalanceDto openDevizniRacun(DevizniRacunDto devizniRacunDto) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate now = LocalDate.now();
        DevizniRacun newDevizniRacun = new DevizniRacun("69696969", devizniRacunDto.getOwnerId(),
                5000.0, 5000.0, devizniRacunDto.getAssignedAgentId(),
                dtf.format(now), null, devizniRacunDto.getCurrency(), BalanceStatus.ACTIVE,
                devizniRacunDto.getBalanceType(), devizniRacunDto.getInterestRatePercentage(), devizniRacunDto.getAccountMaintenance(),
                devizniRacunDto.getDefaultCurrency(), devizniRacunDto.getAllowedNumOfCurrencies());
        devizniRacunRepository.save(newDevizniRacun);

        return new BalanceDto(200, "Devizni racun uspeno napravljen.");
    }
    public BalanceDto openTekuciRacun(TekuciRacunDto tekuciRacunDto) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate now = LocalDate.now();
        TekuciRacun newTekuciRacun = new TekuciRacun("69696969", tekuciRacunDto.getOwnerId(),
                5000.0, 5000.0, tekuciRacunDto.getAssignedAgentId(),
                dtf.format(now), null, tekuciRacunDto.getCurrency(), BalanceStatus.ACTIVE,
                tekuciRacunDto.getBalanceType(), tekuciRacunDto.getInterestRatePercentage(), tekuciRacunDto.getAccountMaintenance());
        tekuciRacunRepository.save(newTekuciRacun);

        return new BalanceDto(200, "Tekuci racun uspeno napravljen.");
    }
    public BalanceDto openPoslovniRacun(PoslovniRacunDto poslovniRacunDto) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate now = LocalDate.now();
        PoslovniRacun newPoslovniRacun = new PoslovniRacun("69696969", poslovniRacunDto.getOwnerId(),
                5000.0, 5000.0, poslovniRacunDto.getAssignedAgentId(),
                dtf.format(now), null, poslovniRacunDto.getCurrency(),BalanceStatus.ACTIVE,
                poslovniRacunDto.getBussinessAccountType());
        poslovniRacunRepository.save(newPoslovniRacun);

        return new BalanceDto(200, "Poslovni racun uspeno napravljen.");
    }
    //TODO ubaciti edit i delete opcije
}

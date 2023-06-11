package rs.edu.raf.si.bank2.client.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.edu.raf.si.bank2.client.models.mongodb.DevizniRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.PoslovniRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.TekuciRacun;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BalanceStatus;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BalanceType;
import rs.edu.raf.si.bank2.client.models.mongodb.enums.BussinessAccountType;
import rs.edu.raf.si.bank2.client.repositories.mongodb.DevizniRacunRepository;
import rs.edu.raf.si.bank2.client.repositories.mongodb.PoslovniRacunRepository;
import rs.edu.raf.si.bank2.client.repositories.mongodb.TekuciRacunRepository;

@RestController
@CrossOrigin
@RequestMapping("/api/balance")
public class BalanceController {

    private final TekuciRacunRepository tekuciRacunRepository;
    private final DevizniRacunRepository devizniRacunRepository;
    private final PoslovniRacunRepository poslovniRacunRepository;

    @Autowired
    public BalanceController(TekuciRacunRepository tekuciRacunRepository, DevizniRacunRepository devizniRacunRepository, PoslovniRacunRepository poslovniRacunRepository) {
        this.tekuciRacunRepository = tekuciRacunRepository;
        this.devizniRacunRepository = devizniRacunRepository;
        this.poslovniRacunRepository = poslovniRacunRepository;
    }



    @GetMapping
    public void test(){
        System.err.println("kurac");

//        TekuciRacun tekuciRacun = new TekuciRacun(
//                "regNum", "ownerId", 5000.0, 5000.0, 1L, "creationDate",
//                "expDate", "USD", BalanceStatus.ACTIVE, BalanceType.STEDNI, 1, 20.0);
//        tekuciRacunRepository.save(tekuciRacun);

        DevizniRacun devizniRacun =
                new DevizniRacun("regNum", "ownerId", 5000.0, 5000.0, 1L,
                        "creationDate", "expDate", "USD", BalanceStatus.ACTIVE, BalanceType.STEDNI,
                        1, 20.0, true, 4);
        devizniRacunRepository.save(devizniRacun);

        PoslovniRacun poslovniRacun = new PoslovniRacun("regNum", "ownerId", 5000.0, 5000.0, 1L,
                "creationDate", "expDate", "USD", BalanceStatus.ACTIVE, BussinessAccountType.KUPOVNI);
        poslovniRacunRepository.save(poslovniRacun);
    }

}

package rs.edu.raf.si.bank2.main.controllers;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.bank2.main.models.mariadb.Balance;
import rs.edu.raf.si.bank2.main.models.mariadb.Future;
import rs.edu.raf.si.bank2.main.models.mariadb.PermissionName;
import rs.edu.raf.si.bank2.main.models.mariadb.User;
import rs.edu.raf.si.bank2.main.requests.FutureRequestBuySell;
import rs.edu.raf.si.bank2.main.services.*;
import rs.edu.raf.si.bank2.main.services.interfaces.UserCommunicationInterface;

@RestController
@CrossOrigin
@RequestMapping("/api/futures")
public class FutureController {

    private final AuthorisationService authorisationService;
    private final FutureService futureService;
    private final BalanceService balanceService;
    private final UserService userService;
    private final UserCommunicationInterface userCommunicationInterface;

    @Autowired
    public FutureController(
            AuthorisationService authorisationService,
            FutureService futureService,
            BalanceService balanceService,
            UserService userService,
            UserCommunicationService communicationService) {
        this.userCommunicationInterface = communicationService;
        this.authorisationService = authorisationService;
        this.futureService = futureService;
        this.balanceService = balanceService;
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<?> findAll() {
        String signedInUserEmail = getContext().getAuthentication().getName(); // todo dodaj nove perms
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da pristupite terminskeim ugovorima.");
        }
        return ResponseEntity.ok().body(futureService.findAll());
    }

    @GetMapping(value = "/{futureId}")
    public ResponseEntity<?> findById(@PathVariable(name = "futureId") Long id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da pristupite terminskeim ugovorima.");
        }

        return ResponseEntity.ok().body(futureService.findById(id));
    }

    @GetMapping(value = "/name/{name}")
    public ResponseEntity<?> findFuturesByName(@PathVariable(name = "name") String futureName) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da pristupite terminskeim ugovorima.");
        }
        return ResponseEntity.ok().body(futureService.findFuturesByFutureName(futureName));
    }

    @PostMapping(value = "/buy")
    public ResponseEntity<?> buyFuture(@RequestBody FutureRequestBuySell futureRequest) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da kupujete terminske ugovore.");
        }
        Optional<User> user = userService.findByEmail(signedInUserEmail);
        if (futureRequest.getCurrencyCode() == null
                || futureRequest.getCurrencyCode().equals("")) {
            futureRequest.setCurrencyCode(
                    "USD"); // TODO: this is only for testing because front doesn't send currencyCode yet - remove this
            // if later.
        }
        Balance usersBalance =
                balanceService.findBalanceByUserIdAndCurrency(user.get().getId(), futureRequest.getCurrencyCode());
        if (usersBalance == null) {
            return ResponseEntity.badRequest()
                    .body("Balance for user with id <" + user.get().getId() + "> and currency code "
                            + futureRequest.getCurrencyCode() + " has not been found.");
        }
        futureRequest.setUserId(user.get().getId());
        return futureService.buyFuture(futureRequest, signedInUserEmail, usersBalance.getFree());
    }

    @PostMapping(value = "/sell")
    public ResponseEntity<?> sellFuture(@RequestBody FutureRequestBuySell futureRequest) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da pristupite terminskeim ugovorima.");
        }

        Optional<User> user = userService.findByEmail(signedInUserEmail);
        Optional<Future> future = futureService.findById(futureRequest.getId());
        if (future.get().getUser().getId() != user.get().getId()) {
            return ResponseEntity.status(401).body("Nemate dozvolu da modifikujete terminskim ugovorom.");
        }

        futureRequest.setUserId(user.get().getId());
        return futureService.sellFuture(futureRequest);
    }

    @PostMapping(value = "/remove/{id}")
    public ResponseEntity<?> removeFromMarket(@PathVariable(name = "id") Long id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da skinete terminski ugovor sa marketa.");
        }

        Optional<User> user = userService.findByEmail(signedInUserEmail);
        Optional<Future> future = futureService.findById(id);
        if (future.get().getUser().getId() != user.get().getId()) {
            return ResponseEntity.status(401).body("Nemate dozvolu da skinete terminski ugovor sa marketa.");
        }

        return futureService.removeFromMarket(id);
    }

    @PostMapping(value = "/remove-waiting-sell/{id}")
    public ResponseEntity<?> removeWaitingSellFutures(@PathVariable(name = "id") Long id) {
        String signedInUserEmail = getContext().getAuthentication().getName();

        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da pristupite terminskim ugovorima");
        }
        return futureService.removeWaitingSellFuture(id);
    }

    @PostMapping(value = "/remove-waiting-buy/{id}")
    public ResponseEntity<?> removeWaitingBuyFutures(@PathVariable(name = "id") Long id) {
        String signedInUserEmail = getContext().getAuthentication().getName();

        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da pristupite terminskim ugovorima");
        }
        return futureService.removeWaitingBuyFuture(id);
    }

    @GetMapping(value = "waiting-futures/{type}/{futureName}")
    public ResponseEntity<?> getAllWaitingFuturesForUser(
            @PathVariable(name = "type") String type, @PathVariable(name = "futureName") String futureName) {
        String signedInUserEmail = getContext().getAuthentication().getName(); // todo dodaj nove perms
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da pristupite terminskeim ugovorima.");
        }
        Optional<User> user = userService.findByEmail(signedInUserEmail);
        if (user.isPresent()) {
            return ResponseEntity.ok()
                    .body(futureService.getWaitingFuturesForUser(user.get().getId(), type, futureName));
        }

        return ResponseEntity.status(500).body("Doslo je do neocekivane greske.");
    }

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<?> findByUserId(@PathVariable(name = "userId") Long id) {
        String signedInUserEmail = getContext().getAuthentication().getName();
        if (!userCommunicationInterface.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
            return ResponseEntity.status(401).body("Nemate dozvolu da pristupite terminskeim ugovorima.");
        }

        return ResponseEntity.ok().body(futureService.findFuturesByUserId(id));
    }
}

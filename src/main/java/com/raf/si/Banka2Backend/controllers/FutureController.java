package com.raf.si.Banka2Backend.controllers;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import com.raf.si.Banka2Backend.models.mariadb.Future;
import com.raf.si.Banka2Backend.models.mariadb.PermissionName;
import com.raf.si.Banka2Backend.models.mariadb.User;
import com.raf.si.Banka2Backend.requests.FutureRequestBuySell;
import com.raf.si.Banka2Backend.services.AuthorisationService;
import com.raf.si.Banka2Backend.services.FutureService;
import com.raf.si.Banka2Backend.services.UserService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/futures")
public class FutureController {

  private final AuthorisationService authorisationService;

  private final FutureService futureService;

  private final UserService userService;

  @Autowired
  public FutureController(
      AuthorisationService authorisationService,
      FutureService futureService,
      UserService userService) {
    this.authorisationService = authorisationService;
    this.futureService = futureService;
    this.userService = userService;
  }

  @GetMapping()
  public ResponseEntity<?> findAll() {
    String signedInUserEmail = getContext().getAuthentication().getName(); // todo dodaj nove perms
    if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
      return ResponseEntity.status(401).body("You don't have permission to read users.");
    }
    return ResponseEntity.ok().body(futureService.findAll());
  }

  @GetMapping(value = "/{futureId}")
  public ResponseEntity<?> findById(@PathVariable(name = "futureId") Long id) {
    String signedInUserEmail = getContext().getAuthentication().getName();
    if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
      return ResponseEntity.status(401).body("You don't have permission to read.");
    }

    return ResponseEntity.ok().body(futureService.findById(id));
  }

  @GetMapping(value = "/name/{name}")
  public ResponseEntity<?> findFuturesByName(@PathVariable(name = "name") String futureName) {
    String signedInUserEmail = getContext().getAuthentication().getName();
    if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
      return ResponseEntity.status(401).body("You don't have permission to read.");
    }
    return ResponseEntity.ok().body(futureService.findFuturesByFutureName(futureName));
  }

  // todo dodaj check na pocetku da proveri da li  future postoji
  @PostMapping(value = "/buy")
  public ResponseEntity<?> buyFuture(@RequestBody FutureRequestBuySell futureRequest) {
    String signedInUserEmail = getContext().getAuthentication().getName();
    if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
      return ResponseEntity.status(401).body("You don't have permission to buy/sell.");
    }
    Optional<User> user = userService.findByEmail(signedInUserEmail);
//    Optional<Future> future = futureService.findById(futureRequest.getId());
//    if (future.get().getUser().getId() != user.get().getId()) {
//      return ResponseEntity.status(401)
//              .body("You don't have permission to modify this future contract.");
//    }
    futureRequest.setUserId(user.get().getId());
    return futureService.buyFuture(futureRequest);
  }

  // TODO POSTALJI ID USERA U FUNKCIJU, PREKO EMAIL-A
  @PostMapping(value = "/sell")
  public ResponseEntity<?> sellFuture(@RequestBody FutureRequestBuySell futureRequest) {
    String signedInUserEmail = getContext().getAuthentication().getName();
    if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
      return ResponseEntity.status(401).body("You don't have permission to buy/sell.");
    }

    Optional<User> user = userService.findByEmail(signedInUserEmail);
    Optional<Future> future = futureService.findById(futureRequest.getId());
    if (future.get().getUser().getId() != user.get().getId()) {
      return ResponseEntity.status(401)
          .body("You don't have permission to modify this future contract.");
    }

    futureRequest.setUserId(user.get().getId());
    return futureService.sellFuture(futureRequest);
  }

  @PostMapping(value = "/remove/{id}")
  public ResponseEntity<?> removeFromMarket(@PathVariable(name = "id") Long id) {
    String signedInUserEmail = getContext().getAuthentication().getName();
    if (!authorisationService.isAuthorised(PermissionName.READ_USERS, signedInUserEmail)) {
      return ResponseEntity.status(401).body("You don't have permission to buy/sell.");
    }

    Optional<User> user = userService.findByEmail(signedInUserEmail);
    Optional<Future> future = futureService.findById(id);
    if (future.get().getUser().getId() != user.get().getId()) {
      return ResponseEntity.status(401)
          .body("You don't have permission to modify this future contract.");
    }

    return futureService.removeFromMarket(id);
  }
}

package com.raf.si.Banka2Backend.controllers;

import com.raf.si.Banka2Backend.services.AuthorisationService;
import com.raf.si.Banka2Backend.services.FutureService;
import com.raf.si.Banka2Backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
    return ResponseEntity.ok().body(futureService.findAll());
  }

  @GetMapping(value = "/id/{id}")
  public ResponseEntity<?> findById(@PathVariable(name = "id") Long id) {
    return ResponseEntity.ok().body(futureService.findById(id));
  }

  @GetMapping(value = "/name/{name}")
  public ResponseEntity<?> findByName(@PathVariable(name = "name") String contractName) {
    return ResponseEntity.ok().body(futureService.findByName(contractName));
  }

}

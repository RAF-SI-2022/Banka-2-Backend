Feature: Balance service

  Additional description

  Scenario: get all tekuci racuni
    Given test client is logged in
    Then get all tekuci racun

  Scenario: get all poslovni racuni
    When test client is logged in
    Then get all poslovni racuni

  Scenario: get all tekuci devizni
    When test client is logged in
    Then get all tekuci devizni

  Scenario:get all client balances
    When test client is logged in
    Then get all client balances

  Scenario:open tekuci racun
    When test client is logged in
    Then open tekuci racun

  Scenario:open devizni racun
    When test client is logged in
    Then open devizni racun

  Scenario:open poslovni racun
    When test client is logged in
    Then open poslovni racun

  Scenario:get devizni racun
    When test client is logged in
    Then get devizni racun

  Scenario:get tekuci racun
    When test client is logged in
    Then get tekuci racun

  Scenario: get poslovni racun
    When test client is logged in
    Then get poslovni racun
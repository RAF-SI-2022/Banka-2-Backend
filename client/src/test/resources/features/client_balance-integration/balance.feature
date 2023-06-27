Feature: Balance service

  Additional description

  Scenario: get all tekuci racuni
    Given test client is logged in
    Then get all tekuci racun

  Scenario: get all poslovni racuni
    Then get all poslovni racuni

  Scenario: get all tekuci devizni
    Then get all tekuci devizni

  Scenario: try to get nonexistent devizni
    Then try to get nonexistent devizni

  Scenario: try to get nonexistent tekuci
    Then try to get nonexistent tekuci

  Scenario: try to get nonexistent poslovni
    Then try to get nonexistent poslovni

  Scenario:get all client balances
    Then get all client balances

  Scenario:open tekuci racun OVO GOVNO POGLEDAJ
    Then open tekuci racun

  Scenario:open devizni racun
    Then open devizni racun

  Scenario:open poslovni racun
    Then open poslovni racun

  Scenario:get devizni racun
    Then get devizni racun

  Scenario:get tekuci racun
    Then get tekuci racun

  Scenario: get poslovni racun
    Then get poslovni racun
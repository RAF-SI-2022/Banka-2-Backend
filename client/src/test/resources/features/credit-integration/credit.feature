Feature: Credit service

  Additional description

  Scenario: get credits for client
    Given test client exists in db
    When test client is logged in
    Then get credits for client

  Scenario: get all payed interests
    When test client is logged in
    Then get all payed interests

  Scenario: request credit
    When test client is logged in
    Then request credit

  Scenario: get all waiting
    When test client is logged in
    Then get all waiting

  Scenario: delete useless
    Given deleteTestUsers
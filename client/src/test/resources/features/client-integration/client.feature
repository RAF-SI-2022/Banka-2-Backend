Feature: Client service

  Additional description


  Scenario: get all clients from db
    Given test client exists in db
    When test client is logged in
    Then get all clients

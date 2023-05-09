Feature: Currency service

  Additional description

  Scenario: user gets all currencies
    Given user logs in
    When user is logged in
    Then user gets all currencies from database

  Scenario: user gets currency by id
    When user is logged in
    Then user gets currency by id from database

  Scenario: user gets inflation by id
    Then user gets inflation by id

  Scenario: user gets inflation by id and year
    Then user gets inflation by id and year

  Scenario: user gets currency by currency code
    When user is logged in
    Then user gets currency by currency code from database

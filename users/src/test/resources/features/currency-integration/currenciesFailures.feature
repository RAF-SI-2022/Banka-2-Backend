Feature: Currency Failure service

  Additional description

  Scenario: user that is not logged in tries to get currencies from database
    When user is not logged in
    Then user can not get currencies

  Scenario: user requests nonexistent currency by id from database
    Given user logs in
    When user is logged in
    Then user can not get nonexistent currency by id


  Scenario: user gets currency by nonexistent code from database
    Given user logs in
    When user is logged in
    Then user can not get currency by nonexistent code from database


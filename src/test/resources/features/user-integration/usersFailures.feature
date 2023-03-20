Feature: User service

  Additional description

  Scenario: not logged in user tires to access site
    When user not logged in
    Then user accesses endpoint

Feature: Balance failure service

  Additional description

  Scenario: user tries to increase balance but currency not found
    Given user logs in
    When user is logged in
    Then user tries to increase balance but currency not found

  Scenario: user tries to increase balance but user not found
    When user is logged in
    Then user tries to increase balance but user not found

  Scenario: user tries to decrease balance but not enough money
    When user is logged in
    Then user tries to decrease balance but not enough money

  Scenario: user tries to decrease balance but balance not found
    When user is logged in
    Then user tries to decrease balance but balance not found

  Scenario: user tries to decrease balance but some error occured
    When user is logged in
    Then user tries to decrease balance but some error occured
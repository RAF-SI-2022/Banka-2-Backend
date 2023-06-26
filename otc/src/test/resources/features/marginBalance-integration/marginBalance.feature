Feature: Margin balance service

  Tests for margin balance service

  Scenario: user gets all margin balances
    Given balances exist in database
    And user is logged in
    Then user gets all margin balances

  Scenario: user gets margin balance by id
    Given balances exist in database
    And user is logged in
    Then user gets margin balance by id\

  Scenario: user creates margin balance
    Given balance doesnt exist in database
    And user is logged in
    When user creates margin balance
    Then margin balance is saved in database

  Scenario: user updates margin balance
    Given balances exist in database
    And user is logged in
    Then user updates margin balance

  Scenario: user deletes margin balance
    Given balances exist in database
    And user is logged in
    Then user deletes margin balance
Feature: reserve controller

  Additional description: controller and services

  Scenario: user reserves user-option
    Given user logs in
    When user is logged in
    And user-option exists in db
    Then user reserves user-option

  Scenario: user cancels reservation
    Given user logs in
    When user is logged in
    And user-option exists in db
    Then user cancels reservation

  Scenario: user reserves stock
    Given user logs in
    When user is logged in
    And user-option exists in db
    Then user reserves stock

  Scenario: user cancels stock reservation
    Given user logs in
    When user is logged in
    And user-option exists in db
    Then user cancels stock reservation

  Scenario: user reserves future
    Given user logs in
    When user is logged in
    And user-option exists in db
    Then user reserves future

  Scenario: user cancels future reservation
    Given user logs in
    When user is logged in
    And user-option exists in db
    Then user cancels future reservation

  Scenario: user reserves money
    Given user logs in
    When user is logged in
    And user-option exists in db
    Then user reserves money

  Scenario: user cancels money reservation
    Given user logs in
    When user is logged in
    And user-option exists in db
    Then user cancels money reservation

  Scenario: user finalizes stock
    Given user logs in
    When user is logged in
    And stock exists in db
    Then user finalizes stock

  Scenario: user finalizes option
    Given user logs in
    When user is logged in
    And user-option exists in db
    Then user finalizes option

  Scenario: user finalizes future
    Given user logs in
    When user is logged in
    And user-option exists in db
    Then user finalizes future


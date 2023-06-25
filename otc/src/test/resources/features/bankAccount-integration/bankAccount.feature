Feature: BankAccount service

  Tests for bank account service

  Scenario: user gets bank account by id
    Given user logs in
    When user is logged in
    And bank account exists in database
    Then user gets bank account by id

  Scenario: user gets accounts by company id
    Given user logs in
    When user is logged in
    Given company exists in db
    Then user gets accounts by company id

  Scenario: user creates account for company
    Given user logs in
    When user is logged in
    Given company exists in db
    Then user creates account for company

  Scenario: user edits account for company
    Given user logs in
    When user is logged in
    Given company exists in db
    Then user edits account for company

  Scenario: user deletes account for company
    Given user logs in
    When user is logged in
    Given company exists in db
    And bank account exists in database
    Then user deletes account for company

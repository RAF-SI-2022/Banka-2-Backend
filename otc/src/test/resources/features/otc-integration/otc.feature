Feature: Auth service

  Tests for authentication service

  Scenario: user gets all contracts
    Given user logs in
    When contracts exist in database
    Then user gets all contracts

  Scenario: user gets all contracts by company id
    Given user logs in
    When contracts exist in database
    Then user gets all contracts owned by that company

  Scenario: user gets contract by contract id
    Given user logs in
    When contracts exist in database
    Then user gets contract with specified contract id

  Scenario: user gets contract by contract id
    Given user logs in
    When contracts exist in database
    Then user opens contract

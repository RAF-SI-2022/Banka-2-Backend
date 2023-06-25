Feature: Otc service

  Tests for otc service

  Scenario: unauthorized user tries to get all contracts
    Given user without permission logs in
    When contracts exist in database
    Then unauthorized user fails to get all contracts

  Scenario: unauthorized user tries to get all contracts by company id
    Given user without permission logs in
    When contracts exist in database
    Then unauthorized user fails to get all contracts owned by that company

  Scenario: unauthorized user tries to get contract by contract id
    Given user without permission logs in
    When contracts exist in database
    Then unauthorized user fails to get contract with specified contract id

  Scenario: unauthorized user tries to open contract
    Given user without permission logs in
    When contracts exist in database
    Then unauthorized user fails to open contract

  Scenario: unauthorized user tries to edit contract
    Given user without permission logs in
    When contracts exist in database
    Then unauthorized user fails to edit contract

  Scenario: unauthorized user tries to finalize contract
    Given user without permission logs in
    When contracts exist in database
    Then unauthorized user fails to finalize contract by id

  Scenario: unauthorized user tries to delete contract
    Given user without permission logs in
    When contracts exist in database
    Then unauthorized user fails to delete contract by id

  Scenario: unauthorized user tries to get all elements
    Given user without permission logs in
    When elements exist in database
    Then unauthorized user fails to get all elements

  Scenario: unauthorized user tries to get element by id
    Given user without permission logs in
    When elements exist in database
    Then unauthorized user fails to get element by id

  Scenario: unauthorized user tries to get elements for contract
    Given user without permission logs in
    When elements exist in database
    Then unauthorized user fails to get elements for contract

  Scenario: unauthorized user tries to add transaction element to contract
    Given user without permission logs in
    When contracts exist in database
    Then unauthorized user fails to add element to contract

  Scenario: unauthorized user tries to remove transaction element from contract
    Given user without permission logs in
    When contracts exist in database
    Then unauthorized user fails to remove element from contract
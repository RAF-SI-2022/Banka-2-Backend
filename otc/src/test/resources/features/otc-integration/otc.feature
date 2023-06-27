Feature: OTC service

  Tests for OTC service

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

  Scenario: user opens contract
    Given user logs in
    When contracts exist in database
    Then user opens contract

  Scenario: user edits contract
    Given user logs in
    When contracts exist in database
    Then user edits contract

  Scenario: user finalizes contract
    Given user logs in
    When contracts exist in database
    Then user finalizes contract by id

  Scenario: user deletes contract
    Given user logs in
    When contracts exist in database
    Then user deletes contract by id

  Scenario: user gets all elements
    Given user logs in
    When elements exist in database
    Then user gets all elements

  Scenario: user gets element by id
    Given user logs in
    When elements exist in database
    Then user gets element by id

  Scenario: user gets elements for contract
    Given user logs in
    When elements exist in database
    Then user gets elements for contract

#  Scenario: user adds transaction element to contract
#    Given user logs in
#    When contracts exist in database
#    Then user adds element to contract
#
#  Scenario: user removes transaction element from contract
#    Given user logs in
#    When contracts exist in database
#    Then user deletes element from contract
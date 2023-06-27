Feature: Margin transaction service

  Tests for margin transaction service

  Scenario: user gets all transactions
    Given transactions exist in database
    And user is logged in
    Then user gets all transactions

  Scenario: user gets all transactions by group
    Given transactions exist in database
    And user is logged in
    Then user gets all transactions by group

  Scenario: user gets transaction by id
    Given transactions exist in database
    And user is logged in
    Then user gets transaction by id

#  Scenario: user creates margin transaction
#    Given transaction doesnt exist in database
#    And user is logged in
#    When user creates margin transaction
#    Then margin transaction is saved in database
#
#  Scenario: user gets transaction by email
#    Given transactions exist in database
#    And user is logged in
#    Then user gets transaction by email
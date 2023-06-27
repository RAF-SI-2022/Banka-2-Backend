Feature: Margin transaction service (failures)

  Fail tests for margin transaction service

  Scenario: user fails to get transaction by id because it doesnt exist
    Given user is logged in
    Then user fails to get transaction by id
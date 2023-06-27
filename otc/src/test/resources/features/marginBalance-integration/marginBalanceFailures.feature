Feature: Margin balance service (failure)

  Failure tests for margin balance service

  Scenario: user fails to get margin balance by id because it doesnt exist
    Given user is logged in
    Then user fails to get margin balance by id

  Scenario: user fails to update margin balance by id because it doesnt exist
    Given user is logged in
    Then user fails to update margin balance by id

  Scenario: user fails to delete margin balance by id because it doesnt exist
    Given user is logged in
    Then user fails to delete margin balance by id
Feature: ServiceAuth controller tests

  Failure tests for ServiceAuth controller

  Scenario: request with valid token
    Given invalid token generated
    When post to validate
    Then not ok response

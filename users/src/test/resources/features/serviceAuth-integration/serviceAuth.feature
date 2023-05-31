Feature: ServiceAuth controller tests

  Tests for ServiceAuth controller

  Scenario: request with valid token
    Given valid token generated
    When post to validate
    Then ok response

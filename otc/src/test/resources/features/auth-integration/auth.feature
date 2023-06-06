Feature: Auth service

  Tests for authentication service

  Scenario: user logs in
    Given user exists in database
    When user logs in with correct credentials
    Then ok response
    And token returned in response

  Scenario: user resets password
    Given user exists in database
    When user resets password with correct email
    Then ok response
    And password reset token added to database

  Scenario: user changes password
    Given user exists in database
    When user resets password with correct email
    And user resets password with correct token
    Then ok response
    And user's password changed in database
Feature: Auth service (failure)

  Tests for authentication service failures

  Scenario: user logs in
    Given user exists in database
    When user logs in with bad credentials
    Then 401 response

  Scenario: user resets password
    Given user exists in database
    When user resets password with bad email
    Then 401 response

  Scenario: user changes password
    Given user exists in database
    When user resets password with bad token
    Then 401 response
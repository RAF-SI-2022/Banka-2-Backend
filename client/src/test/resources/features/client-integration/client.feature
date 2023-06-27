Feature: Client service

  Additional description

  Scenario: get all clients from db
    Given test client exists in db
    When test client is logged in
    Then get all clients

  Scenario: get mail from token
    When test client is logged in
    Then get mail from token

  Scenario: get nonexistent client
    Then get nonexistent client

  Scenario: bad login credentials
    Then bad login credentials

  Scenario: get user by id
    When test client is logged in
    Then get user by id

  Scenario: create user
    When test client is logged in
    Then create user

  Scenario: sendToken
    When test client is logged in
    Then sendToken

  Scenario: checkToken is valid
    Then checkToken is valid

  Scenario: checkToken is not valid
    Then checkToken is not valid

    Scenario: delete useless
    Given deleteTestUsers
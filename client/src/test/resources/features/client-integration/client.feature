Feature: Client service

  Additional description

  Scenario: get all clients from db
    Given test client exists in db
    When test client is logged in
    Then get all clients

  Scenario: get mail from token
    When test client is logged in
    Then get mail from token

  Scenario: get user by id
    When test client is logged in
    Then get user by id

  Scenario: create user
    When test client is logged in
    Then create user

  Scenario: sendToken
    When test client is logged in
    Then sendToken

  Scenario: checkToken
    When test client is logged in
    Then checkToken

    Scenario: delete useless
    Given deleteTestUsers
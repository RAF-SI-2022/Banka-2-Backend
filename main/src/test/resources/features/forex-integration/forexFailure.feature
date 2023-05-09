Feature: Forex service

  Additional description

  Scenario: user cant find forex in database or by api
    Given user logs in
    When user is logged in
    Then user cant find forex in database or by api

  Scenario: user doesn't have balance in currency he requested to convert from
    Given user logs in
    When user is logged in
    Then user doesn't have balance in currency he requested to convert from

  Scenario: user doesn't have enough balance in currency he requested to convert from
    Given user logs in
    When user is logged in
    Then user doesn't have enough balance in currency he requested to convert from
Feature: Stock service

  Scenario: user gets all stocks from database
    Given user logs in
    When user is logged in
    Then user gets all stocks from database

  Scenario: user gets stock by id from database
    Given user logs in
    And there is a stock in database
    When user is logged in
    Then user gets stock by id from database

  Scenario: user gets stock by symbol from database
    Given user logs in
    And there is a stock in database
    When user is logged in
    Then user gets stock by symbol from database
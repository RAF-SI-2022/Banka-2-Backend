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

  Scenario: user gets his userStocks
    When user is logged in
    Then user gets his user stocks

  Scenario: user gets stock history
    When user is logged in
    Then user gets stock history

  Scenario: user buys stock
    When user is logged in
    Then user  buys stock

  Scenario: user sells stock
    When user is logged in
    Then user sells stock

  Scenario: user removes stock
    When user is logged in
    Then user removes stock

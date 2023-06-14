Feature: Option service

  Scenario: user gets option by symbol and date
    Given user logs in
    When user is logged in
    Then user gets option by symbol and date

  Scenario: user gets option by symbol
    Given user logs in
    When user is logged in
    Then user gets option by symbol

  Scenario: user gets dates
    Given user logs in
    When user is logged in
    Then user gets dates

  Scenario: user gets user options
    Given user logs in
    When user is logged in
    Then user gets user options

  Scenario: user gets his user options
    Given user logs in
    When user is logged in
    Then user gets his user options

#  Scenario: user buys an AAPL option
#    Given user logs in
#    And there is an option to buy
#    When user is logged in
#    Then user buys an AAPL option

  Scenario: user sells an AAPL option
    Given user logs in
    When user is logged in
    Then user sells an AAPL option

  Scenario: user buys stock with option
    Given user logs in
    When user is logged in
    Then user buys stock with option
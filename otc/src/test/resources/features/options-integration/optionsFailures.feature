Feature: Option failure service

  Scenario: user sells a nonexistent AAPL option
    Given user logs in
    When user is logged in
    Then user sells a nonexistent AAPL option

  Scenario: user buys a nonexistent AAPL option
    Given user logs in
    When user is logged in
    Then user buys a nonexistent AAPL option

Feature: Future service

  Additional description

  Scenario: user gets all futures from database
    Given user logs in
    When user is logged in
    Then user gets all futures from database

  Scenario: user gets future by id from database
    Then user gets future by id from database

  Scenario: user gets future by id from database
    Then user gets future by id from database

  Scenario: user gets future by name from database
    Then user gets future by name from database

  Scenario: user buys future from company
    Given future is on sale
    Then user buys future from company

  Scenario: user buys future from company with null currency code
    Given future is on sale
    Then user buys future from company with null currency code

  Scenario: user buys future from company with limit or stop
    Given future is on sale
    Then user buys future from company with limit or stop

  Scenario: user buys future from another user
    Given future is on sale
    Given future is owned by another user
    Then user buys future from another user

  Scenario: user buys future from another user with limit or stop
    Given future is on sale
    Given future is owned by another user
    Then user buys future from another user with limit or stop

  Scenario: user sells future
    Given future is owned by this user
    Then user sells future

  Scenario: user sells future with limit or stop
    Given future is owned by this user
    Then user sells future with limit or stop

  Scenario: user gets waiting-futures
    Then user gets waiting-futures

  Scenario: user gets his futures
    Then user gets his futures

  Scenario: user removes waiting future buy
    Then user removes waiting future buy

  Scenario: user removes waiting future sell
    Then user removes waiting future sell

  Scenario: user removes from market
    Then user removes from market
Feature: Future service

  Additional description

  Scenario: user buys future which is not for sale
    Given user logs in
    When user is logged in
    Given future is not for sale
    Then user can't buy future because it is not for sale

  Scenario: user buys future but his daily limit has exceeded
    Given daily limit exceeded
    Then user can't buy future because his daily limit has exceeded

  Scenario: user buys future but he doesnt have enough money
    Given user free money is zero
    Then user cant buy future because he doesnt have enough money

  Scenario: user can't sell future because he is not owner
    Given user is not owner of the future
    Then user can't sell future because he is not owner

  Scenario: user cant get all futures because lack of permissions
    Given nonpriv user exists
    Given nonpriv user logs in
    Then user can't get futures

  Scenario: user cant get future by id because lack of permissions
    Given nonpriv user logs in
    Then user cant get future by id

  Scenario: user cant get future by name because lack of permissions
    Given nonpriv user logs in
    Then user cant get future by name

  Scenario: user cant sell future because lack of permissions
    Given nonpriv user logs in
    Then user cant sell future

  Scenario: user cant buy future because lack of permissions
    Given nonpriv user logs in
    Then user cant buy future

  Scenario: user cant remove future by id because lack of permissions
    Given nonpriv user logs in
    Then user cant remove future by id

  Scenario: user cant remove waiting buy future because lack of permissions
    Given nonpriv user logs in
    Then user cant remove waiting buy future

  Scenario: user cant get waiting buy future by id because lack of permissions
    Given nonpriv user logs in
    Then user cant get waiting buy future

  Scenario: user cant get future by user because lack of permissions
    Given nonpriv user logs in
    Then user cant get future by user

  Scenario: user without balacne buys future
    Given user doesnt have a balance
#    Then user tries to but future

  Scenario: user removes waiting future buy but isnt authorized
    Then user removes waiting future buy but isnt authorized


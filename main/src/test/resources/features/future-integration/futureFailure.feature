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

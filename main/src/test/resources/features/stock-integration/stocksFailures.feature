Feature: Stock service

  Scenario: get stock by nonexistent id
    Given user logged in
    When stock doesnt exist
    Then user gets nonexistent stock by id

#  Scenario: get stock by nonexistent symbol
#    When stock with symbol doesnt exist
#    Then user gets nonexistent stock by symbol

  Scenario: user without buy perms tries to buy stock
    Given user without permissions logs in
    And doesnt have readUser perms
    When user doesnt have perms
    Then user tries to buy stock

  Scenario: user gets nonexistent stock history
    Then user gets nonexistent stock history

  Scenario: user without perms tries to sell stock
    Then user tries to sell stock

#  Scenario: user without perms tries to see stocks
#    Then user tries to get stock

#  Scenario: user without perms tries to remove stocks
#    Then user tries to remove stock


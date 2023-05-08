Feature: Transaction service


  Scenario: Admin user gets all orders
    Given user logs in
    When user is logged in
    Then user gets all orders

  Scenario: user gets transactions by TransactionsByCurrencyValue
    Then user gets transactions by TransactionsByCurrencyValue


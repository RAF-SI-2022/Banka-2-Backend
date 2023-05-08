Feature: order service


  Scenario: User gets all orders
    Given user logs in
    When user logged in
    Then user gets all orders from db

  Scenario: Get all orders by user ID
    Given user logs in
    And there are orders in database
    When user logged in
    Then user gets orders by user id from database

  Scenario: Approve order
    Given user logs in
    And there is order in waiting status in db
    When user logged in
    Then user Approves order

  Scenario: Deny order
    Given user logs in
    And there is order in waiting status in db
    When user logged in
    Then user Denies order
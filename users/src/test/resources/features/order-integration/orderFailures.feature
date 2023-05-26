Feature: order service


  Scenario: not logged in user tires to get all orders
    When user not logged in
    Then user accesses endpoint

  Scenario: not logged in user tires to get all orders by user
    When user not logged in
    Then user accesses get all by user endpoint

  Scenario: logged user approves non existing order
    Given user logs in
    When user logged in
    Then order not approved

  Scenario: logged user approves non waiting order
    Given user logs in
    And there is order in non waiting status in db
    When user logged in
    Then order not approved

  Scenario: logged user denies non existing order
    Given user logs in
    When user logged in
    Then order not denied

  Scenario: logged user denies non waiting order
    Given user logs in
    And there is order in non waiting status in db
    When user logged in
    Then order not denied
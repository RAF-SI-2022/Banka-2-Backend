Feature: Contact person service

  Tests for contact person service

  Scenario: user gets all contact persons
    Given user logs in
    When user is logged in
    Then user gets all contact persons

  Scenario: user gets contact person by id
    Given user logs in
    When user is logged in
    And contact person exists in db
    Then user gets contact person by id

  Scenario: user creates contact person
    Given user logs in
    When user is logged in
    Then user creates contact person

  Scenario: user edits contact person
    Given user logs in
    When user is logged in
    And contact person exists in db
    Then user edits contact person

  Scenario: user deletes contact person
    Given user logs in
    When user is logged in
    And contact person exists in db
    Then user deletes contact person
Feature: Company service

  Tests for company service

  Scenario: user gets all companies
    Given user logs in
    When user is logged in
    Then user gets all companies

  Scenario: user gets all accounts for company
    Given user logs in
    When user is logged in
    And company exists in db
    Then user gets all accounts for company

  Scenario: user gets company by id
    Given user logs in
    When user is logged in
    And company exists in db
    Then user gets company by id

  Scenario: user gets company by name
    Given user logs in
    When user is logged in
    And company exists in db
    Then user gets company by name

  Scenario: user gets company by registration number
    Given user logs in
    When user is logged in
    And company exists in db
    Then user gets company by registration number

  Scenario: user gets company by tax number
    Given user logs in
    When user is logged in
    And company exists in db
    Then user gets company by tax number

  Scenario: user creates company
    Given user logs in
    When user is logged in
    Then user creates company

  Scenario: user adds contacts and accounts for company
    Given user logs in
    When user is logged in
    And company exists in db
    Then user adds contacts and accounts for company

  Scenario: user edits company
    Given user logs in
    When user is logged in
    And company exists in db
    Then user edits company


Feature: Exchange service

  Additional description

  Scenario: user gets all exchanges from database
    Given user logs in
    When user is logged in
    Then user gets all exchanges from database

  Scenario: user gets exchange by id from database
    Given user logs in
    And there is an exchange record in database
    When user is logged in
    Then user gets exchange by id from database


  Scenario: user gets exchange by acronym from database
    Given user logs in
    And there is an exchange record in database
    When user is logged in
    Then user gets exchange by acronym from database

  Scenario: user gets activity of exchange by MIC Code from database
    Given user logs in
    And there is an exchange record in database
    When user is logged in
    Then user gets activity of exchange by MIC Code from database
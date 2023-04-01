Feature: Exchange Failure service

  Additional description

  Scenario: user that is not logged in tries to get exchanges from database
    When user is not logged in
    Then user can not get exchanges

  Scenario: user requests nonexistent exchange by id from database
    Given user logs in
    When user is logged in
    Then get nonexistent exchange by id


  Scenario: user gets exchange by nonexistent acronym from database
    Given user logs in
    When user is logged in
    Then user gets exchange by nonexistent acronym from database

  Scenario: user gets activity of nonexistent exchange by MIC Code from database
    Given user logs in
    When user is logged in
    Then user gets activity of nonexistent exchange by MIC Code from database

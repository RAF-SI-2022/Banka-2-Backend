Feature: Forex service

  Additional description

  Scenario: user gets all forex from database
    Given user logs in
    When user is logged in
    Then user gets all forex from database

  Scenario: user gets forex by currency to and currency from from database
    Given user logs in
    And there is a forex record in database
    When user is logged in
    Then user gets forex by currency to and currency from from database

  Scenario: user converts from one currency to another with db
    Given user logs in
    And there is a forex record in database
    When user is logged in
    Then user converts from one currency to another with db


  Scenario: user gets forex by currency to and currency from from api
    Given user logs in
    And there is no forex record in database
    When user is logged in
    Then user gets forex by currency to and currency from from api

#  Scenario: user converts from one currency to another with api
#    Given user logs in
#    And there is no forex record in database
#    When user is logged in
#    Then user converts from one currency to another with api

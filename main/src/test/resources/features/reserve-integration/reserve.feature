Feature: reserve controller

  Additional description: controller and services

  Scenario: user reserves user-option
    Given user logs in
    When user is logged in
    And user-option exists in db
    Then user reserves user-option
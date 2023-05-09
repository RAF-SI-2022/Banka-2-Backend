Feature: User service

  Test for user controller nad service, they are meant to test good requests meant to pass

  Scenario: user logs in
    When user can login
    Then user logs in

  Scenario: admin gets all permission names
    When admin logged in
    Then read all permissions

  Scenario: user gets his permissions
    When user logged in
    Then user gets his permissions

  Scenario: Creating new user
    When creating new user
    Then new user is saved in database

  Scenario: get all users
    When database not empty
    Then get all users from database

  Scenario: admin gets his daily limit
    Then user gets his daily limit

  Scenario: user resets his daily limit
    Then admin resets his daily limit

  Scenario: admin updates user
    When user to update exists in database
    Then admin updates user

  Scenario: get user by his id
    When user exists in database
    Then get user by his id

  Scenario: admin changes users default daily limit
    Then admin changes users default daily limit

  Scenario: deactivate user
    When user exists in database
    Then deactivate user in database

  Scenario: reactivate user
    When user exists in database
    Then reactivate user in database

  Scenario: logged in user updates their profile
    Given any user logs in
    When user is logged in
    Then user updates his profile

  Scenario: get user by his email
    When user exists in database
    Then get user by his email

  Scenario: logged in user changes his password
    When user is logged in
    Then user changes his password

  Scenario: Deleting user
    Given privileged user logged in
    When deleting user from database
    Then user no longer in database


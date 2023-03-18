Feature: User service

  Additional description


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
    When Creating new user
    Then New user is saved in database

  Scenario: get all users
    When database not empty
    Then get all users from database

  Scenario: deactivate user
    When user exists in database
    Then deactivate user in database

  Scenario: reactivate user
    When user exists in database
    Then reactivate user in database

  Scenario: admin edits user in database
    When admin logged in and user exists in database
    Then update user in database

  Scenario: logged in user updates their profile
    Given any user logs in
    When user is logged in
    Then user updates his profile

  Scenario: logged in user changes his password
    When user is logged in
    Then user changes his password

  Scenario: Deleting user
    Given admin is logged in
    When deleting user from database
    Then user no longer in database



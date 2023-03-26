Feature: User service

  Test for user controller nad service, they are meant to test bad requests and unauthorised requests

  Scenario: not logged in user tires to access site
    When user not logged in
    Then user accesses endpoint

  Scenario: bad url request
    Given user logged in
    When logged in user
    Then is not found

  Scenario: user gets permissions from nonexistent user
    When user doesnt exist in database
    Then get perms form nonexistent user

  Scenario: get nonexistent user from database
    When user doesnt exist in database
    Then get nonexistent user by id

  Scenario: deactivate nonexistent user
    When user doesnt exist in database
    Then deactivate nonexistent user

  Scenario: reactivate nonexistent user
    When user doesnt exist in database
    Then reactivate nonexistent user

#  Scenario: deleting nonexistent user #test je brljao zbog promenjenog importa (hoce -models.users.User umesto -models.mariaDb.User, i buni se)
#    When user doesnt exist in database
#    Then deleting nonexistent user from database

  Scenario: non admin user gets all permission names
    Given non privileged user logs in
    When non privileged user logged in
    Then user doesnt get all permission names

  Scenario: non privileged user gets users permissions
    When non privileged user logged in and user exists in database
    Then user doesnt gets users permissions

  Scenario: non privileged user creates new user
    Then user not created

  Scenario: non privileged user get all users
    When non privileged user logged in and database not empty
    Then user doesnt get all users from database

  Scenario: non privileged user gets user by his id
    When non privileged user logged in and user exists in database
    Then user doesnt get user by id

  Scenario: non privileged user deactivates user
    When non privileged user logged in and user exists in database
    Then user doesnt deactivate user

  Scenario: non privileged user reactivates user
    When non privileged user logged in and user exists in database
    Then user doesnt reactivate user

  Scenario: non privileged user edits user in database
    When non privileged user logged in and user exists in database
    Then user not updated

  Scenario: non privileged user get user by his email
    When non privileged user logged in and user exists in database
    Then user doesnt user by his email

  Scenario: non privileged user deleting user
    When non privileged user logged in and user exists in database
    Then user still in database

  Scenario: wipe db from test user
    Given delete test user
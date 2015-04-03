Feature: Password Manager
  Scenario: Change password
    Given User is logged in
    And User is on edit profile page
    When User presses Edit Password button
    And User enters "value" for new password and repeats "value" for new password confirmation
    And User presses "Change password"
    Then User should see "Password changed"
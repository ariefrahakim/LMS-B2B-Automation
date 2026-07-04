# -----------------------------------------------------------------------------
# Feature: Login to the LMS B2B web application
# -----------------------------------------------------------------------------
# Positive vs. negative flows for the /login screen.
#
# The "URL still contains /login" check is our authoritative "login rejected"
# signal — the toast/alert is ephemeral in Chakra and difficult to catch
# reliably from Selenium, so we don't hard-assert on it.
# -----------------------------------------------------------------------------
Feature: LMS B2B login

  Background:
    Given the user is on the login page

  @positive @smoke
  Scenario: Successful login with valid credentials
    When the user submits valid credentials from config
    Then the user is redirected away from the login page
    And the dashboard shell is visible

  @positive
  Scenario: Login page renders required inputs
    Then the login form is visible

  @negative
  Scenario: Login fails with a wrong password
    When the user submits email "arwendymelyn@dibimbing.id" and password "wrong-password"
    Then the user remains on the login page

  @negative
  Scenario: Login fails with an unknown email
    When the user submits email "nobody-here@example.com" and password "somePass123"
    Then the user remains on the login page

  @negative
  Scenario Outline: Login fails for blank / malformed input
    When the user submits email "<email>" and password "<password>"
    Then the user remains on the login page

    Examples:
      | email                        | password       |
      |                              | s2et9bh6l      |
      | arwendymelyn@dibimbing.id    |                |
      | not-an-email                 | s2et9bh6l      |
      |                              |                |

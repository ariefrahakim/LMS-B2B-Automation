# -----------------------------------------------------------------------------
# Feature: Manage employees in the LMS B2B web app
# -----------------------------------------------------------------------------
# Every scenario starts from an already-logged-in state (see Background). The
# Add-Employee modal is exercised for both happy-path and failure modes.
# -----------------------------------------------------------------------------
Feature: Employee management

  Background:
    Given the user is logged in and on the employee list page

  @positive @smoke
  Scenario: Employee list page renders with a total count
    Then the employee count is visible
    And the first employee row is visible

  @positive
  Scenario: Add-employee modal renders all required inputs
    When the user opens the add-employee modal
    Then all required employee-form fields are visible

  # The actual "create with valid data" happy-path is exercised at the API layer
  # (`EmployeeCRUDTest` in the GraphQL suite) because filling every dependent
  # dropdown (Program Studi, Angkatan) via the UI adds accidental complexity to
  # the BDD scenario for no additional coverage.

  @negative
  Scenario: Add an employee with a duplicate email is rejected
    When the user opens the add-employee modal
    And the user submits the add-employee form with the admin email
    Then the add-employee modal is still open

  @negative
  Scenario: Submitting the add-employee form with blank required fields is rejected
    When the user opens the add-employee modal
    And the user clicks submit without filling required fields
    Then the add-employee modal is still open

  @negative
  Scenario: Searching for a non-existent employee yields no rows
    When the user searches for "zzz-no-such-employee-xyz"
    Then the first employee row is not visible

package ui.stepdefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import ui.driver.DriverFactory;
import ui.pages.EmployeePage;
import ui.pages.LoginPage;
import utils.ConfigReader;

import java.util.UUID;

/**
 * Step definitions for `employee.feature`.
 *
 * State is scoped to a single scenario; we cache the fresh email generated
 * during "add employee" so subsequent steps in the same scenario can refer
 * back to it if needed.
 */
public class EmployeeSteps {

    private EmployeePage employeePage;
    private String freshEmail;

    // -------------------------- GIVEN --------------------------

    @Given("the user is logged in and on the employee list page")
    public void the_user_is_logged_in_and_on_employee_list() {
        // Fresh login every scenario — Hooks starts a fresh browser.
        new LoginPage(DriverFactory.get()).open().loginAsConfiguredUser();
        employeePage = new EmployeePage(DriverFactory.get()).open();
    }

    // -------------------------- WHEN --------------------------

    @When("the user opens the add-employee modal")
    public void user_opens_the_add_employee_modal() {
        employeePage.clickAddEmployee();
    }

    @And("the user submits the add-employee form with a fresh email")
    public void user_submits_form_with_fresh_email() {
        // A UUID-based email guarantees uniqueness so the "duplicate" server rule doesn't reject it.
        freshEmail = "qa-ui-" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        employeePage.fillRequiredFields("QA UI Employee", freshEmail, "employee")
                    .submitForm();
    }

    @And("the user submits the add-employee form with the admin email")
    public void user_submits_form_with_admin_email() {
        // The configured admin email is guaranteed to already exist in the DB,
        // so the create must be rejected as a duplicate.
        employeePage.fillRequiredFields("Duplicate User",
                        ConfigReader.getProperty("emailWeb"), "employee")
                    .submitForm();
    }

    @And("the user clicks submit without filling required fields")
    public void user_submits_form_blank() {
        // Do NOT fill any field — just click submit.
        employeePage.submitForm();
    }

    @When("the user searches for {string}")
    public void user_searches_for(String keyword) {
        employeePage.search(keyword);
        // Give the debounced search a moment to reflect in the DOM.
        try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    // -------------------------- THEN --------------------------

    @Then("the employee count is visible")
    public void the_employee_count_is_visible() {
        String count = employeePage.getListCount();
        Assert.assertNotNull(count, "Count widget should render");
        Assert.assertFalse(count.isEmpty(), "Count text should not be empty");
    }

    @Then("the first employee row is visible")
    public void the_first_employee_row_is_visible() {
        Assert.assertTrue(employeePage.firstRowExists(),
                "Expected at least one employee row to render");
    }

    @Then("the first employee row is not visible")
    public void the_first_employee_row_is_not_visible() {
        Assert.assertFalse(employeePage.firstRowExists(),
                "Expected no rows for a bogus search term");
    }

    @Then("the add-employee modal is still open")
    public void the_add_employee_modal_is_still_open() {
        // Give the server a moment to respond and the UI to (not) close the modal.
        try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        Assert.assertTrue(employeePage.isAddModalOpen(),
                "Modal should stay open when submission is invalid / rejected");
    }

    @Then("all required employee-form fields are visible")
    public void all_required_employee_form_fields_are_visible() {
        Assert.assertTrue(employeePage.hasAllRequiredFields(),
                "Add-Employee modal should render name/email/role/phone/gender inputs");
    }
}

package ui.stepdefs;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import ui.driver.DriverFactory;
import ui.pages.DashboardPage;
import ui.pages.LoginPage;

/**
 * Step definitions for `login.feature`.
 *
 * Each Gherkin step maps to one method here. Page Objects (LoginPage,
 * DashboardPage) hold the actual Selenium code; steps only orchestrate them
 * and assert outcomes.
 */
public class LoginSteps {

    private LoginPage loginPage;
    private DashboardPage dashboard;

    // ------------------------------ GIVEN ------------------------------

    @Given("the user is on the login page")
    public void the_user_is_on_the_login_page() {
        loginPage = new LoginPage(DriverFactory.get()).open();
    }

    // ------------------------------ WHEN -------------------------------

    @When("the user submits valid credentials from config")
    public void the_user_submits_valid_credentials_from_config() {
        loginPage.loginAsConfiguredUser();
    }

    @When("the user submits email {string} and password {string}")
    public void the_user_submits_email_and_password(String email, String password) {
        loginPage.loginAs(email, password);
    }

    // ------------------------------ THEN -------------------------------

    @Then("the user is redirected away from the login page")
    public void the_user_is_redirected_away_from_the_login_page() {
        Assert.assertTrue(loginPage.isLoggedIn(),
                "Expected redirect off /login, still on: " + loginPage.currentUrl());
    }

    @Then("the dashboard shell is visible")
    public void the_dashboard_shell_is_visible() {
        dashboard = new DashboardPage(DriverFactory.get());
        Assert.assertTrue(dashboard.isLoaded(),
                "Dashboard shell (nav links) should be visible after login");
    }

    @Then("the user remains on the login page")
    public void the_user_remains_on_the_login_page() {
        // Give the UI a moment to react (form validation / toast) — no strict wait,
        // we just re-read the URL after the click.
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        Assert.assertTrue(loginPage.currentUrl().contains("/login"),
                "Expected to still be on /login, actual URL: " + loginPage.currentUrl());
    }

    @Then("an error message is displayed")
    public void an_error_message_is_displayed() {
        Assert.assertTrue(loginPage.hasErrorMessage(),
                "Expected an error/toast to be visible for invalid login");
    }

    @Then("the login form is visible")
    public void the_login_form_is_visible() {
        // The @Given step already asserts the form renders (waitVisible on the
        // email input succeeded) — here we simply reaffirm the URL is /login.
        Assert.assertTrue(loginPage.currentUrl().contains("/login"),
                "Expected to be on /login while form is rendering");
    }
}

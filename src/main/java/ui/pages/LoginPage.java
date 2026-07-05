package ui.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ui.locators.LoginLocator;
import utils.ConfigReader;

import java.time.Duration;

/**
 * Page Object for the LMS B2B login screen.
 *
 * Behaviour lives here; every selector comes from {@link LoginLocator}.
 */
public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Navigate the browser to the login URL configured in config.properties.
     *
     * Because we reuse a single browser across all Cucumber scenarios, an
     * earlier scenario may have left an authenticated session in place — that
     * would cause `/login` to redirect straight to `/dashboard`. Clearing the
     * cookie jar before navigating guarantees the login form always renders.
     */
    public LoginPage open() {
        driver.manage().deleteAllCookies();
        driver.get(ConfigReader.getProperty("webUrl"));
        // Cold start on CI (Cloudflare handshake + first SPA hydration) can exceed
        // the standard 15s wait, so we use a longer 45s window and retry once
        // with a refresh before giving up.
        WebDriverWait long45 = new WebDriverWait(driver, Duration.ofSeconds(45));
        try {
            long45.until(ExpectedConditions.visibilityOfElementLocated(LoginLocator.EMAIL_INPUT));
        } catch (Exception first) {
            driver.navigate().refresh();
            long45.until(ExpectedConditions.visibilityOfElementLocated(LoginLocator.EMAIL_INPUT));
        }
        return this;
    }

    /** Submit the login form with the given credentials. */
    public void loginAs(String email, String password) {
        // For blank strings we still need to clear the field, so `type()` is fine.
        type(LoginLocator.EMAIL_INPUT, email);
        type(LoginLocator.PASSWORD_INPUT, password);
        click(LoginLocator.SUBMIT_BUTTON);
    }

    /** Convenience: login with the credentials stored in config.properties. */
    public void loginAsConfiguredUser() {
        loginAs(ConfigReader.getProperty("emailWeb"),
                ConfigReader.getProperty("passwordWeb"));
    }

    /** @return the URL currently displayed in the browser. */
    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * @return true if an error toast/alert appears with non-empty text within
     * the wait window. Chakra renders errors as elements with role="alert".
     */
    public boolean hasErrorMessage() {
        try {
            return wait.until(d -> {
                for (WebElement el : d.findElements(LoginLocator.TOAST_ALERT)) {
                    if (el.isDisplayed() && !el.getText().trim().isEmpty()) return true;
                }
                return false;
            });
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * A successful login redirects away from `/login`. We consider "no longer
     * on /login" as the positive signal — this avoids hard-coding the exact
     * destination path.
     */
    public boolean isLoggedIn() {
        try {
            wait.until(d -> !d.getCurrentUrl().contains("/login"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

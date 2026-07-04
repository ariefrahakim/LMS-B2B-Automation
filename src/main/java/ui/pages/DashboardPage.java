package ui.pages;

import org.openqa.selenium.WebDriver;
import ui.locators.DashboardLocator;

/**
 * Page Object for the post-login dashboard.
 *
 * On this app the dashboard is the first authenticated route the user lands on
 * after `/login`. Locators are kept in {@link DashboardLocator}; this class
 * only expresses behaviour.
 */
public class DashboardPage extends BasePage {

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    /** @return true if any authenticated navigation element is rendered. */
    public boolean isLoaded() {
        return isPresent(DashboardLocator.AUTH_NAV_HINT);
    }
}

package ui.locators;

import org.openqa.selenium.By;

/**
 * Centralised locators for the LMS B2B post-login dashboard.
 *
 * We don't have stable ids on the top-level layout, so we probe for a URL
 * change instead (see {@link ui.pages.LoginPage#isLoggedIn()}). This class
 * keeps a nav-hint locator as a *secondary* check for scenarios that want a
 * stronger assertion than "URL changed".
 */
public final class DashboardLocator {

    private DashboardLocator() {}

    /**
     * Any of the top-level nav labels that only appear after login.
     * If none render within the wait window we consider the dashboard
     * not loaded.
     */
    public static final By AUTH_NAV_HINT = By.xpath(
            "//*[contains(text(),'Dashboard') or contains(text(),'Employees') " +
            "or contains(text(),'Programs') or contains(text(),'Announcements') " +
            "or contains(text(),'Users') or contains(text(),'Home')]");
}

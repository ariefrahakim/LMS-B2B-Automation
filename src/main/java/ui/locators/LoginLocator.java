package ui.locators;

import org.openqa.selenium.By;

/**
 * Centralised locators for the LMS B2B login screen.
 *
 * Elements have stable, semantic ids emitted by the SPA
 * (verified against the live DOM), so we prefer id-based
 * locators — they're the most resilient across UI reskins.
 */
public final class LoginLocator {

    private LoginLocator() {}

    /** Email / username input. */
    public static final By EMAIL_INPUT = By.id("input-username-or-email");

    /** Password input. */
    public static final By PASSWORD_INPUT = By.id("input-password");

    /** "Sign in" submit button. */
    public static final By SUBMIT_BUTTON = By.id("button-sign-in");

    /**
     * Chakra toast / alert that appears when the server rejects the login.
     * We wait for any element with role="alert" that has non-empty text.
     */
    public static final By TOAST_ALERT = By.cssSelector("[role='alert']");
}

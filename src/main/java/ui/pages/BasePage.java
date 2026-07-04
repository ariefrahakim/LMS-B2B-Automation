package ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Base class shared by every Page Object.
 *
 * Encapsulates the WebDriver reference and the common explicit-wait / interaction
 * helpers so that individual page classes only carry business-level methods.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        // Explicit wait of 15s — chosen to survive slow first-render on the live env.
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /** Wait until the element is visible, then return it. */
    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Wait until the element is clickable, then return it. */
    protected WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /** Type text into a field after clearing it. */
    protected void type(By locator, String text) {
        WebElement el = waitVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    /** Click a clickable element. */
    protected void click(By locator) {
        waitClickable(locator).click();
    }

    /** Best-effort presence check; returns false if the element is missing after the wait window. */
    protected boolean isPresent(By locator) {
        try {
            waitVisible(locator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click via JavaScript, bypassing overlays that would intercept a native
     * Selenium click. Useful for Chakra-styled radio buttons and checkboxes
     * where the real &lt;input&gt; is visually hidden behind a styled &lt;div&gt;.
     */
    protected void jsClick(By locator) {
        WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }
}

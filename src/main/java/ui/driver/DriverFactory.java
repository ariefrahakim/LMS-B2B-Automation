package ui.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

/**
 * Centralised WebDriver factory.
 *
 * We deliberately keep ONE Chrome instance alive for the whole JVM run
 * (registered via a JVM shutdown hook) instead of spinning a fresh browser for
 * every Cucumber scenario. Reasons:
 *   1. Spinning up Chrome takes ~2s per scenario; the cumulative overhead is
 *      significant on multi-scenario features.
 *   2. Cloudflare's bot heuristics flag repeated brand-new Chrome sessions from
 *      the same IP; reusing one session bypasses that noise.
 *
 * The Cucumber Hooks class (ui.stepdefs.Hooks, in the test source set) resets
 * URL/state between scenarios.
 */
public class DriverFactory {

    private static WebDriver driver;

    /** Idempotent — starts Chrome only on first call, returns the cached driver afterwards. */
    public static synchronized WebDriver start() {
        if (driver != null) return driver;

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1440,900");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        // Stealth flag — hides the "AutomationControlled" bit that Cloudflare
        // uses to fingerprint headless Chrome.
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        // Ensure the browser exits cleanly at JVM shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (driver != null) driver.quit();
            } catch (Exception ignored) {}
        }));

        return driver;
    }

    /** @return the shared WebDriver (starts one if needed). */
    public static WebDriver get() {
        return start();
    }

    /** Explicit quit — normally not needed; the JVM shutdown hook handles it. */
    public static synchronized void quit() {
        if (driver != null) {
            try { driver.quit(); } catch (Exception ignored) {}
            driver = null;
        }
    }
}

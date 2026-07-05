package ui.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Centralised WebDriver factory.
 *
 * We deliberately keep ONE browser instance alive for the whole JVM run
 * (registered via a JVM shutdown hook) instead of spinning a fresh one for
 * every Cucumber scenario. Reasons:
 *   1. Spinning up a browser takes ~2s per scenario; cumulative overhead is
 *      significant on multi-scenario features.
 *   2. Cloudflare's bot heuristics flag repeated brand-new sessions from the
 *      same IP; reusing one session bypasses that noise.
 *
 * Browser selection is driven by the `browser` system property:
 *   -Dbrowser=chrome  (default) → Google Chrome
 *   -Dbrowser=firefox           → Mozilla Firefox
 *
 * Headless mode is driven by `-Dheadless=true|false` (default: false).
 *
 * The Cucumber Hooks class (ui.stepdefs.Hooks, in the test source set) resets
 * URL/state between scenarios.
 */
public class DriverFactory {

    private static WebDriver driver;

    /** Idempotent — starts the browser only on first call, returns the cached driver afterwards. */
    public static synchronized WebDriver start() {
        if (driver != null) return driver;

        String browser = System.getProperty("browser", "chrome").toLowerCase();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        switch (browser) {
            case "firefox":
                driver = buildFirefox(headless);
                break;
            case "chrome":
            default:
                driver = buildChrome(headless);
                break;
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        // Ensure the browser exits cleanly at JVM shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (driver != null) driver.quit();
            } catch (Exception ignored) {}
        }));

        return driver;
    }

    private static WebDriver buildChrome(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (headless) options.addArguments("--headless=new");
        options.addArguments("--window-size=1440,900");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        // Stealth flag — hides the "AutomationControlled" bit that Cloudflare
        // uses to fingerprint headless Chrome.
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
        return new ChromeDriver(options);
    }

    private static WebDriver buildFirefox(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        if (headless) options.addArguments("-headless");
        options.addArguments("--width=1440", "--height=900");
        // Firefox stealth: don't send the "webdriver" flag in navigator; also
        // set a common desktop user-agent so Cloudflare fingerprinting is less
        // aggressive on GitHub Actions IPs.
        options.addPreference("dom.webdriver.enabled", false);
        options.addPreference("useAutomationExtension", false);
        options.addPreference("general.useragent.override",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:120.0) Gecko/20100101 Firefox/120.0");
        return new FirefoxDriver(options);
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

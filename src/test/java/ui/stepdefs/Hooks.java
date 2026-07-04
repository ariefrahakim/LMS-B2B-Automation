package ui.stepdefs;

import io.cucumber.java.Before;
import ui.driver.DriverFactory;

/**
 * Cucumber hooks.
 *
 * We reuse a single WebDriver across the whole JVM run (see
 * {@link DriverFactory}) — @After no longer quits the browser; the JVM
 * shutdown hook in {@link DriverFactory} does it once at exit.
 *
 * The @Before hook keeps its purpose: guarantee that a WebDriver exists before
 * any step runs, even if the previous scenario somehow closed it.
 */
public class Hooks {

    @Before
    public void ensureBrowserReady() {
        DriverFactory.start();
    }
}

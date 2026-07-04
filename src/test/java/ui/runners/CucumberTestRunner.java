package ui.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * TestNG entry point for the Cucumber (BDD) suite.
 *
 * `features`  — where the .feature files live (Gherkin).
 * `glue`      — package(s) scanned for step definitions + hooks.
 * `plugin`    — reporters. `pretty` prints to console; `html`/`json` write reports
 *                consumable by CI or dashboards.
 * `tags`      — allows the CI to select which scenarios run (e.g. `-Dcucumber.filter.tags=@positive`).
 */
@CucumberOptions(
        features = {"src/test/resources/features"},
        glue = {"ui.stepdefs"},
        plugin = {
                "pretty",
                "html:build/cucumber-reports/report.html",
                "json:build/cucumber-reports/report.json"
        },
        monochrome = true
)
public class CucumberTestRunner extends AbstractTestNGCucumberTests {
}

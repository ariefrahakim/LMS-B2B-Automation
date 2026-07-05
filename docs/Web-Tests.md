# Web UI Test Suite (Selenium + Cucumber BDD)

Target: `https://lms-b2b.do.dibimbing.id/dibimbingqa/login` (Next.js SPA).

The web suite uses the **Page Object Model** (POM) for maintainability and
**Cucumber / Gherkin** for Behaviour-Driven scenarios — so acceptance criteria
read like plain English and non-developers can review them.

Current status: **13 scenarios, 0 failures** across two feature files.

---

## 1. Layout

```
src/main/java/ui/
├── driver/DriverFactory.java        Single-instance Chrome WebDriver (reused across scenarios)
├── locators/                        Centralised locators (one file per page)
│   ├── LoginLocator.java
│   ├── DashboardLocator.java
│   └── EmployeeLocator.java
└── pages/                           Page Objects — behaviour only, no locators
    ├── BasePage.java                Common wait / click / type / jsClick helpers
    ├── LoginPage.java               /login page
    ├── DashboardPage.java           Post-login shell probe
    └── EmployeePage.java            /admin/employee — list + Add-Employee modal

src/test/java/ui/
├── stepdefs/                        Cucumber step definitions
│   ├── Hooks.java                   Ensures WebDriver is ready before each scenario
│   ├── LoginSteps.java              Steps for login.feature
│   └── EmployeeSteps.java           Steps for employee.feature
└── runners/CucumberTestRunner.java  TestNG entry point that runs .feature files

src/test/resources/features/
├── login.feature                    Login positive + negative scenarios
└── employee.feature                 Employee CRUD positive + negative scenarios
```

**Why POM?** Selectors live in one place per screen, so a UI change requires
editing one file, not every test.
**Why the locator split?** Selectors change most often (design tweaks). Isolating
them in their own package keeps every future edit surgical.
**Why BDD?** Feature files double as living documentation.

---

## 2. Test scenarios

### 2.1 `login.feature` — 8 scenarios

| # | Tag | Scenario | Assertion |
|---|-----|----------|-----------|
| 1 | @positive @smoke | Successful login with valid credentials | Redirects off `/login` AND dashboard nav visible |
| 2 | @positive | Login page renders required inputs | URL is `/login`, form fields present |
| 3 | @negative | Login fails with a wrong password | URL still `/login` |
| 4 | @negative | Login fails with an unknown email | URL still `/login` |
| 5 | @negative | Blank email + valid password (Scenario Outline row) | URL still `/login` |
| 6 | @negative | Valid email + blank password (Scenario Outline row) | URL still `/login` |
| 7 | @negative | Malformed email + valid password (Scenario Outline row) | URL still `/login` |
| 8 | @negative | Blank email + blank password (Scenario Outline row) | URL still `/login` |

### 2.2 `employee.feature` — 5 scenarios

| # | Tag | Scenario | Assertion |
|---|-----|----------|-----------|
| 1 | @positive @smoke | Employee list page renders with a total count | Count widget renders non-empty; first row visible |
| 2 | @positive | Add-employee modal renders all required inputs | Name / email / role / phone / gender / submit visible |
| 3 | @negative | Add an employee with a duplicate email is rejected | Modal stays open after submit |
| 4 | @negative | Submitting the add-employee form with blank required fields is rejected | Modal stays open after submit |
| 5 | @negative | Searching for a non-existent employee yields no rows | No rows visible after debounced search |

**Design note.** The Employee happy-path "create with valid data end-to-end" is
covered at the API layer (`EmployeeCRUDTest` in the GraphQL suite). Filling
every dependent dropdown (Program Studi, Angkatan) through the UI adds
accidental complexity to the BDD scenario without adding coverage the API
tests don't already give us.

---

## 3. Running

```bash
./gradlew webTest                       # Chrome (default browser), headless (default)
./gradlew webTestChrome                 # Explicit Chrome run
./gradlew webTestFirefox                # Cross-browser: Firefox run
./gradlew webTest -Dbrowser=firefox     # Same effect as webTestFirefox
./gradlew webTest -Dheadless=false      # Open a visible browser locally
```

Browser is picked at runtime from the `browser` system property inside
`DriverFactory.start()` — Chrome or Firefox. Reports go to separate
folders (`build/reports/tests/webTestChrome/` vs.
`build/reports/tests/webTestFirefox/`) so both survive a full-matrix run.

Filter by Cucumber tag:

```bash
./gradlew webTest -Dcucumber.filter.tags="@positive"
./gradlew webTest -Dcucumber.filter.tags="@negative"
./gradlew webTest -Dcucumber.filter.tags="@smoke"
```

Reports (regenerated per run):

- Console: `pretty` Cucumber reporter (inline in the Gradle output).
- HTML   : `build/cucumber-reports/report.html`
- JSON   : `build/cucumber-reports/report.json`
- TestNG : `build/reports/tests/webTest/index.html`

---

## 4. Adding a new page / feature

1. Add locators in `src/main/java/ui/locators/XxxLocator.java` — one
   `public static final By` per element.
2. Add the Page Object in `src/main/java/ui/pages/XxxPage.java` extending
   `BasePage`. It should import the locators from step 1 and only contain
   behaviour (open, fillForm, submit, isLoaded, ...).
3. Add a `.feature` file in `src/test/resources/features/`.
4. Add step definitions in `src/test/java/ui/stepdefs/XxxSteps.java`.
   Step defs only orchestrate Page Objects and assert outcomes — no direct
   Selenium code and no locator strings.
5. Re-run `./gradlew webTest`.

---

## 5. Environment / stability notes

- **WebDriver reuse.** We keep a *single* Chrome across all scenarios in the
  JVM run (see `DriverFactory`). Reasons:
  1. Spinning up Chrome costs ~2 s per scenario.
  2. Cloudflare's bot heuristics flag repeated brand-new Chrome sessions from
     the same IP.
  A JVM shutdown hook quits the browser at process exit.

- **Cookie reset per login.** Because the browser is reused, `LoginPage.open()`
  clears cookies before navigating to `/login`; otherwise an authenticated
  session from an earlier scenario would redirect us straight to `/dashboard`.

- **Chakra-hidden inputs.** Radio / checkbox inputs are visually hidden and
  covered by a styled `<div>`, which intercepts native Selenium clicks.
  `BasePage.jsClick()` bypasses that with a `JavascriptExecutor.click()`.

- **Cross-browser (Chrome + Firefox).** `DriverFactory` reads
  `-Dbrowser=chrome|firefox` (default `chrome`) and instantiates the matching
  `ChromeDriver` or `FirefoxDriver`. `WebDriverManager` auto-downloads the
  right driver binary in both cases, so no manual driver setup is required.
  GitHub-hosted runners already have Chrome; the Firefox CI job explicitly
  installs it via `browser-actions/setup-firefox@v1`.

- **Stealth flags.** `--disable-blink-features=AutomationControlled` +
  realistic User-Agent hide the "AutomationControlled" bit that Cloudflare
  fingerprints headless Chrome by.

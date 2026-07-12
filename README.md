# LMS B2B Automation — Combined API + Web Test Suite

Single repository holding **three** independent test layers for the Dibimbing
platforms, all driven by the same Gradle project.

| Layer | Target | Stack | Style |
|-------|--------|-------|-------|
| REST API | `https://sport-reservation-2-api-bootcamp.do.dibimbing.id/api/v1` | Rest-Assured + TestNG | Data-object POST/GET/PUT/DELETE |
| GraphQL API | `https://lmsb2b.do.dibimbing.id/graphql` | Rest-Assured + TestNG | Module-per-package CRUD + Negative + Data-Driven |
| Web UI | `https://lms-b2b.do.dibimbing.id/dibimbingqa/login` | Selenium 4 + Cucumber BDD | Page-Object Model with centralised locators |

**Current status**

| Suite | Test cases | Failures |
|-------|-----------|----------|
| `sportActivityTest` | 5 | 0 |
| `graphqlTest` | 67 | 0 |
| `webTest` | 13 | 0 |
| **Total** | **85** | **0** |

---

## 1. Repository layout

```
LMS-B2B-Automation/
├── build.gradle                     Gradle deps + per-suite tasks
├── .github/workflows/ci.yml         CI with per-suite dropdown + secret injection
├── postman/
│   └── LMS-B2B-GraphQL.postman_collection.json   Mirrors the Java GraphQL suite
├── docs/
│   ├── GraphQL-Tests.md             Deep dive on GraphQL suite
│   └── Web-Tests.md                 Deep dive on Selenium + BDD suite
└── src/
    ├── main/java/
    │   ├── api/                     API-side production code
    │   │   └── body/                Request-body builders (JSON payload objects)
    │   │       ├── auth/, sportActivity/
    │   │       └── graphql/         One package per GraphQL module
    │   │           ├── auth/, company/, angkatan/, employee/,
    │   │           ├── division/, program/, announcement/,
    │   │           └── bootcamp/, media/
    │   ├── ui/                      Web-UI production code
    │   │   ├── driver/DriverFactory Single-instance Chrome (JVM shutdown hook)
    │   │   ├── locators/            Centralised By locators, one file per page
    │   │   │   ├── LoginLocator.java
    │   │   │   ├── DashboardLocator.java
    │   │   │   └── EmployeeLocator.java
    │   │   └── pages/               Page Objects — behaviour only
    │   │       ├── BasePage.java        wait / click / type / jsClick helpers
    │   │       ├── LoginPage.java
    │   │       ├── DashboardPage.java
    │   │       └── EmployeePage.java
    │   └── utils/                   Shared helpers (ConfigReader, Utils)
    ├── test/java/
    │   ├── api/
    │   │   ├── base/                BaseTest, BaseGraphQLTest (setup + auth helper)
    │   │   └── tests/
    │   │       ├── auth/, sportActivity/            REST API tests
    │   │       └── graphql/
    │   │           ├── NegativeAssertions.java      Shared helper
    │   │           ├── auth/, system/, company/,
    │   │           ├── angkatan/, employee/, division/,
    │   │           ├── program/, announcement/,
    │   │           └── bootcamp/, media/            Per-module CRUD + Negative
    │   ├── ui/
    │   │   ├── runners/CucumberTestRunner
    │   │   └── stepdefs/                            LoginSteps, EmployeeSteps, Hooks
    │   └── runner/
    │       ├── testng.xml                Master (imports the two API suite files)
    │       ├── sportActivityTestng.xml   REST API only
    │       ├── graphqlTestng.xml         GraphQL modules only
    │       └── webTestng.xml             Cucumber runner only
    └── test/resources/
        ├── features/                     Gherkin .feature files
        │   ├── login.feature
        │   └── employee.feature
        └── json/                         Fixtures (angkatan_data.json, ...)
```

**Why `api/` and `ui/` at the top?** Cleanly separates two independent concerns,
minimises accidental cross-imports, keeps compile times isolated if this ever
grows into Gradle sub-projects, and makes it obvious where to add a future
test type (`mobile/` for Appium, `performance/` for JMeter, ...).

**Why the locator / page / stepdef three-layer split for Web?** Selectors change
most often (design tweaks), page behaviour changes less often (new flows),
Gherkin scenarios change least. Isolating each axis keeps every future edit
surgical.

---

## 2. Quick start

Each suite has its own Gradle task so CI stages can run focused subsets:

```bash
./gradlew sportActivityTest             # REST API only (Sport Activity)
./gradlew graphqlTest                   # GraphQL only (all 10 modules)

./gradlew webTest                       # Selenium + Cucumber (Chrome by default)
./gradlew webTestChrome                 # Explicit Chrome run
./gradlew webTestFirefox                # Cross-browser: Firefox run
./gradlew webTest -Dbrowser=firefox     # Same as webTestFirefox
./gradlew webTest -Dheadless=false      # Watch the browser locally

./gradlew test                          # umbrella = sportActivity + graphql
```

**Cross-browser** — `webTestChrome` and `webTestFirefox` write reports to
separate output folders (`build/reports/tests/webTestChrome/` and
`build/reports/tests/webTestFirefox/`) so a full-matrix run keeps both intact.

Reports (regenerated per run):

- REST API: `build/reports/tests/sportActivityTest/index.html`
- GraphQL: `build/reports/tests/graphqlTest/index.html`
- Web UI TestNG: `build/reports/tests/webTest/index.html`
- Web UI Cucumber: `build/cucumber-reports/report.html`

Filter Cucumber scenarios by tag — the `-Dcucumber.filter.tags` system property is
propagated to the runner by `build.gradle` (both the umbrella `test` task and the
`webTest*` tasks). Omit the flag to run every scenario.

```bash
./gradlew webTest -Dcucumber.filter.tags="@positive"
./gradlew webTest -Dcucumber.filter.tags="@negative"
./gradlew webTest -Dcucumber.filter.tags="@smoke"

# Boolean expressions are supported (Cucumber tag expression syntax):
./gradlew webTest         -Dcucumber.filter.tags="@smoke and @positive"
./gradlew webTest         -Dcucumber.filter.tags="@smoke and not @wip"
./gradlew webTestFirefox  -Dcucumber.filter.tags="@smoke"     # smoke on Firefox
```

Currently tagged `@smoke` (fast sanity subset, ~2 scenarios):

- `login.feature` → *Successful login with valid credentials*
- `employee.feature` → *Employee list page renders with a total count*

---

## 3. Configuration

`src/resources/config.properties` is **gitignored** (keeps credentials out of
git). Copy the template and fill in values:

```bash
cp src/resources/config.properties.example src/resources/config.properties
```

Then edit the file:

```properties
# REST API
baseUrl=https://sport-reservation-2-api-bootcamp.do.dibimbing.id/api/v1
email=<SPORT_API_EMAIL>
password=<SPORT_API_PASSWORD>

# GraphQL API (LMS B2B)
url=https://lmsb2b.do.dibimbing.id/graphql
usernameGraphQL=<GRAPHQL_BASIC_USERNAME>                # Basic Auth (gateway)
passwordGraphQL=<GRAPHQL_BASIC_PASSWORD>

# Web app (Selenium target) + admin user for GraphQL login mutation
webUrl=https://lms-b2b.do.dibimbing.id/dibimbingqa/login
emailWeb=<LMS_ADMIN_EMAIL>
passwordWeb=<LMS_ADMIN_PASSWORD>
companyId=<COMPANY_ID>
companySlug=dibimbingqa
```

In CI the values are injected from GitHub Secrets — see [§5](#5-github-actions-ci)
below.

---

## 4. Test coverage summary

### 4.1 REST API — `sportActivityTest` (5 test cases)

`api.tests.sportActivity.*` — Login → Create → Get by id → Update → Delete
sport activity. Ordered TestNG flow that persists a bearer token + activity id
across steps.

### 4.2 GraphQL API — `graphqlTest` (67 test cases, 10 modules)

| Module | Positive class | Negative class | Notes |
|--------|----------------|----------------|-------|
| Auth | `LoginGraphQLTest`, `GetMeTest` | `AuthNegativeTest` | Persists `sid_b2b` session cookie for downstream modules |
| System | `PingTest` | — | Smoke |
| Company | `CompanyTest` | `CompanyNegativeTest` | Read-only |
| Angkatan (cohort) | `AngkatanCRUDTest`, `AngkatanDataDrivenTest` | `AngkatanNegativeTest` | Data-driven from `src/resources/json/angkatan_data.json` |
| Employee | `EmployeeCRUDTest` | `EmployeeNegativeTest` | Full CRUD; update derives `username` from email (see § 7) |
| Division (class) | `DivisionCRUDTest` | `DivisionNegativeTest` | Full CRUD |
| Program (program studi) | `ProgramCRUDTest` | `ProgramNegativeTest` | Full CRUD |
| Announcement | `AnnouncementCRUDTest` | `AnnouncementNegativeTest` | Delete assertion omitted — see § 7 |
| Bootcamp (fast-track) | `BootcampCRUDTest` | `BootcampNegativeTest` | Full CRUD |
| Media Library | `MediaLibraryTest` | `MediaNegativeTest` | List / countMedias / availableStorage / mediaById |

Every module covers: happy-path CRUD, non-existent id (get/update/delete),
missing NON_NULL input, and wrong scalar type (where applicable).

### 4.3 Web UI — `webTest` (13 scenarios in 2 feature files)

**`login.feature` — 8 scenarios**

| Tag | Scenario | Assertion |
|-----|----------|-----------|
| @positive @smoke | Login with valid credentials | Redirects off `/login` + dashboard nav visible |
| @positive | Login page renders required inputs | URL `/login`, form fields present |
| @negative | Wrong password | URL still `/login` |
| @negative | Unknown email | URL still `/login` |
| @negative | Scenario Outline × 4 rows: blank email / blank password / malformed email / both blank | URL still `/login` |

**`employee.feature` — 5 scenarios**

| Tag | Scenario | Assertion |
|-----|----------|-----------|
| @positive @smoke | Employee list renders with a total count | Count widget non-empty; first row visible |
| @positive | Add-employee modal renders all required inputs | Name / email / role / phone / gender / submit visible |
| @negative | Add employee with duplicate email | Modal stays open |
| @negative | Submit blank form | Modal stays open |
| @negative | Search for non-existent employee | No rows visible |

Full scenario-by-scenario detail: `docs/Web-Tests.md`.

---

## 5. GitHub Actions CI

Workflow: `.github/workflows/ci.yml`.

### Triggers

- **Automatic** — every push / PR to `main` runs the three suites in parallel.
- **Manual** — Actions tab → **CI → Run workflow** panel; the `suite` dropdown
  lets you choose:

  | Value | What runs |
  |-------|-----------|
  | `all` (default) | Every suite in parallel (REST + GraphQL + web-chrome + web-firefox) |
  | `sportActivity` | REST API only |
  | `graphql` | GraphQL API only |
  | `web-chrome` | Selenium + Cucumber, Chrome headless |
  | `web-firefox` | Selenium + Cucumber, Firefox headless |

  This is powered by `workflow_dispatch.inputs.suite` in the YAML — GitHub
  renders it as a native dropdown.

### Required secrets

Add these to Settings → Secrets and variables → Actions:

| Secret name | Replaces in config.properties |
|-------------|------------------------------|
| `LMS_ADMIN_EMAIL` | `emailWeb` |
| `LMS_ADMIN_PASSWORD` | `passwordWeb` |
| `GRAPHQL_BASIC_USERNAME` | `usernameGraphQL` |
| `GRAPHQL_BASIC_PASSWORD` | `passwordGraphQL` |
| `SPORT_API_EMAIL` | `email` |
| `SPORT_API_PASSWORD` | `password` |
| `COMPANY_ID` | `companyId` |

Or via `gh` CLI (once logged in to the target account):

```bash
gh secret set LMS_ADMIN_EMAIL        -R <owner>/LMS-B2B-Automation --body '<value>'
gh secret set LMS_ADMIN_PASSWORD     -R <owner>/LMS-B2B-Automation --body '<value>'
gh secret set GRAPHQL_BASIC_USERNAME -R <owner>/LMS-B2B-Automation --body '<value>'
gh secret set GRAPHQL_BASIC_PASSWORD -R <owner>/LMS-B2B-Automation --body '<value>'
gh secret set SPORT_API_EMAIL        -R <owner>/LMS-B2B-Automation --body '<value>'
gh secret set SPORT_API_PASSWORD     -R <owner>/LMS-B2B-Automation --body '<value>'
gh secret set COMPANY_ID             -R <owner>/LMS-B2B-Automation --body '<value>'
```

Each CI job copies `config.properties.example` → `config.properties` and then
`sed`-injects only the secrets that job needs (the Sport-Activity job never
sees the GraphQL credentials, etc.).

Non-sensitive values (`baseUrl`, `url`, `webUrl`, `companySlug`) stay in the
template.

TestNG + Cucumber reports are uploaded as build artifacts on every run.

---

## 6. Postman collection

Import `postman/LMS-B2B-GraphQL.postman_collection.json`.

- One Postman folder per module, split into **Positive** and **Negative**
  sub-folders (mirrors the Java package structure).
- Global test-script asserts HTTP 200 + no `errors` on positive requests
  (skipped on requests whose name starts with `[NEG]`).
- **Auth → Positive → Login** runs a test-script that persists the returned
  `sid_b2b` cookie into a collection variable, so every downstream request is
  authenticated automatically.
- CRUD folders (`Angkatan`, `Employee`, `Division`, `Program`, `Announcement`,
  `Bootcamp`) capture the `id` returned by `Create ...` into a collection
  variable that the subsequent `GetById / Update / Delete` requests read.

---

## 7. Known live-server quirks

Documented so tests intentionally accommodate them rather than fail.

- **`updateEmployee` requires `username`.** Server-side resolver calls
  `formatUsername(input.username).toLowerCase()` unconditionally → sending
  null throws `Cannot read properties of undefined (reading 'toLowerCase')`.
  Both `EmployeeBodies.update()` and the Postman "Update Employee" request
  include a `username` derived from the email local-part.
- **`deleteAnnouncement` blocked by FK constraint.** When an announcement is
  created with `isForAllEmployee: true` the DB inserts rows in
  `user_announcement` and the delete on `announcement` fails with a foreign-
  key violation. `AnnouncementCRUDTest` omits the delete assertion; the
  negative-path "delete non-existent" scenario still runs.
- **Mutation response returns pre-update snapshot** for Employee / Division /
  Program / Announcement / Bootcamp. CRUD tests re-fetch by id to confirm
  persistence rather than trusting the mutation's echo.
- **`angkatanById` types `id` as `Float!`** (schema quirk). Body helpers accept
  `Number` so an `int` can be passed transparently.

---

## 8. Local Selenium tips

- Chrome is auto-installed by
  [WebDriverManager](https://github.com/bonigarcia/webdrivermanager) — no
  manual driver setup required.
- We reuse **one** Chrome across all Cucumber scenarios (see
  `ui/driver/DriverFactory`). Reasons: fresh browsers cost ~2 s each; Cloudflare
  flags repeated brand-new headless sessions from the same IP.
- `LoginPage.open()` clears cookies before navigating so a reused browser
  doesn't skip `/login` and land on the dashboard.
- `BasePage.jsClick()` bypasses Chakra's visually-hidden inputs (radios,
  checkboxes) by executing `element.click()` via JavaScript.

---

## 9. Docs

- `docs/GraphQL-Tests.md` — GraphQL suite deep dive: per-module design,
  negative-error signalling, adding a new module.
- `docs/Web-Tests.md` — Selenium/BDD suite deep dive: scenario matrix,
  stability notes, adding a new page/feature.

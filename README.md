# SportReservationAPI — Combined API + Web Automation

Single repository that holds **both** automated test layers for the Dibimbing
platforms:

| Layer | Target | Stack | Style |
|-------|--------|-------|-------|
| REST API | `https://sport-reservation-2-api-bootcamp.do.dibimbing.id/api/v1` | Rest-Assured + TestNG | Data-object POST/GET/PUT/DELETE |
| GraphQL API | `https://lmsb2b.do.dibimbing.id/graphql` | Rest-Assured + TestNG | Module-per-package CRUD + Negative + Data-Driven |
| Web UI | `https://lms-b2b.do.dibimbing.id/dibimbingqa/login` | Selenium 4 + Cucumber BDD | Page-Object Model + Gherkin |

---

## Repository layout

```
SportReservationAPI/
├── build.gradle                       Gradle deps + `test` (API) + `webTest` (UI) tasks
├── postman/                           Ready-to-import Postman collection
│   └── LMS-B2B-GraphQL.postman_collection.json
├── docs/
│   ├── GraphQL-Tests.md               Deep dive on GraphQL suite
│   └── Web-Tests.md                   Deep dive on Selenium + BDD suite
└── src/
    ├── main/java/
    │   ├── api/                       API-side production code
    │   │   └── body/                  Request-body builders (JSON payload objects)
    │   │       ├── auth/, sportActivity/
    │   │       └── graphql/           One package per GraphQL module
    │   │           ├── auth/, company/, angkatan/, employee/,
    │   │           ├── division/, program/, announcement/,
    │   │           └── bootcamp/, media/
    │   ├── ui/                        Web-UI production code
    │   │   ├── driver/DriverFactory   ThreadLocal Selenium WebDriver
    │   │   ├── locators/              Centralised By locators, one file per page
    │   │   └── pages/                 Page Objects — behaviour only, import locators
    │   └── utils/                     Shared helpers (ConfigReader, Utils)
    ├── test/java/
    │   ├── api/
    │   │   ├── base/                  BaseTest + BaseGraphQLTest (setup + auth helper)
    │   │   └── tests/
    │   │       ├── auth/, sportActivity/          Existing REST tests
    │   │       └── graphql/
    │   │           ├── NegativeAssertions.java    Shared helper
    │   │           ├── auth/, system/, company/,
    │   │           ├── angkatan/, employee/, division/,
    │   │           ├── program/, announcement/,
    │   │           └── bootcamp/, media/          Per-module CRUD + Negative
    │   ├── ui/
    │   │   ├── runners/CucumberTestRunner
    │   │   └── stepdefs/                          Cucumber step defs + Hooks
    │   └── runner/testng.xml                      Master suite (drives everything)
    └── test/resources/
        └── features/                              Gherkin .feature files
```

**Why `api/` and `ui/` at the top?** It cleanly separates two independent
concerns, minimises accidental cross-imports, keeps compile times isolated
if we ever split into Gradle sub-projects, and makes it obvious where to add a
future test type (e.g. `mobile/` for Appium, `performance/` for JMeter).

---

## Quick start

Each suite has its own Gradle task so CI stages can run focused subsets:

```bash
./gradlew sportActivityTest             # REST API only (Sport Activity)
./gradlew graphqlTest                   # GraphQL only (all modules)
./gradlew webTest                       # Selenium + Cucumber (headless Chrome)
./gradlew webTest -Dheadless=false      # watch the browser locally

./gradlew test                          # umbrella = sportActivityTest + graphqlTest
```

Current status:
- `graphqlTest` — **67 test cases, 0 failures**
- `webTest`     — **13 scenarios, 0 failures** (8 login: 1 smoke + 1 form-render + 6 negative; 5 employee: 2 positive + 3 negative)

Reports:

- API: `build/reports/tests/test/index.html`
- Web: `build/cucumber-reports/report.html`

---

## GitHub Actions CI

Workflow: `.github/workflows/ci.yml`.

- **Automatic** — every push / PR to `main` or `master` runs all three suites
  in parallel (sportActivity, graphql, web).
- **Manual** — open the **Actions → CI → Run workflow** panel; the `suite`
  dropdown lets you choose one of:

  | Value           | What runs                             |
  |-----------------|---------------------------------------|
  | `all` (default) | Every suite in parallel               |
  | `sportActivity` | REST API only                         |
  | `graphql`       | GraphQL API only                      |
  | `web`           | Selenium + Cucumber (headless Chrome) |

  This is powered by `workflow_dispatch.inputs.suite` in the YAML — GitHub
  renders it as a native dropdown in the run-workflow UI.

TestNG + Cucumber reports are uploaded as build artifacts on every run.

---

## Configuration

All credentials & endpoints live in a single file:
`src/resources/config.properties`.

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

Copy `src/resources/config.properties.example` → `src/resources/config.properties`
and fill in the values locally. In CI they are injected from GitHub Secrets
(see the [GitHub Actions CI](#github-actions-ci) section).

---

## Test coverage summary

### GraphQL API — 72 test cases across 10 modules, 100% pass, 0 errors

| Module | Positive class | Negative class | Notes |
|--------|----------------|----------------|-------|
| Auth | `LoginGraphQLTest`, `GetMeTest` | `AuthNegativeTest` | Persists `sid_b2b` session cookie |
| System | `PingTest` | — | Smoke |
| Company | `CompanyTest` | `CompanyNegativeTest` | Read-only |
| Angkatan (cohort) | `AngkatanCRUDTest`, `AngkatanDataDrivenTest` | `AngkatanNegativeTest` | Data-driven from `angkatan_data.json` |
| Employee | `EmployeeCRUDTest` | `EmployeeNegativeTest` | Full CRUD |
| Division (class) | `DivisionCRUDTest` | `DivisionNegativeTest` | Full CRUD |
| Program (program studi) | `ProgramCRUDTest` | `ProgramNegativeTest` | Full CRUD |
| Announcement | `AnnouncementCRUDTest` | `AnnouncementNegativeTest` | Delete blocked by FK on live env — see `AnnouncementCRUDTest` javadoc |
| Bootcamp (fast-track) | `BootcampCRUDTest` | `BootcampNegativeTest` | Full CRUD |
| Media Library | `MediaLibraryTest` | `MediaNegativeTest` | List / countMedias / availableStorage / mediaById |

Every module includes: wrong id, wrong scalar type (where applicable), and
missing NON_NULL field.

### Web UI (Cucumber) — 13 scenarios across 2 feature files

**`login.feature`** — 8 scenarios
- @positive @smoke : login with valid credentials → redirected away from `/login`
- @positive       : login page renders required inputs
- @negative       : wrong password → stays on `/login`
- @negative       : unknown email → stays on `/login`
- @negative       : Scenario Outline × 4 rows (blank email, blank password, malformed email, both blank)

**`employee.feature`** — 5 scenarios
- @positive @smoke : Employee list renders with a total count
- @positive       : Add-employee modal renders all required inputs
- @negative       : Add employee with duplicate email → modal stays open
- @negative       : Submit blank form → modal stays open
- @negative       : Search for non-existent employee → no rows

See `docs/Web-Tests.md` for scenario-by-scenario detail.

---

## Postman

Import `postman/LMS-B2B-GraphQL.postman_collection.json`.

- One folder per module, split into **Positive** and **Negative** sub-folders
  (mirrors the Java package structure).
- Global test-script asserts HTTP 200 + no `errors` on positive requests.
- Auth → Login persists the `sid_b2b` cookie into a collection variable so all
  subsequent requests are authenticated.

---

## Docs

- `docs/GraphQL-Tests.md` — GraphQL suite deep dive.
- `docs/Web-Tests.md` — Selenium/BDD suite deep dive.

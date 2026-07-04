# GraphQL API Test Suite

Target endpoint: `https://lmsb2b.do.dibimbing.id/graphql`
Web app under test: `https://lms-b2b.do.dibimbing.id/dibimbingqa/login`

---

## 1. Design principles

1. **Module-per-package.** Every business module (Auth, Company, Angkatan,
   Employee, Division, Program, Announcement, Bootcamp, Media, System) owns
   its own Java package under `api.tests.graphql.<module>` and its own body
   class under `api.body.graphql.<module>`. Both the positive (CRUD) and
   negative test classes for a module live side-by-side in the same package.
2. **No `crud/` or `negative/` sub-folders.** The intent is to keep everything
   about a module in one place, so opening `angkatan/` shows you the full
   contract at a glance (positive + negative + data-driven).
3. **Shared assertion helpers.** `api.tests.graphql.NegativeAssertions` provides
   a single `assertGraphQLError()` helper reused across all *Negative test
   classes.
4. **Consolidated body classes.** Instead of one file per operation, each
   module exposes a single static-method class (`AngkatanBodies`,
   `EmployeeBodies`, ...) containing every payload for that module.

---

## 2. Directory layout

```
src/main/java/api/body/graphql/
├── GraphQLPayload.java                Tiny helper that builds { operationName, query, variables }
├── auth/AuthBodies.java               login / me / ping
├── company/CompanyBodies.java         myCompany / companyBySlug
├── angkatan/AngkatanBodies.java       list / byId / create / update / delete
├── employee/EmployeeBodies.java       CRUD
├── division/DivisionBodies.java       CRUD
├── program/ProgramBodies.java         CRUD
├── announcement/AnnouncementBodies.java CRUD
├── bootcamp/BootcampBodies.java       CRUD
└── media/MediaBodies.java             medias / countMedias / availableStorage / mediaById

src/test/java/api/tests/graphql/
├── NegativeAssertions.java            shared helper
├── auth/       LoginGraphQLTest.java, GetMeTest.java, AuthNegativeTest.java
├── system/     PingTest.java
├── company/    CompanyTest.java, CompanyNegativeTest.java
├── angkatan/   AngkatanCRUDTest.java, AngkatanDataDrivenTest.java, AngkatanNegativeTest.java
├── employee/   EmployeeCRUDTest.java, EmployeeNegativeTest.java
├── division/   DivisionCRUDTest.java, DivisionNegativeTest.java
├── program/    ProgramCRUDTest.java, ProgramNegativeTest.java
├── announcement/ AnnouncementCRUDTest.java, AnnouncementNegativeTest.java
├── bootcamp/   BootcampCRUDTest.java, BootcampNegativeTest.java
└── media/      MediaLibraryTest.java, MediaNegativeTest.java
```

---

## 3. Auth model

Two layers stacked:

1. **HTTP Basic Auth** on the gateway — always sent by `BaseGraphQLTest.baseRequest()`.
2. **Session cookie** `sid_b2b` — obtained by running `LoginGraphQLTest`
   first; persisted to `src/resources/json/graphql_session.json` and
   attached to subsequent authenticated requests via
   `BaseGraphQLTest.authRequest()`.

---

## 4. Test scenarios

### 4.1 Positive (CRUD)

Each `<Module>CRUDTest` runs `create → get → update → delete` as one flow.
The created id is shared through a `static` field, ordering is enforced with
TestNG `priority` and `dependsOnMethods`.

For updates the mutation response is often stale (the server returns the
pre-update snapshot). We verify the update by re-fetching via the module's
get-by-id query.

### 4.2 Data-driven (Angkatan example)

`AngkatanDataDrivenTest` uses `@DataProvider` to iterate over
`src/resources/json/angkatan_data.json`; each row becomes one test case.
Every iteration creates and deletes its own record so re-runs are idempotent.

Adding a new case = adding a row to JSON, zero Java changes.

### 4.3 Negative scenarios (every module)

| Failure mode                     | Covered on           |
|----------------------------------|----------------------|
| Wrong password / companyId       | Auth                 |
| Missing session cookie for `me`  | Auth                 |
| Malformed email                  | Auth                 |
| Non-existent id (get/update/delete) | Every module module (except System which has no id-based queries) |
| Missing NON_NULL input field     | Angkatan, Division, Program, Announcement |
| Wrong scalar variable type       | Angkatan, Employee   |
| Duplicate email                  | Employee             |
| Non-existent slug                | Company              |

Assertions are made through the shared helper `NegativeAssertions.assertGraphQLError()`
which accepts three signalling patterns:

- HTTP 200 with a populated `errors` array,
- HTTP 200 with `data.<field>` being `null`,
- HTTP 400 with a validation payload (used by the server for schema-level
  violations such as missing NON_NULL variables).

---

## 5. Running

```bash
./gradlew test                             # full suite
./gradlew test --tests 'api.tests.graphql.employee.*'   # one module
```

Reports: `build/reports/tests/test/index.html`.

Current status: **72 test cases, 0 failures.**

---

## 6. Known backend limitations

- `deleteAnnouncement` on the live env is blocked by an FK constraint from
  `user_announcement` when `isForAllEmployee = true`. `AnnouncementCRUDTest`
  intentionally omits the delete assertion; the negative-path delete-with-
  non-existent-id still runs in `AnnouncementNegativeTest`.
- Mutation responses may return the pre-update snapshot. CRUD tests re-fetch
  to verify persistence.
- `angkatanById` etc. type their id as `Float!` (schema quirk). Our body
  helpers accept `Number` so callers can pass an `int`.

---

## 7. Postman

`postman/LMS-B2B-GraphQL.postman_collection.json` — one Postman folder per
module, split into Positive / Negative sub-folders, each request preloaded
with a test-script asserting the same properties as the Java suite.

The Login request stores `sid_b2b` into a collection variable via
`pm.collectionVariables.set(...)`, so downstream requests are already
authenticated.

---

## 8. Adding a new module

1. Add `api.body.graphql.<module>.<Module>Bodies` with static factory methods.
2. Add `api.tests.graphql.<module>.<Module>CRUDTest` for the happy path.
3. Add `api.tests.graphql.<module>.<Module>NegativeTest` (use
   `NegativeAssertions.assertGraphQLError`).
4. Register both classes in `src/test/java/runner/testng.xml` under a new
   `<test name="GraphQL - <Module>">` block.
5. Mirror the folder structure in the Postman collection.

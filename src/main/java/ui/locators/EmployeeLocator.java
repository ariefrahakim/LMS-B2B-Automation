package ui.locators;

import org.openqa.selenium.By;

/**
 * Centralised locators for the Employee module in the LMS B2B web app.
 *
 * Covers the list page (`/dibimbingqa/admin/employee`) and the modal form
 * that opens when clicking "Add Employee".
 */
public final class EmployeeLocator {

    private EmployeeLocator() {}

    // ---------------- List page ----------------

    /** Total employee count displayed on the tab header. */
    public static final By LIST_COUNT = By.id("employee-list-count");

    /** Wrapper around the search input on the list page. */
    public static final By SEARCH_WRAPPER = By.id("input-admin-employee-search");

    /** The actual <input> nested inside {@link #SEARCH_WRAPPER}. */
    public static final By SEARCH_INPUT = By.cssSelector("#input-admin-employee-search input");

    /** "Add Employee" primary button that opens the create-employee modal. */
    public static final By ADD_EMPLOYEE_BUTTON = By.id("button-add-employee");

    /** First detail-row button — used to detect the "row 0" in results. */
    public static final By FIRST_ROW_DETAIL_BUTTON = By.id("button-detail-employee-0");

    // ---------------- Add-Employee modal ----------------

    public static final By FORM_NAME = By.id("name");
    public static final By FORM_EMPLOYEE_ID = By.id("employeeId");
    public static final By FORM_EMAIL = By.id("email");
    public static final By FORM_PHONE = By.id("phoneNumber");
    public static final By FORM_ROLE = By.id("employeeRole");
    public static final By FORM_GENDER_MALE = By.id("add-employee-gender-radio-male");
    public static final By FORM_GENDER_FEMALE = By.id("add-employee-gender-radio-female");
    public static final By FORM_DATE_OF_BIRTH = By.id("dateOfBirth");
    public static final By FORM_NIK = By.id("nik");
    public static final By FORM_NPWP = By.id("npwp");
    public static final By FORM_SUBMIT = By.id("button-add-employee-submit");

    /** Toast/alert that shows on server response (success or error). */
    public static final By TOAST_ALERT = By.cssSelector("[role='alert']");
}

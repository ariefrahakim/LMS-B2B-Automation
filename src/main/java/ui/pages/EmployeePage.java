package ui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ui.locators.EmployeeLocator;

import java.time.Duration;

/**
 * Page Object for the Employee list + Add-Employee modal.
 *
 * Encapsulates the two flows exercised by the BDD feature file:
 *   - reading state (count / row visibility) on the list page
 *   - filling the modal form and submitting
 *
 * Locators live in {@link EmployeeLocator}; this class describes behaviour only.
 */
public class EmployeePage extends BasePage {

    /** Direct URL of the Employee list — the sidebar link route may lag on some builds. */
    public static final String LIST_PATH = "/admin/employee";

    public EmployeePage(WebDriver driver) {
        super(driver);
    }

    /**
     * Navigate to the Employee list page and wait until the count widget renders.
     *
     * The first cold navigation on CI can take ~20-40s (Cloudflare handshake +
     * initial SPA bundle + list GraphQL query). We use a generous 45s wait; if
     * that still fails we hard-refresh once and retry with the same window
     * before giving up. Subsequent navigations in the same JVM warm-hit and
     * complete well under the standard 15s wait.
     */
    public EmployeePage open() {
        driver.get(baseUrlRoot() + LIST_PATH);
        try {
            longWait().until(ExpectedConditions.visibilityOfElementLocated(EmployeeLocator.LIST_COUNT));
        } catch (Exception first) {
            // Cold start missed the deadline — refresh and give it one more chance.
            driver.navigate().refresh();
            longWait().until(ExpectedConditions.visibilityOfElementLocated(EmployeeLocator.LIST_COUNT));
        }
        return this;
    }

    /** Longer wait used only by cold-start navigations that hit Cloudflare + first SPA render. */
    private WebDriverWait longWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(45));
    }

    /** Derive the base app URL (scheme + host + first path segment) from `webUrl`. */
    private String baseUrlRoot() {
        String webUrl = utils.ConfigReader.getProperty("webUrl");
        // webUrl looks like https://host/companySlug/login → strip the trailing "/login".
        return webUrl.replaceAll("/login/?$", "");
    }

    /** @return the numeric count shown at the top of the Employee tab (waits for it to populate). */
    public String getListCount() {
        // The count widget renders empty first, then the number arrives after the
        // background query resolves. Wait for the text to become non-empty.
        return wait.until(d -> {
            String txt = d.findElement(EmployeeLocator.LIST_COUNT).getText().trim();
            return txt.isEmpty() ? null : txt;
        });
    }

    /** Type into the search box on the list. */
    public EmployeePage search(String keyword) {
        type(EmployeeLocator.SEARCH_INPUT, keyword);
        return this;
    }

    /** Open the Add-Employee modal and wait for the form to render. */
    public EmployeePage clickAddEmployee() {
        click(EmployeeLocator.ADD_EMPLOYEE_BUTTON);
        waitVisible(EmployeeLocator.FORM_NAME);
        return this;
    }

    /**
     * Fill the required fields of the Add-Employee modal.
     *
     * The form has additional required inputs (gender, employee-id, phone) that
     * are not documented as {@code NON_NULL} in the GraphQL schema but the
     * client-side validation still enforces them, so we populate them here for
     * a positive-path submission.
     *
     * @param name         mandatory display name
     * @param email        mandatory unique email
     * @param employeeRole free-text role, e.g. "employee"
     */
    public EmployeePage fillRequiredFields(String name, String email, String employeeRole) {
        type(EmployeeLocator.FORM_NAME, name);
        type(EmployeeLocator.FORM_EMPLOYEE_ID, "QA-" + System.currentTimeMillis());
        type(EmployeeLocator.FORM_EMAIL, email);
        type(EmployeeLocator.FORM_PHONE, "081234567890");
        type(EmployeeLocator.FORM_ROLE, employeeRole);
        // Pick a gender via the radio group. Chakra visually hides the real
        // <input> and intercepts native clicks with a styled div, so we click
        // through JavaScript instead.
        jsClick(EmployeeLocator.FORM_GENDER_MALE);
        return this;
    }

    /** Click Submit on the Add-Employee modal. */
    public EmployeePage submitForm() {
        click(EmployeeLocator.FORM_SUBMIT);
        return this;
    }

    /**
     * @return true when at least one detail-row button is present in the current
     * result set (i.e. the search returned results / list is non-empty).
     */
    public boolean firstRowExists() {
        return isPresent(EmployeeLocator.FIRST_ROW_DETAIL_BUTTON);
    }

    /** @return true if any Chakra toast with non-empty text is currently visible. */
    public boolean hasToast() {
        try {
            return wait.until(d -> {
                for (WebElement el : d.findElements(EmployeeLocator.TOAST_ALERT)) {
                    if (el.isDisplayed() && !el.getText().trim().isEmpty()) return true;
                }
                return false;
            });
        } catch (Exception e) {
            return false;
        }
    }

    /** @return true when the Add-Employee modal is still open (form fields present). */
    public boolean isAddModalOpen() {
        return isPresent(EmployeeLocator.FORM_NAME);
    }

    /**
     * @return true when every field the client-side form treats as required
     * is rendered inside the modal.
     */
    public boolean hasAllRequiredFields() {
        return isPresent(EmployeeLocator.FORM_NAME)
                && isPresent(EmployeeLocator.FORM_EMAIL)
                && isPresent(EmployeeLocator.FORM_ROLE)
                && isPresent(EmployeeLocator.FORM_PHONE)
                && isPresent(EmployeeLocator.FORM_SUBMIT);
    }
}

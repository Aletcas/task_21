package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.WaitForSelectorState;

public class DynamicControlsPage {
    private final Page page;
    private final Locator removeButton;
    private final Locator checkbox;

    public DynamicControlsPage(Page page) {
        this.page = page;
        this.removeButton = page.locator("button:has-text('Remove')");
        this.checkbox = page.locator("#checkbox");
    }

    public void clickRemoveButton() {
        removeButton.click();

        // Ждем исчезновения чекбокса (максимум 15 секунд)
        checkbox.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN)
                .setTimeout(15000));
    }

    public boolean isCheckboxVisible() {
        try {
            // Проверяем, виден ли чекбокс (ждем до 2 секунд)
            return checkbox.isVisible(new Locator.IsVisibleOptions().setTimeout(10000));
        } catch (TimeoutError e) {
            return false;
        }
    }
}


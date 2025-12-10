package context;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;

public class TestContext {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    public TestContext() {
        this.playwright = Playwright.create();

        // Для CI/CD лучше использовать headless режим
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(true); // или true для CI

        this.browser = playwright.chromium().launch(launchOptions);
        this.context = browser.newContext();
        this.page = context.newPage();

        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");
        page.waitForLoadState(LoadState.NETWORKIDLE); // Ждем загрузки
    }

    public BrowserContext getBrowserContext() {
        return context;
    }

    public Page getPage() {
        return page;
    }

    public void close() {
        if (page != null) {
            try { page.close(); } catch (Exception e) { /* игнорируем */ }
        }
        if (context != null) {
            try { context.close(); } catch (Exception e) { /* игнорируем */ }
        }
        if (browser != null) {
            try { browser.close(); } catch (Exception e) { /* игнорируем */ }
        }
        if (playwright != null) {
            try { playwright.close(); } catch (Exception e) { /* игнорируем */ }
        }
    }
}


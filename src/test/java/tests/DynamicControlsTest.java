package tests;

import context.TestContext;
import org.junit.jupiter.api.Test;
import pages.DynamicControlsPage;
import utils.Traceable;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicControlsTest implements Traceable {
    @Test
    public void testCheckboxRemoval() {
        TestContext testContext = new TestContext();

        try {
            withTracing(testContext.getBrowserContext(), "testCheckboxRemoval", () -> {
                DynamicControlsPage page = new DynamicControlsPage(testContext.getPage());

                testContext.getPage().navigate("https://the-internet.herokuapp.com/dynamic_controls");

                assertTrue(page.isCheckboxVisible());
                page.clickRemoveButton();
                assertFalse(page.isCheckboxVisible(),
                        "Чекбокс не исчез после нажатия кнопки Remove");
            });

        } finally {
            testContext.close();
        }
    }
}

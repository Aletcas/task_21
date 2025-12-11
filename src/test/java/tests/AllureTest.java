package tests;

import org.junit.jupiter.api.Test;
import io.qameta.allure.Allure;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AllureTest {
    @Test
    public void testAllureIsWorking() {
        Allure.step("Проверка работы Allure");
        assertTrue(true, "Allure должен работать");
        Allure.attachment("Test attachment", "This is test content");
    }
}

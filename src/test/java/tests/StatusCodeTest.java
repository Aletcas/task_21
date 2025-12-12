package tests;

import config.EnvironmentConfig;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.*;
import pages.StatusCodePage;
import utils.TestLogger;
import java.io.IOException;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class StatusCodeTest {
    private EnvironmentConfig config;
    private StatusCodePage statusCodePage;

    @BeforeEach
    public void setup() {
        String env = System.getProperty("env", "dev");
        System.setProperty("env", env);

        config = ConfigFactory.create(EnvironmentConfig.class);
        statusCodePage = new StatusCodePage(config.baseUrl());

        TestLogger.info("Environment: " + env);
        TestLogger.info("Base URL: " + config.baseUrl());
    }

    @Test
    public void testAllStatusCodes() throws IOException {
        TestLogger.startTest("testAllStatusCodes");

        try {
            Map<String, Integer> results = statusCodePage.getAllStatusCodes();
            TestLogger.info("Retrieved " + results.size() + " status codes");

            boolean allPassed = true;
            for (Map.Entry<String, Integer> entry : results.entrySet()) {
                String path = entry.getKey();
                int actualCode = entry.getValue();
                int expectedCode = Integer.parseInt(path.replaceAll("\\D+", ""));

                if (actualCode == expectedCode) {
                    TestLogger.info(String.format("%-25s → %-3d ✓", path, actualCode));
                } else {
                    TestLogger.error(String.format(
                            "%-25s → Expected: %d, Actual: %d ✗",
                            path, expectedCode, actualCode), null);
                    allPassed = false;
                }

                assertEquals(expectedCode, actualCode,
                        "Wrong status code for: " + path);
            }

            TestLogger.endTest("testAllStatusCodes", allPassed);

        } catch (Exception e) {
            TestLogger.error("Test failed with exception", e);
            TestLogger.endTest("testAllStatusCodes", false);
            throw e;
        }
    }
//    @Test
//    public void testConfigWorks() {
//        System.out.println("✅ Config loaded successfully!");
//        System.out.println("Base URL: " + config.baseUrl());
//        System.out.println("Environment: " + config.environment());
//        assertNotNull(config.baseUrl());
//    }
}

package utils;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Tracing;

import java.nio.file.Files;
import java.nio.file.Paths;

public interface Traceable {
    default void withTracing(BrowserContext context, String testName, Runnable test) {
        // Начинаем трассировку
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true)
                .setTitle(testName));

        Throwable testError = null;

        try {
            test.run();
            System.out.println("✓ Test passed, saving trace...");
        } catch (Throwable e) {
            testError = e;
            System.err.println("✗ Test failed! Saving trace for debugging...");
            System.err.println("Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        } finally {
            // Всегда сохраняем трассировку
            String tracePath = "target/traces/" + testName + ".zip";
            try {
                Files.createDirectories(Paths.get("target/traces"));
                context.tracing().stop(new Tracing.StopOptions()
                        .setPath(Paths.get(tracePath)));

                if (testError != null) {
                    System.err.println("Trace saved to: " + tracePath);
                    System.err.println("To view: npx playwright show-trace " + tracePath);
                } else {
                    System.out.println("Trace saved to: " + tracePath);
                }
            } catch (Exception e) {
                System.err.println("Failed to save trace: " + e.getMessage());
            }
        }

        // Если была ошибка - выбрасываем её снова
        if (testError != null) {
            if (testError instanceof RuntimeException) {
                throw (RuntimeException) testError;
            } else {
                throw new RuntimeException(testError);
            }
        }
    }
}
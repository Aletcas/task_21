package utils;

import com.microsoft.playwright.*;
import java.nio.file.*;

public interface Traceable {
    // Определяем окружение
    boolean IS_CI = System.getenv("GITHUB_ACTIONS") != null ||
            System.getenv("CI") != null ||
            "true".equals(System.getenv("TRACE_ONLY_FAILED"));

    // Директории для разных окружений
    String TRACES_DIR = IS_CI ? "target/traces-failed-only" : "target/traces-all";

    default void withTracing(BrowserContext context, String testName, Runnable test) {
        System.out.println("Starting test with tracing: " + testName);
        System.out.println("Environment: " + (IS_CI ? "CI/CD" : "Local"));

        // Начинаем трассировку с разными настройками для CI и локально
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(!IS_CI)      // В CI не сохраняем DOM snapshots (экономия)
                .setSources(!IS_CI)        // В CI не сохраняем исходники
                .setTitle("Test: " + testName));

        boolean testFailed = false;
        Throwable testError = null;

        try {
            test.run();
            System.out.println("✅ Test PASSED: " + testName);
        } catch (Throwable e) {
            testFailed = true;
            testError = e;
            System.err.println("❌ Test FAILED: " + testName);
            System.err.println("   Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        } finally {
            saveTraceWithStrategy(context, testName, testFailed);
        }

        if (testError != null) {
            if (testError instanceof RuntimeException) {
                throw (RuntimeException) testError;
            } else {
                throw new RuntimeException(testError);
            }
        }
    }

    private void saveTraceWithStrategy(BrowserContext context, String testName, boolean failed) {
        try {
            // Создаем директорию если нет
            Path tracesDir = Paths.get(TRACES_DIR);
            Files.createDirectories(tracesDir);

            if (IS_CI) {
                // В CI/CD: сохраняем ТОЛЬКО если тест упал
                if (failed) {
                    String fileName = testName + "_FAILED_" +
                            System.currentTimeMillis() + ".zip";
                    Path tracePath = tracesDir.resolve(fileName);

                    context.tracing().stop(new Tracing.StopOptions()
                            .setPath(tracePath));

                    // Дополнительный скриншот для быстрого просмотра
                    try {
                        Page page = context.pages().isEmpty() ? null : context.pages().get(0);
                        if (page != null) {
                            page.screenshot(new Page.ScreenshotOptions()
                                    .setPath(tracesDir.resolve(testName + "_screenshot.png"))
                                    .setFullPage(false));
                        }
                    } catch (Exception e) {
                        // Игнорируем ошибки скриншота
                    }

                    System.err.println("📁 Trace saved for failed test: " + tracePath.toAbsolutePath());

                } else {
                    // Тест прошел - просто останавливаем трассировку без сохранения
                    context.tracing().stop();
                    System.out.println("Trace discarded (test passed in CI)");
                }

            } else {
                // Локально: сохраняем ВСЕ тесты для отладки
                String status = failed ? "FAILED" : "PASSED";
                String fileName = testName + "_" + status + "_" +
                        System.currentTimeMillis() + ".zip";
                Path tracePath = tracesDir.resolve(fileName);

                context.tracing().stop(new Tracing.StopOptions()
                        .setPath(tracePath));

                System.out.println("💾 Trace saved locally: " + tracePath.toAbsolutePath());
            }

        } catch (Exception e) {
            System.err.println("⚠️  Could not save trace: " + e.getMessage());
            try {
                context.tracing().stop(); // Всегда останавливаем
            } catch (Exception ignored) {}
        }
    }
}
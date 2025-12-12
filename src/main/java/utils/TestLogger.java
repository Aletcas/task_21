package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestLogger {
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static void info(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.printf("[INFO] [%s] %s%n", timestamp, message);
    }

    public static void error(String message, Throwable t) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.err.printf("[ERROR] [%s] %s%n", timestamp, message);
        if (t != null) {
            t.printStackTrace();
        }
    }

    public static void startTest(String testName) {
        info("════════════════════════════════════════");
        info("Starting test: " + testName);
        info("════════════════════════════════════════");
    }

    public static void endTest(String testName, boolean passed) {
        String status = passed ? "PASSED ✓" : "FAILED ✗";
        info("Test " + testName + " " + status);
        info("════════════════════════════════════════\n");
    }
}

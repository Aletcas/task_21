package pages;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class StatusCodePage {
    private String baseUrl;

    public StatusCodePage(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getStatusCode(String path) throws IOException {
        String url = baseUrl + path;
        HttpURLConnection connection = null;

        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setInstanceFollowRedirects(false); // ← ВАЖНО! Отключаем редиректы

            return connection.getResponseCode();
        } catch (IOException e) {
            System.err.println("Error accessing " + url + ": " + e.getMessage());
            throw e; // Пробрасываем исключение дальше
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    public Map<String, Integer> getAllStatusCodes() throws IOException {
        // Определяем форматы путей в зависимости от базового URL
        String[] paths;

        if (baseUrl.contains("the-internet.herokuapp.com")) {
            paths = new String[]{
                    "/status_codes/200",
                    "/status_codes/301",
                    "/status_codes/404",
                    "/status_codes/500"
            };
        } else if (baseUrl.contains("httpstat.us")) {
            paths = new String[]{
                    "/200",
                    "/301",
                    "/404",
                    "/500"
            };
        } else {
            // По умолчанию для любого другого сайта
            paths = new String[]{
                    "/200",
                    "/301",
                    "/404",
                    "/500"
            };
        }

        Map<String, Integer> statusCodes = new HashMap<>();

        for (String path : paths) {
            try {
                int code = getStatusCode(path);
                statusCodes.put(path, code);
            } catch (IOException e) {
                statusCodes.put(path, -1);
            }
        }

        return statusCodes;
    }
}


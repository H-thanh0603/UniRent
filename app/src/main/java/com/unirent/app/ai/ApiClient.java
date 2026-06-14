package com.unirent.app.ai;

import android.os.Handler;
import android.os.Looper;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Client gọi DeepSeek API (OpenAI-compatible).
 * MVP: API key hardcode tạm, sẽ chuyển sang server-side proxy sau.
 */
public class ApiClient {
    private static final String API_URL = "https://api.ai-box.vn/v1/chat/completions";
    private static final String API_KEY = "sk-QVe6LUWgDJrP22guak7Ay69MCWpk23sTL6ApGEh1zrZqKj8v";
    private static final String MODEL = "deepseek-v4-pro";
    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface Callback {
        void onSuccess(String result);
        void onError(String error);
    }

    public static void chat(String systemPrompt, String userMessage, Callback callback) {
        executor.execute(() -> {
            try {
                String body = "{"
                    + "\"model\":\"" + MODEL + "\","
                    + "\"messages\":["
                    + "{\"role\":\"system\",\"content\":\"" + escapeJson(systemPrompt) + "\"},"
                    + "{\"role\":\"user\",\"content\":\"" + escapeJson(userMessage) + "\"}"
                    + "],"
                    + "\"temperature\":0.7,\"max_tokens\":1024"
                    + "}";

                HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
                conn.setDoOutput(true);
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(30000);

                OutputStream os = conn.getOutputStream();
                os.write(body.getBytes("UTF-8"));
                os.close();

                int code = conn.getResponseCode();
                InputStream is = code >= 200 && code < 300
                    ? conn.getInputStream()
                    : conn.getErrorStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();
                conn.disconnect();

                String response = sb.toString();
                String content = extractContent(response);

                mainHandler.post(() -> {
                    if (content != null) callback.onSuccess(content);
                    else callback.onError("API error: " + code + " - " + response);
                });
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    private static String extractContent(String json) {
        try {
            int idx = json.indexOf("\"content\":\"");
            if (idx < 0) return null;
            idx += 11;
            StringBuilder sb = new StringBuilder();
            for (int i = idx; i < json.length(); i++) {
                char c = json.charAt(i);
                if (c == '\\' && i + 1 < json.length()) {
                    char next = json.charAt(i + 1);
                    if (next == '"') { sb.append('"'); i++; continue; }
                    if (next == 'n') { sb.append('\n'); i++; continue; }
                    if (next == '\\') { sb.append('\\'); i++; continue; }
                }
                if (c == '"') break;
                sb.append(c);
            }
            return sb.toString().trim();
        } catch (Exception e) {
            return null;
        }
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

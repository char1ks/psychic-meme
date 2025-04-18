package com.example.LLMCodingChallenge2025.Config;

import com.github.jknack.handlebars.internal.text.StringEscapeUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Repository
public class MessageRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public MessageRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Получить все сообщения из Redis
    public List<String> getAllMessages() {
        Set<String> keys = redisTemplate.keys("message:*");
        List<String> messages = new ArrayList<>();
        if (keys != null) {
            for (String key : keys) {
                String message = redisTemplate.opsForValue().get(key);
                if (message != null) {
                    System.out.println("Key: " + key + ", Message: " + message); // Логирование
                    messages.add(message);
                }
            }
        }
        return messages;
    }
    public String getMoreInfoToSchema(String messages) throws Exception {
        // URL Flask API
        String apiUrl = "http://localhost:1234/processData";

        // Создаем JSON-тело запроса
        String jsonInputString = "{\"table_data\": \"" + messages.replace("\n", "\\n").replace("\"", "\\\"") + "\"}";

        // Создаем соединение
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        // Отправляем данные
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Получаем ответ
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                // Извлекаем ответ из JSON
                String jsonResponse = response.toString();
                String[] parts = jsonResponse.split("\"response\":\"");
                if (parts.length > 1) {
                    // Преобразуем Unicode-последовательности в читаемый текст
                    String escapedResponse = parts[1].split("\"")[0];
                    return StringEscapeUtils.unescapeJava(escapedResponse);
                } else {
                    throw new RuntimeException("Ответ от API не содержит поля 'response'");
                }
            }
        } else {
            throw new RuntimeException("Ошибка при вызове API: " + responseCode);
        }
    }

}

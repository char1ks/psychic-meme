package com.example.LLMCodingChallenge2025.Controller.REST;

import com.example.LLMCodingChallenge2025.Config.MessageRepository;
import com.example.LLMCodingChallenge2025.Model.Info.Info;
import com.example.LLMCodingChallenge2025.Service.InfoService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageRESTController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private InfoService infoService; // Внедрение InfoService
    @Autowired
    private RestTemplate restTemplate; // Внедрение RestTemplate

    @GetMapping
    public List<String> getAllMessages() {
        return messageRepository.getAllMessages();
    }

    @PostMapping("/llm_merge")
    public ResponseEntity<ByteArrayResource> mergeMessages(@RequestBody List<Map<String, String>> messages) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Messages");
            // Создаем заголовки столбцов
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Легенда (оригинальный текст)", "Дата", "Подразделение", "Операция", "Культура", "За день, га", "С начала операции, га", "Вал за день, ц", "Вал с начала, ц"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i); // Автоматически подстраивает ширину столбца под содержимое
            }
            // Заполняем данные
            int rowNum = 1;
            for (Map<String, String> message : messages) {
                String text = message.get("text");
                String totalText = messageRepository.getMoreInfoToSchema(text);
                if (totalText != null) {
                    // Убираем лишние кавычки и пробелы
                    totalText = totalText.replace("\"", "").trim();
                    // Разбиваем строку на части по разделителю "\n"
                    String[] rows = totalText.split("\n");
                    // Флаг для отслеживания, была ли уже записана легенда
                    boolean isLegendWritten = false;
                    for (String rowText : rows) {
                        // Убираем лишние пробелы и проверяем, не пустая ли строка
                        rowText = rowText.trim();
                        if (!rowText.isEmpty()) {
                            System.out.println("СТРОКААААА" + rowText);
                            // Разбиваем строку на столбцы
                            String[] parts = rowText.split(";");
                            // Очищаем каждую часть от лишних пробелов
                            for (int i = 0; i < parts.length; i++) {
                                parts[i] = parts[i].trim();
                            }
                            // Проверяем, начинается ли операция со слова "Выкашивание"
                            String operation = "";
                            int dateIndex = -1;
                            // Ищем дату в строке (формат: день.месяц.год или день.месяц)
                            for (int i = 0; i < parts.length; i++) {
                                if (isValidDate(parts[i])) {
                                    dateIndex = i;
                                    break;
                                }
                            }
                            // Определяем операция
                            if (dateIndex != -1 && dateIndex + 2 < parts.length) {
                                operation = parts[dateIndex + 2];
                            } else if (parts.length > 1) {
                                operation = parts[1];
                            }
                            // Пропускаем запись, если операция начинается со слова "Выкашивание"
                            if (operation.startsWith("Выкашивание") || operation.startsWith("Выкаш")) {
                                System.out.printf("ВЫКАШИВАНИЕЕЕЕ БЫЛО");
                                continue; // Пропустить эту строку
                            }
                            // Создаем строку в Excel
                            Row row = sheet.createRow(rowNum++);
                            // Легенда (оригинальный текст) записывается только один раз
                            if (!isLegendWritten) {
                                row.createCell(0).setCellValue(text);
                                isLegendWritten = true;
                            }
                            // Дата
                            String date = "";
                            if (dateIndex != -1) {
                                date = parts[dateIndex];
                            }
                            if (date == null || date.isEmpty() || !isValidDate(date)) {
                                LocalDate today = LocalDate.now(); // Получаем текущую дату
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-dd-MM"); // Формат даты
                                date = today.format(formatter); // Преобразуем в строку
                            }
                            row.createCell(1).setCellValue(date);
                            // Подразделение
                            String division = "";
                            if (dateIndex != -1 && dateIndex + 1 < parts.length) {
                                division = parts[dateIndex + 1];
                            } else if (parts.length > 0) {
                                division = parts[0];
                            }
                            row.createCell(2).setCellValue(division);
                            // Операция
                            row.createCell(3).setCellValue(operation);
                            // Культура
                            String culture = "";
                            if (dateIndex != -1 && dateIndex + 3 < parts.length) {
                                culture = parts[dateIndex + 3];
                            } else if (parts.length > 2) {
                                culture = parts[2];
                            }
                            row.createCell(4).setCellValue(culture);
                            // За день, га
                            String perDayHa = "";
                            if (dateIndex != -1 && dateIndex + 4 < parts.length) {
                                perDayHa = parts[dateIndex + 4];
                            } else if (parts.length > 3) {
                                perDayHa = parts[3];
                            }
                            if (!isNumeric(perDayHa)) {
                                perDayHa = findNextNumericValue(parts, dateIndex + 4);
                            }
                            row.createCell(5).setCellValue(perDayHa);
                            // С начала операции, га
                            String totalHa = "";
                            if (dateIndex != -1 && dateIndex + 5 < parts.length) {
                                totalHa = parts[dateIndex + 5];
                            } else if (parts.length > 4) {
                                totalHa = parts[4];
                            }
                            if (!isNumeric(totalHa)) {
                                totalHa = findNextNumericValue(parts, dateIndex + 5);
                            }
                            row.createCell(6).setCellValue(totalHa);
                            // Вал за день, ц (если есть)
                            if (dateIndex != -1 && dateIndex + 6 < parts.length && !parts[dateIndex + 6].isEmpty()) {
                                row.createCell(7).setCellValue(parts[dateIndex + 6]);
                            }
                            // Вал с начала, ц (если есть)
                            if (dateIndex != -1 && dateIndex + 7 < parts.length && !parts[dateIndex + 7].isEmpty()) {
                                row.createCell(8).setCellValue(parts[dateIndex + 7]);
                            }

                            // Создаем объект Info и сохраняем его в базу данных
                            Info info = new Info();
                            info.setOriginal_text(text);
                            info.setDate(date);
                            info.setSubdivision(division);
                            info.setOperation(operation);
                            info.setPlant_culture(culture);
                            info.setPer_day_ga(perDayHa);
                            info.setFrom_start_ga(totalHa);
                            info.setVal_day_ga(parts.length > 6 ? parts[6] : "");
                            info.setVal_start_ga(parts.length > 7 ? parts[7] : "");
                            infoService.saveInfo(info); // Сохраняем объект в базу данных
                        }
                    }

                    String responseFromProcessAnswer = callProcessAnswerAPI(totalText, "Пример первого сообщения");
                    System.out.println("Ответ от processAnswer: " + responseFromProcessAnswer);
                }
            }
            // Записываем файл в ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            // Создаем ресурс из байтового массива
            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
            // Устанавливаем заголовки для ответа
            HttpHeaders headers = new HttpHeaders();
            LocalDateTime now = LocalDateTime.now();

            // Форматируем дату и время в нужный формат
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy(HHmm)");
            String formattedDateTime = now.format(formatter);

            // Формируем имя файла
            String fileName = "Опять_уронили_" + formattedDateTime + ".xlsx";
            // Кодируем имя файла в UTF-8
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20"); // Заменяем пробелы на %20

            // Добавляем заголовок Content-Disposition с кодировкой
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName);
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            // Возвращаем файл
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании Excel-файла", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Метод для вызова API processAnswer
    private String callProcessAnswerAPI(String answer, String firstMessage) {
        // URL вашего Flask-эндпоинта
        String url = "http://localhost:1234/processAnswer";

        // Создаем тело запроса
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("answer", answer);
        requestBody.put("first_message", firstMessage);

        // Устанавливаем заголовки
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Создаем HTTP-запрос
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Отправляем POST-запрос
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        // Возвращаем ответ
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            throw new RuntimeException("Ошибка при вызове API processAnswer: " + responseEntity.getStatusCode());
        }
    }

    // Проверка, является ли строка числом
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Поиск следующего числового значения в массиве
    private String findNextNumericValue(String[] parts, int startIndex) {
        for (int i = startIndex; i < parts.length; i++) {
            if (isNumeric(parts[i])) {
                return parts[i];
            }
        }
        return ""; // Возвращаем пустую строку, если число не найдено
    }

    // Проверка, является ли строка датой
    public static boolean isValidDate(String date) {
        // Регулярное выражение для проверки дат
        String regex = "(\\d{1,2}[-/.])?\\d{1,2}[-/.]\\d{2,4}|\\d{4}[-/.]\\d{1,2}[-/.]\\d{1,2}";
        return date.matches(regex);
    }

    @GetMapping("/more_info")
    public String getMoreInfoToSchema(@RequestBody String message) throws Exception {
        String messages = messageRepository.getMoreInfoToSchema(message);
        return messages;
    }
}

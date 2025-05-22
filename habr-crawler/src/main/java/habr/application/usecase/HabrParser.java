package habr.application.usecase;

// HabrParser.java - Парсер HTML

import habr.application.dto.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jsoup.nodes.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class HabrParser {
    private static final Logger log = LoggerFactory.getLogger(HabrParser.class);
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private static final DateTimeFormatter FALLBACK_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;


    public Article parse(String html, String url, String docId) {
        Document doc = Jsoup.parse(html);
//        log.info("Получили html" + html);

        String title = doc.selectFirst("h1.tm-title").text();
        String author = doc.selectFirst("a.tm-user-info__username").text();
        String dateStr = doc.selectFirst("time[datetime]").attr("datetime");
        String text = doc.selectFirst("div.tm-article-body").text();

        log.info(dateStr);
        LocalDateTime publishDate = parseDateTime(dateStr);

        return new Article(docId, title, author, url, text, publishDate);
    }
    private String extractTitle(Document doc) {
        Element titleElement = doc.selectFirst("h1.tm-title");
        if (titleElement == null) {
            log.warn("Title element not found in document");
            return "No title";
        }
        return titleElement.text();
    }
    private String extractAuthor(Document doc) {
        Element authorElement = doc.selectFirst("a.tm-user-info__username");
        return (authorElement != null) ? authorElement.text() : "Unknown";
    }

    private LocalDateTime extractDate(Document doc) {
        Element dateElement = doc.selectFirst("time");
        if (dateElement == null) return LocalDateTime.now();

        String dateStr = dateElement.attr("datetime");
        try {
            return LocalDateTime.parse(dateStr, DATE_FORMATTER);
        } catch (RuntimeException e) {
            log.warn("Failed to parse date: {}", dateStr);
            return LocalDateTime.now();
        }
    }

    private String extractText(Document doc) {
        Element textElement = doc.selectFirst("div.tm-article-body");
        return (textElement != null) ? textElement.text() : "";
    }

    /**
     * Универсальный метод парсинга даты.
     * Пробует несколько форматтеров для извлечения времени.
     *
     * @param dateStr строка с датой (в формате ISO или другом)
     * @return объект LocalDateTime или null, если формат не подходит
     */
    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            log.warn("Received empty or null date string. Returning null.");
            return null;
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateStr, formatter);
            } catch (Exception e) {
                // Не логируем каждый формат, чтобы не засорять логи
            }
        }

        // Если ни один формат не подошел
        log.error("Failed to parse date after trying all formatters: '{}'. Returning null.", dateStr);
        return null;
    }

    // Список возможных форматов времени
    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),    // Основной формат
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),  // ISO 8601 с таймзоной
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),     // Без таймзоны
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,                       // Fallback ISO формат
            DateTimeFormatter.ISO_LOCAL_DATE_TIME                         // ISO локальное время
    );


}
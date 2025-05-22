package habr.application.usecase;

// HabrCrawler.java - Основной модуль краулера - скачивает html
import habr.application.dto.Article;
import habr.domain.repository.ArticleRepository;
import habr.application.usecase.HabrParser;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.concurrent.TimeUnit;

//@Component // Spring будет управлять этим классом
public class HabrCrawler {
    private static final Logger log = LoggerFactory.getLogger(HabrCrawler.class);

    private final CloseableHttpClient httpClient;
    private final HabrParser parser;
    private final ArticleRepository repository;


    public HabrCrawler(HabrParser parser, ArticleRepository repository) {
        this.parser = parser;
        this.repository = repository;

        this.httpClient = HttpClients.custom()
                .setRedirectStrategy(new LaxRedirectStrategy()) // Следование за редиректами
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setRedirectsEnabled(true)
                        .setMaxRedirects(10) // Максимум 10 перенаправлений
                        .build())
                .disableCookieManagement() // Отключаем куки
                .setUserAgent("HabrCrawler/1.0 (+https://example.com/bot)")
                .setConnectionTimeToLive(30, TimeUnit.SECONDS)
                .build();

        log.info("Initialized Apache HTTP Client with redirects");
    }

    public void processArticle(String url, String docId) throws Exception {
        log.info("Start processing article: {}", url);
        try {
            // 1. Скачивание HTML
            String html = fetchHtml(url);
            log.debug("HTML content downloaded ({} bytes)", html.length());

            if (html.isEmpty()) {
                log.error("Empty HTML content for {}", url);
                return;
            }
            // 2. Парсинг
            Article article = parser.parse(html, url, docId);
            // 3. Сохранение
            repository.save(article);

            log.info("Successfully processed article: {}", article.getTitle());
        } catch (RuntimeException e) {
            log.error("HTTP error processing {}: {}", url, e.getMessage());
            throw e;
        }
    }

    private String fetchHtml(String url) throws Exception {
        HttpGet request = new HttpGet(url);
        log.debug("Executing request: {}", request);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            log.debug("Response status: {}", statusCode);

            if (statusCode != 200) {
//                throw new Exception("HTTP error: " + statusCode);
                log.error("HTTP error: {}", statusCode);
                return "";
            }

            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            log.error("Error fetching URL: {} - {}", url, e.getMessage());
            return "";
        }
    }
}
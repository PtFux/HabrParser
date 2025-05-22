package habr.application.usecase;

import habr.domain.repository.ArticleRepository;
import habr.domain.service.persistence.elastic.ElasticSearchRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// ConsumerApi.java - Точка входа
@Service
public class ConsumerApi {
    private final HabrCrawler crawler;
    private final ArticleRepository repository;
    private static final Logger log = LoggerFactory.getLogger(ConsumerApi.class);

    @Autowired
    public ConsumerApi(ArticleRepository repository, HabrParser parser) {
        this.repository = repository;
        this.crawler = new HabrCrawler(new HabrParser(), repository);
    }

    public void processInput(String url, String docId) {
        log.info("Received request to process: {} (ID: {})", url, docId);
        try {
            crawler.processArticle(url, docId);
        } catch (Exception e) {
            log.error("Critical error processing article {}: {}", url, e.getMessage(), e);
        }
    }

    public void run(String url) {
        log.info("Starting application...");
        // Пример вызова
        processInput(
                url,
                generateHash(url)
        );
    }


    private static String generateDocId(String title, String datetime) {
        return title.hashCode() + "_" + datetime.hashCode();
    }

    public static String generateHash(String url)  {
        MessageDigest md = null; // Можно заменить на "SHA-256"
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hashBytes = null;
        try {
            hashBytes = md.digest(url.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        // Конвертация в HEX-строку (32 символа для MD5)
        return new BigInteger(1, hashBytes).toString(16);
    }


}
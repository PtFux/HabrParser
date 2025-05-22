package habr.service;

import habr.domain.Article;
import habr.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;

@Service
@RequiredArgsConstructor
public class ArticleGenerator {
    private final ArticleRepository repository;
    private final ApiPublisher apiPublisher;

    public void generateArticles(String baseUrl, int start, int end) throws Exception {
        for (int i = start; i <= end; i++) {
            String url = String.format("%s/%d/", baseUrl, i);
            String id = generateHash(url);

            if (!repository.existsById(id)) {
                repository.save(new Article(id, url));
                apiPublisher.publish(url, id);
            }
        }
    }

    public static String generateHash(String url) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256"); // Можно заменить на "SHA-256"
        byte[] hashBytes = md.digest(url.getBytes("UTF-8"));

        // Конвертация в HEX-строку (32 символа для MD5)
        return new BigInteger(1, hashBytes).toString(16);
    }

}



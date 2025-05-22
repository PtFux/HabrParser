package habr;

import habr.service.ArticleGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private ArticleGenerator generator;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Обязательно без /
        String baseUrl = "https://habr.com/ru/articles";
        int startId = 100301;
        int endId = 100309;

        generator.generateArticles(baseUrl, startId, endId);

        System.out.println("Обработка завершена для ID с "
                + startId + " по " + endId);
        System.exit(0); // Завершаем работу после выполнения
    }
}
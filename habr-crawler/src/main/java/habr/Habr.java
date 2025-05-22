// Файл: src/main/java/habr/Habr.java
package habr;

import habr.application.usecase.ConsumerApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "habr") // Сканирует все подпакеты habr.*
public class Habr{
//    private final ArticleEventListener articleEventListener;
//
//    @Autowired
//    public Habr(ConsumerApi consumerApi) {
//        this.articleEventListener = new ArticleEventListener(consumerApi);
//    }
////    public static void main(String[] args) {
////        // Запуск Spring контекста
////        SpringApplication.run(Habr.class, args);
////
////        // Запуск основной логики приложения
////        ConsumerApi.run("https://habr.com/ru/articles/343454");
////    }
//
//    @Override
//    public void run(String... args) {
//        articleEventListener.handleMessage("https://habr.com/ru/articles/10");
//    }

    public static void main(String[] args) {
        SpringApplication.run(Habr.class, args); // Инициализация Spring
    }

}
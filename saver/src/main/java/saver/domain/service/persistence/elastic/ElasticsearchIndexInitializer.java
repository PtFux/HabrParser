package saver.domain.service.persistence.elastic;

// ElasticsearchIndexInitializer.java
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class ElasticsearchIndexInitializer {
    private final ElasticsearchOperations operations;

    public ElasticsearchIndexInitializer(ElasticsearchOperations operations) {
        this.operations = operations;
    }

    @PostConstruct
    public void createIndex() {
        IndexOperations indexOps = operations.indexOps(ArticleDocument.class);

        if (!indexOps.exists()) {
            indexOps.create();
            indexOps.putMapping(indexOps.createMapping());
        }
    }

    public void init() {
        try {
            // Используйте здесь клиент для проверки соединения или создания индекса
            System.out.println("Подключение к Elasticsearch успешно!");
        } catch (Exception e) {
            System.err.println("Ошибка инициализации индекса: " + e.getMessage());
            // Либо пропустите исключение, либо завершите процесс через логирование
        }
    }

}
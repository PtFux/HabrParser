package saver.listener;

import saver.application.dto.Article;
import saver.broker.RabbitConfig;
import saver.dto.ArticlePayload;
import saver.domain.service.persistence.elastic.ElasticSearchRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchConsumer {
    private final ElasticSearchRepository repository;

    public ElasticsearchConsumer(ElasticSearchRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitConfig.ARTICLES_QUEUE)
    public void saveToElastic(ArticlePayload payload) {
        repository.save(convertToArticle(payload));
    }

    private Article convertToArticle(ArticlePayload payload) {
        Article a = new Article(
                payload.getDocId(),
                payload.getTitle(),
                payload.getAuthor(),
                payload.getUrl(),
                payload.getContent(),
                payload.getPublishDate()
        );

        return a;
    }
}

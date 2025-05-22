package habr.broker;

import habr.application.dto.ArticlePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static habr.broker.RabbitConfig.*;

@Component
public class ArticlePublisher {
    private static final Logger log = LoggerFactory.getLogger(ArticlePublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public ArticlePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishArticle(ArticlePayload payload) {
        String messageId = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(messageId);

        rabbitTemplate.convertAndSend(
                PROCESSED_ARTICLES_EXCHANGE,
                "processed.article.new",
                payload,
                correlationData
        );

        log.info("Сообщение отправленео в exchange: {} routingKey: {} URL {}", PROCESSED_ARTICLES_EXCHANGE, "article.processed", payload.getUrl());

        try {
            var confirm = correlationData.getFuture().get(5, TimeUnit.SECONDS);
            if (confirm.isAck()) {
                log.info("Сообщение подтверждено RabbitMQ, ID: {}", messageId);
            } else {
                throw new RuntimeException("NACK от RabbitMQ: " + confirm.getReason());
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка ожидания подтверждения RabbitMQ: " + e.getMessage(), e);
        }
    }
}

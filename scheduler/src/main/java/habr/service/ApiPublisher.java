package habr.service;

import habr.broker.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ApiPublisher {

    private static final Logger logger = LoggerFactory.getLogger(ApiPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public ApiPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void publish(String url, String id) {
        Map<String, String> message = Map.of("id", id, "url", url);
        rabbitTemplate.convertAndSend(
                RabbitConfig.ARTICLES_EXCHANGE,
                "article.new",
                message,
                m -> {
                    m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return m;
                }
        );
        logger.info("Publishing article: {} with id: {} to {}", url, id, "article.new");

    }
}
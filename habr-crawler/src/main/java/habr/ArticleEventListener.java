package habr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import habr.Habr;
import habr.application.usecase.ConsumerApi;
import habr.domain.service.persistence.elastic.ElasticSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class ArticleEventListener {
    private static final Logger logger = LoggerFactory.getLogger(ArticleEventListener.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConsumerApi consumerApi;

    public ArticleEventListener(ConsumerApi consumerApi) {
        this.consumerApi = consumerApi;
    }

    @RabbitListener(queues = "articles.queue")
    public void handleMessage(
            @Payload Map<String, String> payload,  // Явное указание
            @Header(AmqpHeaders.CONTENT_TYPE) String contentType,
            Message message,
            Channel channel) throws Exception {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            validatePayload(payload);

            logger.info("Processing article: {}", payload);
            consumerApi.processInput(payload.get("url"), payload.get("id"));

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            logger.error("Invalid message: {}", e.getMessage());
            channel.basicReject(deliveryTag, false);  // Отправка в DLQ
        }
    }

    private void validatePayload(Map<String, String> payload) throws Exception {
        if (payload == null || !payload.containsKey("id") || !payload.containsKey("url")) {
            throw new Exception("Invalid message structure");
        }
    }
}

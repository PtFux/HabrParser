package habr.broker;


import habr.service.ApiPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    private static final Logger logger = LoggerFactory.getLogger(ApiPublisher.class);

    public static final String ARTICLES_QUEUE = "articles.queue";
    public static final String ARTICLES_EXCHANGE = "articles.exchange";
    public static final String ROUTING_KEY_PATTERN = "article.#";


    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange articlesExchange() {
        return new TopicExchange(ARTICLES_EXCHANGE, true, false);
    }

    @Bean
    public Binding binding(Queue articlesQueue, TopicExchange articlesExchange) {
        return BindingBuilder.bind(articlesQueue).to(articlesExchange).with(ROUTING_KEY_PATTERN);
    }



    @Bean
    public org.springframework.amqp.rabbit.core.RabbitTemplate RabbitTemplate(ConnectionFactory connectionFactory) {

        org.springframework.amqp.rabbit.core.RabbitTemplate template = new org.springframework.amqp.rabbit.core.RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());


        template.setMandatory(true); // включаем возможность возврата
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (correlationData instanceof ConfirmLatchWrapper wrapper) {
                wrapper.setAck(ack);
                wrapper.setCause(cause);
                wrapper.getLatch().countDown();
            }
        });

        template.setReturnsCallback(returned -> {
            logger.warn("Returned message: {} with reply code: {}",
                    new String(returned.getMessage().getBody()),
                    returned.getReplyCode());
        });

        return template;
    }

    @Bean
    public Queue articlesQueue() {
        return QueueBuilder.durable("articles.queue")
                .maxLength(10000)                         // максимум 10 000 сообщений
                .maxLengthBytes(10 * 1024 * 1024)          // максимум 10 МБ данных (опционально)
                .overflow(QueueBuilder.Overflow.rejectPublish) // отклонить публикации, если переполнено
                .ttl(600_000)                              // TTL: 10 минут (опционально)
                .build();
    }



}

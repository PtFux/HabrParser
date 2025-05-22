package saver.broker;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    private static final Logger logger = LoggerFactory.getLogger(RabbitConfig.class);

    public static final String ARTICLES_QUEUE = "processed.articles.queue";
    public static final String ARTICLES_EXCHANGE = "processed.articles.exchange";
    public static final String ROUTING_KEY_PATTERN = "article.#";




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
        template.setMessageConverter(messageConverter());


        template.setMandatory(true); // включаем возможность возврата
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                logger.info("Message successfully delivered to broker");
            } else {
                logger.warn("Message failed to deliver to broker: {}", cause);
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
        return QueueBuilder.durable(ARTICLES_QUEUE)
                .maxLength(10000)                         // максимум 10 000 сообщений
                .maxLengthBytes(10 * 1024 * 1024)          // максимум 10 МБ данных (опционально)
                .overflow(QueueBuilder.Overflow.rejectPublish) // отклонить публикации, если переполнено
                .ttl(600_000)                              // TTL: 10 минут (опционально)
                .build();
    }

    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new Jackson2JsonMessageConverter(objectMapper);
    }
}

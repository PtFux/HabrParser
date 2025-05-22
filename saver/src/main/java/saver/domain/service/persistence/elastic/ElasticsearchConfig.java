package saver.domain.service.persistence.elastic;

// ElasticsearchConfig.java

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import java.time.Duration;

//@EnableElasticsearchRepositories(basePackages = "habr.domain.service.persistence.elastic")
@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchConfig.class);

    @Override
    public ClientConfiguration clientConfiguration() {
        String host = System.getenv("ELASTICSEARCH_HOST");
        String username = System.getenv("ELASTICSEARCH_USERNAME");
        String password = System.getenv("ELASTICSEARCH_PASSWORD");

        LOGGER.info("Elasticsearch host: {}", host);
        LOGGER.info("Elasticsearch username: {}", username);
        LOGGER.info("Elasticsearch password: {}", password);

        // Проверка на null для хоста
        if (host == null || host.isEmpty()) {
            LOGGER.error("Environment variable ELASTICSEARCH_HOST is not set or empty. Please set it in your environment.");
            throw new IllegalArgumentException("Environment variable ELASTICSEARCH_HOST must not be null or empty.");
        }

        LOGGER.info("Using Elasticsearch host: {}", host);

        // Проверка на наличие аутентификационных данных, если требуется
        if ((username == null || username.isEmpty()) || (password == null || password.isEmpty())) {
            LOGGER.warn("Username or password for Elasticsearch authentication is not set. Defaulting to unauthenticated access.");
        }

        // Построение конфигурации
        return ClientConfiguration.builder()
                .connectedTo(host)
                .withBasicAuth(
                        username == null ? "" : username,
                        password == null ? "" : password
                )
                .withSocketTimeout(Duration.ofSeconds(30))
                .withConnectTimeout(Duration.ofSeconds(30))
                .build();
    }
}

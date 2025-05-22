package habr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ApiPublisher {
    private static final Logger logger = LoggerFactory.getLogger(ApiPublisher.class);

    public void publish(String url, String id) {
        logger.info("Publishing article: {} with id: {}", url, id);
        // Реальная реализация будет здесь
    }
}
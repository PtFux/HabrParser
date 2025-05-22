package habr.domain.service.persistence.elastic;

import habr.application.dto.Article;
import habr.domain.repository.ArticleRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


// ElasticSearchRepository.java - Реализация репозитория
@Repository
public class ElasticSearchRepository implements ArticleRepository  {
    private static final Logger log = LoggerFactory.getLogger(ElasticSearchRepository.class);

    private final ArticleElasticRepository elasticRepo;
    private final ElasticsearchOperations operations;


    public ElasticSearchRepository(ArticleElasticRepository elasticRepo,
                                   ElasticsearchOperations operations) {
        this.elasticRepo = elasticRepo;
        this.operations = operations;
    }

    @Override
    public void save(Article article) {
        try {
            log.debug("Saving article to ES. id: {}", article.getDocId());
            log.debug("Saving article to ES. title: {}", article.getTitle());
            log.debug("Saving article to ES. link: {}", article.getLink());
            log.debug("Saving article to ES. publishDate: {}", article.getPublishDate());
            log.debug("Saving article to ES. id: {}", article.getAuthor());
            log.debug("Saving article to ES. text: {}", article.getText().length());

            ArticleDocument doc = convertToDocument(article);
            elasticRepo.save(doc);
            // Реальная логика сохранения
            log.info("Article saved successfully: {}", article.getDocId());
        } catch (Exception e) {
            log.error("ES save failed for {}: {}", article.getDocId(), e.getMessage());
            throw new RuntimeException("ES save failed: " + e.getMessage());        }
    }

    private ArticleDocument convertToDocument(Article article) {
        ArticleDocument doc = new ArticleDocument();
        doc.setDocId(article.getDocId());
        doc.setTitle(article.getTitle());
        doc.setAuthor(article.getAuthor());
        doc.setUrl(article.getLink());
        doc.setContent(article.getText());
        doc.setPublishDate(article.getPublishDate());
        return doc;
    }
}

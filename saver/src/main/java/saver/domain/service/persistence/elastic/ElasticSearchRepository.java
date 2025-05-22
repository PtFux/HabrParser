package saver.domain.service.persistence.elastic;

import saver.application.dto.Article;
import saver.domain.repository.ArticleRepository;
import saver.domain.service.persistence.elastic.ArticleElasticRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;


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

package saver.domain.service.persistence.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface ArticleElasticRepository extends ElasticsearchRepository<ArticleDocument, String> {
}
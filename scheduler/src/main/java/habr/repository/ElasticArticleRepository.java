package habr.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import habr.domain.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ElasticArticleRepository implements ArticleRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public boolean existsById(String id) {
        return elasticsearchOperations.exists(id, Article.class);
    }

    @Override
    public void save(Article article) {
        elasticsearchOperations.save(article);
    }
}
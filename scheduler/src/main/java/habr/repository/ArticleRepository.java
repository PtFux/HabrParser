package habr.repository;

import habr.domain.Article;

public interface ArticleRepository {
    boolean existsById(String id);
    void save(Article article);
}
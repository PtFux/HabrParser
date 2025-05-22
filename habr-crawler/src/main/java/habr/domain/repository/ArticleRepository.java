package habr.domain.repository;

import habr.application.dto.Article;

// ArticleRepository.java - Интерфейс репозитория

public interface ArticleRepository {

    void save(Article article);

}

package saver.domain.repository;

import saver.application.dto.Article;

// ArticleRepository.java - Интерфейс репозитория

public interface ArticleRepository {

    void save(Article article);

}

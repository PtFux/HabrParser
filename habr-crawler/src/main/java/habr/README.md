Здесь реализован функционала модуля с парсингом хабра

```
     ( url, docID = hash(docTitle, docDateTime) )
  [ RabbitMQ ]            ->               [ ConsumerApi.java ]
  
                                                    | ( url, docID ) 
                                                    ↓ 
                                                              (Doc{title, author, url, text, datetime})
                                           [ HabrCrawler.java ]   ->  [ArticleRepository.java ] -> [ ElasticSearch ]
                                                
                                                    |
                                                    ↓
                                       
                                            [ HabrParser.java ]    <->     habr.com 
```

Работа модуля:
- получение из очереди в rabbitMQ 1 ссылку на статью и идентификатор этой статьи,
- скачивание html этой статьи
- парсинг хтмл и сохранение данных в бизнес структуру Article 
- сохранение данных о документе в Elastic Search



  HabrCrawler - только загрузка HTML

  HabrParser - только парсинг

  ArticleRepository - абстракция для сохранения
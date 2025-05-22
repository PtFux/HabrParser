package habr.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

//@Document(indexName = "articles")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class Article {
//    @Id
//    private String id;
//    private String url;
//}

@Data // Автоматически генерирует геттеры/сеттеры
@Document(indexName = "habr_articles")
public class Article {
    @Id
    private String docId;

    @Field(type = FieldType.Text, analyzer = "russian")
    private String title;

    @Field(type = FieldType.Keyword)
    private String author;

    @Field(type = FieldType.Keyword)
    private String url;

    @Field(type = FieldType.Text, analyzer = "russian")
    private String content;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private LocalDateTime publishDate;

    public Article(String id, String url) {
        this.docId = id;
        this.url = url;
    }
}

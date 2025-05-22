package habr.application.dto;
import java.time.LocalDateTime;

/**
 * Класс, представляющий статью.
 */

public class Article {
    private final String docID;
    private final String title;
    private final String author;
    private final String link;
    private final String text;
    private final LocalDateTime publishDate;

    public Article(String docID, String title, String author, String url,
                   String text, LocalDateTime publishDate) {
        this.docID = docID;
        this.title = title;
        this.author = author;
        this.link = url;
        this.text = text;
        this.publishDate = publishDate;
    }

    public String toString() {
        return "Article: {\n" +  this.title + ",\n" + this.docID + ",\n" + this.link + ",\n" + this.author + ",\n" + this.publishDate + ",\n}\n";
    }

    // Геттеры
    public String getDocId() { return docID; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getLink() { return link; }
    public String getText() { return text; }
    public LocalDateTime getPublishDate() { return publishDate; }
}
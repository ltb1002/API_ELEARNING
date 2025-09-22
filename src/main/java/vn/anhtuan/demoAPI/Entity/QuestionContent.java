package vn.anhtuan.demoAPI.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "question_contents")
public class QuestionContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType;

    @Column(name = "content_value", nullable = false, columnDefinition = "TEXT")
    private String contentValue;

    // Constructors
    public QuestionContent() {}

    public QuestionContent(Question question, ContentType contentType, String contentValue) {
        this.question = question;
        this.contentType = contentType;
        this.contentValue = contentValue;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public String getContentValue() {
        return contentValue;
    }

    public void setContentValue(String contentValue) {
        this.contentValue = contentValue;
    }
}
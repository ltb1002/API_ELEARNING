package vn.anhtuan.demoAPI.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "quiz_types")
public class QuizType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "question_count", nullable = false)
    private Integer questionCount;

    // Constructors
    public QuizType() {}

    public QuizType(Integer id, String name, Integer durationMinutes, Integer questionCount) {
        this.id = id;
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.questionCount = questionCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }
}

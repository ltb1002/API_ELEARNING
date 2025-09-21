package vn.anhtuan.demoAPI.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_results")
public class QuizResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "score", nullable = false)
    private Float score;

    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private QuizStatus status = QuizStatus.INCOMPLETE;

    @Column(name = "completed_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;

    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // Constructors
    public QuizResult() {
        this.createdAt = LocalDateTime.now();
    }

    public QuizResult(User user, Quiz quiz, Float score, Integer correctAnswers,
                      Integer totalQuestions, QuizStatus status) {
        this.user = user;
        this.quiz = quiz;
        this.score = score;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.createdAt = LocalDateTime.now();
        setStatus(status); // Sử dụng setter để đảm bảo completedAt được thiết lập đúng
    }

    public void updateResult(float score, int correctAnswers, int totalQuestions) {
        validateResultData(score, correctAnswers, totalQuestions);

        this.score = score;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        setStatus(QuizStatus.COMPLETED); // Sử dụng setter để tự động set completedAt
    }

    private void validateResultData(float score, int correctAnswers, int totalQuestions) {
        if (correctAnswers > totalQuestions) {
            throw new IllegalArgumentException("correctAnswers cannot exceed totalQuestions");
        }
        if (score < 0 || score > 10) { // Điểm số nên nằm trong khoảng 0-10
            throw new IllegalArgumentException("Score must be between 0 and 10");
        }
        if (totalQuestions <= 0) {
            throw new IllegalArgumentException("Total questions must be greater than 0");
        }
    }

    // Enum for status
    public enum QuizStatus {
        COMPLETED, INCOMPLETE
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        if (score != null && (score < 0 || score > 10)) {
            throw new IllegalArgumentException("Score must be between 0 and 10");
        }
        this.score = score;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        if (correctAnswers != null && totalQuestions != null && correctAnswers > totalQuestions) {
            throw new IllegalArgumentException("correctAnswers cannot exceed totalQuestions");
        }
        this.correctAnswers = correctAnswers;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        if (totalQuestions != null && totalQuestions <= 0) {
            throw new IllegalArgumentException("Total questions must be greater than 0");
        }
        this.totalQuestions = totalQuestions;
    }

    public QuizStatus getStatus() {
        return status;
    }

    public void setStatus(QuizStatus status) {
        this.status = status;
        if (status == QuizStatus.COMPLETED && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
package vn.anhtuan.demoAPI.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity
@Table(name = "quiz_results")
public class QuizResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "attempt_no", nullable = false)
    private Integer attemptNo = 1;

    @Column(name = "score", nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private QuizStatus status = QuizStatus.INCOMPLETE;

    @Column(name = "completed_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;

    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "percentage", precision = 5, scale = 2, insertable = false, updatable = false)
    @Generated(GenerationTime.ALWAYS)
    private BigDecimal percentage;

    // Constructors
    public QuizResult() {
        this.createdAt = LocalDateTime.now();
    }

    public QuizResult(User user, Quiz quiz, Integer attemptNo, BigDecimal score,
                      Integer correctAnswers, Integer totalQuestions, Integer durationSeconds,
                      QuizStatus status) {
        this.user = user;
        this.quiz = quiz;
        this.attemptNo = attemptNo;
        this.score = score;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.durationSeconds = durationSeconds;
        this.createdAt = LocalDateTime.now();
        setStatus(status);
    }

    public void updateResult(BigDecimal score, int correctAnswers, int totalQuestions,
                             int durationSeconds) {
        validateResultData(score, correctAnswers, totalQuestions);
        this.score = score;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.durationSeconds = durationSeconds;
        setStatus(QuizStatus.COMPLETED);
    }

    private void validateResultData(BigDecimal score, int correctAnswers, int totalQuestions) {
        if (correctAnswers > totalQuestions) {
            throw new IllegalArgumentException("correctAnswers cannot exceed totalQuestions");
        }
        if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(new BigDecimal("10")) > 0) {
            throw new IllegalArgumentException("Score must be between 0 and 10");
        }
        if (totalQuestions <= 0) {
            throw new IllegalArgumentException("Total questions must be greater than 0");
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public Integer getAttemptNo() { return attemptNo; }
    public void setAttemptNo(Integer attemptNo) { this.attemptNo = attemptNo; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) {
        if (score != null && (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(new BigDecimal("10")) > 0)) {
            throw new IllegalArgumentException("Score must be between 0 and 10");
        }
        this.score = score;
    }

    public Integer getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(Integer correctAnswers) {
        if (correctAnswers != null && totalQuestions != null && correctAnswers > totalQuestions) {
            throw new IllegalArgumentException("correctAnswers cannot exceed totalQuestions");
        }
        this.correctAnswers = correctAnswers;
    }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) {
        if (totalQuestions != null && totalQuestions <= 0) {
            throw new IllegalArgumentException("Total questions must be greater than 0");
        }
        this.totalQuestions = totalQuestions;
    }

    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }

    public QuizStatus getStatus() { return status; }
    public void setStatus(QuizStatus status) {
        this.status = status;
        if (status == QuizStatus.COMPLETED && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
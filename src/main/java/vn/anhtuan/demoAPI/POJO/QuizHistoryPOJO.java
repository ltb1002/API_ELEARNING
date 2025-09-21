package vn.anhtuan.demoAPI.POJO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class QuizHistoryPOJO {
    private Integer attemptNo;
    private BigDecimal score;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Integer durationSeconds;
    private LocalDateTime completedAt;
    private String status;

    // Constructors
    public QuizHistoryPOJO() {}

    public QuizHistoryPOJO(Integer attemptNo, BigDecimal score, Integer correctAnswers,
                           Integer totalQuestions, Integer durationSeconds,
                           LocalDateTime completedAt, String status) {
        this.attemptNo = attemptNo;
        this.score = score;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.durationSeconds = durationSeconds;
        this.completedAt = completedAt;
        this.status = status;
    }

    // Getters and setters
    public Integer getAttemptNo() { return attemptNo; }
    public void setAttemptNo(Integer attemptNo) { this.attemptNo = attemptNo; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }

    public Integer getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(Integer correctAnswers) { this.correctAnswers = correctAnswers; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
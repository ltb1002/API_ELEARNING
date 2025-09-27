package vn.anhtuan.demoAPI.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "subject_id", "grade"}))
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private Integer grade;

    @Column(name = "completed_lessons", nullable = false)
    private Integer completedLessons = 0;

    @Column(name = "total_lessons", nullable = false)
    private Integer totalLessons = 0;

    @Column(name = "progress_percent", nullable = false)
    private Double progressPercent = 0.0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Constructors
    public Progress() {
    }

    public Progress(User user, Subject subject) {
        this.user = user;
        this.subject = subject;
        this.grade = subject.getGrade(); // Lấy grade từ subject
        this.completedLessons = 0;
        this.totalLessons = 0;
        this.progressPercent = 0.0;
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public void calculateProgress() {
        if (totalLessons > 0) {
            this.progressPercent = (double) completedLessons / totalLessons * 100;
        } else {
            this.progressPercent = 0.0;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementCompletedLesson() {
        this.completedLessons++;
        calculateProgress();
    }

    public void decrementCompletedLesson() {
        if (this.completedLessons > 0) {
            this.completedLessons--;
            calculateProgress();
        }
    }

    public void updateTotalLessons(int total) {
        this.totalLessons = total;
        calculateProgress();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        this.grade = subject.getGrade(); // Đảm bảo grade luôn đồng bộ với subject
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Integer getCompletedLessons() {
        return completedLessons;
    }

    public void setCompletedLessons(Integer completedLessons) {
        this.completedLessons = completedLessons;
        calculateProgress();
    }

    public Integer getTotalLessons() {
        return totalLessons;
    }

    public void setTotalLessons(Integer totalLessons) {
        this.totalLessons = totalLessons;
        calculateProgress();
    }

    public Double getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(Double progressPercent) {
        this.progressPercent = progressPercent;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
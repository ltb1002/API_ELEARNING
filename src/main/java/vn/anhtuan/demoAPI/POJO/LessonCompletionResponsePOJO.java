package vn.anhtuan.demoAPI.POJO;

import java.time.LocalDateTime;

public class LessonCompletionResponsePOJO {
    private Long id;
    private Long userId;
    private Long lessonId;
    private LocalDateTime completedAt;

    // Constructors
    public LessonCompletionResponsePOJO() {
    }

    public LessonCompletionResponsePOJO(Long id, Long userId, Long lessonId, LocalDateTime completedAt) {
        this.id = id;
        this.userId = userId;
        this.lessonId = lessonId;
        this.completedAt = completedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}

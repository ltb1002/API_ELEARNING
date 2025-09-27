package vn.anhtuan.demoAPI.POJO;

public class LessonCompletionRequestPOJO {
    private Long userId;
    private Long lessonId;

    // Constructors
    public LessonCompletionRequestPOJO() {
    }

    public LessonCompletionRequestPOJO(Long userId, Long lessonId) {
        this.userId = userId;
        this.lessonId = lessonId;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }
}

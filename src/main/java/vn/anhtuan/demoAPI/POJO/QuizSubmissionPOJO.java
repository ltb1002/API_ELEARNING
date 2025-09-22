package vn.anhtuan.demoAPI.POJO;

import java.util.List;
import java.util.Map;

public class QuizSubmissionPOJO {
    private Long userId;
    private Map<Long, List<Long>> answers;
    private Integer durationSeconds; // Thêm trường duration

    public QuizSubmissionPOJO() {
    }

    public QuizSubmissionPOJO(Long userId, Map<Long, List<Long>> answers, Integer durationSeconds) {
        this.userId = userId;
        this.answers = answers;
        this.durationSeconds = durationSeconds;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Map<Long, List<Long>> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, List<Long>> answers) {
        this.answers = answers;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
}
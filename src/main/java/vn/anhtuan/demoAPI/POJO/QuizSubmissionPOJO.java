package vn.anhtuan.demoAPI.POJO;

import java.util.List;
import java.util.Map;

public class QuizSubmissionPOJO {
    private Long userId;
    private Map<Integer, List<Integer>> answers;
    private Integer durationSeconds; // Thêm trường duration

    public QuizSubmissionPOJO() {
    }

    public QuizSubmissionPOJO(Long userId, Map<Integer, List<Integer>> answers, Integer durationSeconds) {
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

    public Map<Integer, List<Integer>> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Integer, List<Integer>> answers) {
        this.answers = answers;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
}
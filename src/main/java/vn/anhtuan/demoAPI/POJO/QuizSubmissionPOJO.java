package vn.anhtuan.demoAPI.POJO;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;
import java.util.Map;

public class QuizSubmissionPOJO {
    private Long userId;
    private Map<Integer, List<Integer>> answers; // questionId -> list of choiceIds

    public QuizSubmissionPOJO() {}

    public QuizSubmissionPOJO(Long userId, Map<Integer, List<Integer>> answers) {
        this.userId = userId;
        this.answers = answers;
    }

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

    @Override
    public String toString() {
        return "QuizSubmissionPOJO{" +
                "userId=" + userId +
                ", answers=" + answers +
                '}';
    }
}

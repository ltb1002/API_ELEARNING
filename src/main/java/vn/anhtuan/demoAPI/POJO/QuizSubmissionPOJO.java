package vn.anhtuan.demoAPI.POJO;

import java.util.Map;

public class QuizSubmissionPOJO {
    private Integer userId;
    private Integer quizId;
    private Map<String, Integer> answers; // questionId -> choiceId

    // Getters and setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public Map<String, Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, Integer> answers) {
        this.answers = answers;
    }
}

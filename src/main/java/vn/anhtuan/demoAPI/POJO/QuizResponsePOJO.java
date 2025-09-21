package vn.anhtuan.demoAPI.POJO;

import java.util.List;

public class QuizResponsePOJO {
    private Integer id;
    private String code;
    private String grade;
    private String subject;
    private String quizType;
    private Integer duration;
    private List<QuestionPOJO> questions;

    public QuizResponsePOJO() {}

    public QuizResponsePOJO(Integer id, String code, String grade, String subject, String quizType,
                            Integer duration, List<QuestionPOJO> questions) {
        this.id = id;
        this.code = code;
        this.grade = grade;
        this.subject = subject;
        this.quizType = quizType;
        this.duration = duration;
        this.questions = questions;
    }

    // getters & setters giữ nguyên...

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getQuizType() {
        return quizType;
    }

    public void setQuizType(String quizType) {
        this.quizType = quizType;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public List<QuestionPOJO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionPOJO> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "QuizResponsePOJO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", grade='" + grade + '\'' +
                ", subject='" + subject + '\'' +
                ", quizType='" + quizType + '\'' +
                ", duration=" + duration +
                ", questions=" + questions +
                '}';
    }
}

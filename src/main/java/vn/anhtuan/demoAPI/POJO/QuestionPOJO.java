package vn.anhtuan.demoAPI.POJO;

import java.util.List;

public class QuestionPOJO {
    private Integer id; // Thêm trường id
    private String content;
    private String explanation;
    private List<ChoicePOJO> choices;

    // Constructors
    public QuestionPOJO() {}

    public QuestionPOJO(Integer id, String content, String explanation, List<ChoicePOJO> choices) {
        this.id = id;
        this.content = content;
        this.explanation = explanation;
        this.choices = choices;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public List<ChoicePOJO> getChoices() { return choices; }
    public void setChoices(List<ChoicePOJO> choices) { this.choices = choices; }
}
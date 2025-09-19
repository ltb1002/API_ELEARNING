package vn.anhtuan.demoAPI.POJO;

import java.util.List;

public class QuestionPOJO {

    private String content;
    private String explanation;
    private List<ChoicePOJO> choices;

    // Constructors
    public QuestionPOJO() {}

    public QuestionPOJO(String content, String explanation, List<ChoicePOJO> choices) {
        this.content = content;
        this.explanation = explanation;
        this.choices = choices;
    }

    // Getters & Setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public List<ChoicePOJO> getChoices() { return choices; }
    public void setChoices(List<ChoicePOJO> choices) { this.choices = choices; }
}

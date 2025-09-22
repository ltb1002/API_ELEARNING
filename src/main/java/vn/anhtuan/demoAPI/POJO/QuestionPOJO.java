package vn.anhtuan.demoAPI.POJO;

import java.util.List;

public class QuestionPOJO {
    private Long id;
    private List<QuestionContentPOJO> contents; // Thay thế content bằng contents
    private String explanation;
    private List<ChoicePOJO> choices;

    // Constructors
    public QuestionPOJO() {}

    public QuestionPOJO(Long id, List<QuestionContentPOJO> contents, String explanation, List<ChoicePOJO> choices) {
        this.id = id;
        this.contents = contents;
        this.explanation = explanation;
        this.choices = choices;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<QuestionContentPOJO> getContents() { return contents; }
    public void setContents(List<QuestionContentPOJO> contents) { this.contents = contents; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public List<ChoicePOJO> getChoices() { return choices; }
    public void setChoices(List<ChoicePOJO> choices) { this.choices = choices; }
}
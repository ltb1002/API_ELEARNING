package vn.anhtuan.demoAPI.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChoicePOJO {

    private String content;

    @JsonProperty("is_correct")
    private Boolean isCorrect;

    // Constructors
    public ChoicePOJO() {}

    public ChoicePOJO(String content, Boolean isCorrect) {
        this.content = content;
        this.isCorrect = isCorrect;
    }

    // Getters & Setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
}

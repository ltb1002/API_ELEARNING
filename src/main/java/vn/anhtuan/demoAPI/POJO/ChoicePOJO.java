package vn.anhtuan.demoAPI.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChoicePOJO {
    private Long id; // Đổi từ Integer sang Long
    private String content;

    @JsonProperty("is_correct")
    private Boolean isCorrect;

    // Constructors
    public ChoicePOJO() {}

    public ChoicePOJO(Long id, String content, Boolean isCorrect) {
        this.id = id;
        this.content = content;
        this.isCorrect = isCorrect;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
}
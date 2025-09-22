package vn.anhtuan.demoAPI.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;
import vn.anhtuan.demoAPI.Entity.ContentType;

public class QuestionContentPOJO {
    private ContentType contentType;
    private String contentValue;

    // Constructors
    public QuestionContentPOJO() {}

    public QuestionContentPOJO(ContentType  contentType, String contentValue) {
        this.contentType = contentType;
        this.contentValue = contentValue;
    }

    // Getters & Setters
    @JsonProperty("content_type")
    public ContentType  getContentType() {
        return contentType;
    }

    public void setContentType(ContentType  contentType) {
        this.contentType = contentType;
    }

    @JsonProperty("content_value")
    public String getContentValue() {
        return contentValue;
    }

    public void setContentValue(String contentValue) {
        this.contentValue = contentValue;
    }
}

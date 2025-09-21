package vn.anhtuan.demoAPI.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum QuizStatus {
    COMPLETED,
    INCOMPLETE;

    @JsonCreator
    public static QuizStatus fromString(String value) {
        return value == null ? null : QuizStatus.valueOf(value.toUpperCase());
    }
}

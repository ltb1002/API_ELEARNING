package vn.anhtuan.demoAPI.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class QuizPOJO {

    private Integer id;

    @JsonProperty("grade_id")
    private Integer gradeId;

    @JsonProperty("subject_id")
    private Integer subjectId;

    @JsonProperty("subject_name")
    private String subjectName;

    @JsonProperty("chapter_id")
    private Long chapterId;

    @JsonProperty("chapter_title")
    private String chapterTitle;

    @JsonProperty("quiz_type_id")
    private Integer quizTypeId;

    private String code;

    private List<QuestionPOJO> questions;

    // Constructors
    public QuizPOJO() {}

    public QuizPOJO(Integer id, Integer gradeId, Integer subjectId, String subjectName,
                    Long chapterId, String chapterTitle, Integer quizTypeId,
                    String code, List<QuestionPOJO> questions) {
        this.id = id;
        this.gradeId = gradeId;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.chapterId = chapterId;
        this.chapterTitle = chapterTitle;
        this.quizTypeId = quizTypeId;
        this.code = code;
        this.questions = questions;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getGradeId() { return gradeId; }
    public void setGradeId(Integer gradeId) { this.gradeId = gradeId; }

    public Integer getSubjectId() { return subjectId; }
    public void setSubjectId(Integer subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public Long getChapterId() { return chapterId; }
    public void setChapterId(Long chapterId) { this.chapterId = chapterId; }

    public String getChapterTitle() { return chapterTitle; }
    public void setChapterTitle(String chapterTitle) { this.chapterTitle = chapterTitle; }

    public Integer getQuizTypeId() { return quizTypeId; }
    public void setQuizTypeId(Integer quizTypeId) { this.quizTypeId = quizTypeId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public List<QuestionPOJO> getQuestions() { return questions; }
    public void setQuestions(List<QuestionPOJO> questions) { this.questions = questions; }
}

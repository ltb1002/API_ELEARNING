package vn.anhtuan.demoAPI.POJO;

import java.util.List;

public class LessonPOJO {
    private Long id;
    private String title;
    private String videoUrl;
    private List<LessonContentPOJO> contents;
    private Integer subjectId; // Thêm để hiển thị filter
    private String subjectName; // Thêm để hiển thị trên UI
    private String chapterName;// Thêm để hiển thị context
    private Integer grade; //


    public LessonPOJO() {
    }

    public LessonPOJO(Long id, String title, String videoUrl, List<LessonContentPOJO> contents) {
        this.id = id;
        this.title = title;
        this.videoUrl = videoUrl;
        this.contents = contents;
    }

    public LessonPOJO(Long id, String title, String videoUrl, List<LessonContentPOJO> contents,
                      Integer subjectId, String subjectName, String chapterName) {
        this.id = id;
        this.title = title;
        this.videoUrl = videoUrl;
        this.contents = contents;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.chapterName = chapterName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<LessonContentPOJO> getContents() {
        return contents;
    }

    public void setContents(List<LessonContentPOJO> contents) {
        this.contents = contents;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}

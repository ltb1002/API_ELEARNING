package vn.anhtuan.demoAPI.POJO;

import java.util.List;

public class SubjectPOJO {
    private Integer id;
    private String code;
    private String name;
    private int grade;
    private List<ChapterPOJO> chapters;

    public SubjectPOJO() {
    }

    public SubjectPOJO(Integer id, String code, String name, int grade, List<ChapterPOJO> chapters) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.grade = grade;
        this.chapters = chapters;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public List<ChapterPOJO> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChapterPOJO> chapters) {
        this.chapters = chapters;
    }
}

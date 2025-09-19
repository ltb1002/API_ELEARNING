package vn.anhtuan.demoAPI.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(
        name = "subjects",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"code", "grade"})
        }
)
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String code;  // toan, nguvan, tienganh

    @Column(nullable = false)
    private String name;  // Toán, Ngữ Văn

    @Column(nullable = false)
    private int grade;    // 6,7,8,9

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapter> chapters;

    // Constructors
    public Subject() {
    }

    public Subject(Integer id, String code, String name, int grade, List<Chapter> chapters) {
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

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }
}

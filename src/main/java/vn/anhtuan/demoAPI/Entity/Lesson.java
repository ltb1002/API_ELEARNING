package vn.anhtuan.demoAPI.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bắt buộc Lesson phải thuộc Chapter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 500)
    private String videoUrl;

    // Nội dung bài học, sắp xếp theo contentOrder
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("contentOrder ASC")
    @JsonManagedReference
    private List<LessonContent> contents = new ArrayList<>();

    // Bài tập, sắp xếp theo orderNo
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderNo ASC")
    @JsonManagedReference
    private List<Exercise> exercises = new ArrayList<>();

    // ===== Constructors =====
    public Lesson() {
    }

    public Lesson(Chapter chapter, String title, String videoUrl) {
        this.chapter = chapter;
        this.title = title;
        this.videoUrl = videoUrl;
    }

    // ===== Getters & Setters =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
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

    public List<LessonContent> getContents() {
        return contents;
    }

    public void setContents(List<LessonContent> contents) {
        this.contents = contents;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    // ===== Helper Methods =====
    public void addContent(LessonContent content) {
        contents.add(content);
        content.setLesson(this);
    }

    public void removeContent(LessonContent content) {
        contents.remove(content);
        content.setLesson(null);
    }

    public void addExercise(Exercise exercise) {
        exercises.add(exercise);
        exercise.setLesson(this);
    }

    public void removeExercise(Exercise exercise) {
        exercises.remove(exercise);
        exercise.setLesson(null);
    }
}

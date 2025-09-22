package vn.anhtuan.demoAPI.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import vn.anhtuan.demoAPI.Entity.*;
import vn.anhtuan.demoAPI.Repository.*;

@Component
@Order(1) // Thứ tự thực thi ưu tiên cao nhất
public class DataLoaderService implements CommandLineRunner {

    private final SubjectRepository subjectRepo;
    private final ChapterRepository chapterRepo;
    private final LessonRepository lessonRepo;
    private final LessonContentRepository lessonContentRepo;
    private final ExerciseRepository exerciseRepo;
    private final ExerciseSolutionRepository exerciseSolutionRepo;

    public DataLoaderService(SubjectRepository subjectRepo,
                             ChapterRepository chapterRepo,
                             LessonRepository lessonRepo,
                             LessonContentRepository lessonContentRepo,
                             ExerciseRepository exerciseRepo,
                             ExerciseSolutionRepository exerciseSolutionRepo) {
        this.subjectRepo = subjectRepo;
        this.chapterRepo = chapterRepo;
        this.lessonRepo = lessonRepo;
        this.lessonContentRepo = lessonContentRepo;
        this.exerciseRepo = exerciseRepo;
        this.exerciseSolutionRepo = exerciseSolutionRepo;
    }

    @Override
    public void run(String... args) {
        String[] subjects = {"toan", "nguvan", "tienganh", "khoahoctunhien"};
        int[] grades = {6,7,8,9};

        ObjectMapper mapper = new ObjectMapper();

        for (String subjectName : subjects) {
            for (int grade : grades) {
                try {
                    // Kiểm tra dữ liệu đã load chưa
                    if (!subjectRepo.findByCodeAndGrade(subjectName.toLowerCase(), grade).isEmpty()) {
                        System.out.println("ℹ️ Data already loaded for " + subjectName + " grade " + grade + ", skipping");
                        continue;
                    }

                    // Đọc file JSON
                    String fileName = getFileName(subjectName, grade);
                    JsonNode root = mapper.readTree(new ClassPathResource(fileName).getInputStream());

                    for (JsonNode subjectNode : root.path("subjects")) {
                        Subject subject = new Subject();
                        subject.setCode(subjectNode.get("code").asText());
                        subject.setName(subjectNode.get("name").asText());
                        subject.setGrade(subjectNode.get("grade").asInt());
                        subjectRepo.save(subject);

                        for (JsonNode chapterNode : subjectNode.path("chapters")) {
                            Chapter chapter = new Chapter();
                            chapter.setTitle(chapterNode.get("title").asText());
                            chapter.setOrderNo(chapterNode.path("orderNo").asInt(1));
                            chapter.setSubject(subject);
                            chapterRepo.save(chapter);

                            for (JsonNode lessonNode : chapterNode.path("lessons")) {
                                Lesson lesson = new Lesson();
                                lesson.setTitle(lessonNode.get("title").asText());
                                lesson.setVideoUrl(lessonNode.path("videoUrl").asText(""));
                                lesson.setChapter(chapter);
                                lessonRepo.saveAndFlush(lesson);

                                // Load lesson contents
                                int contentOrder = 1;
                                for (JsonNode contentNode : lessonNode.path("contents")) {
                                    LessonContent content = new LessonContent();
                                    content.setLesson(lesson);
                                    content.setContentType(ContentType.valueOf(contentNode.get("type").asText().toUpperCase()));
                                    content.setContentValue(contentNode.get("value").asText());
                                    content.setContentOrder(contentOrder++);
                                    lessonContentRepo.save(content);
                                }

                                // Load exercises
                                int exerciseOrder = 1;
                                for (JsonNode exerciseNode : lessonNode.path("exercises")) {
                                    Exercise exercise = new Exercise();
                                    exercise.setLesson(lesson);
                                    exercise.setQuestion(exerciseNode.get("question").asText());
                                    exercise.setOrderNo(exerciseOrder++);
                                    exerciseRepo.saveAndFlush(exercise);

                                    int solutionOrder = 1;
                                    for (JsonNode solutionNode : exerciseNode.path("solutions")) {
                                        ExerciseSolution solution = new ExerciseSolution();
                                        solution.setExercise(exercise);
                                        solution.setSolutionType(ContentType.valueOf(solutionNode.get("type").asText().toUpperCase()));
                                        solution.setSolutionValue(solutionNode.get("value").asText());
                                        solution.setSolutionOrder(solutionOrder++);
                                        exerciseSolutionRepo.save(solution);
                                    }
                                }
                            }
                        }
                    }

                    System.out.println("✅ Loaded data from file: " + fileName);

                } catch (Exception e) {
                    System.err.println("❌ Failed to load data for subject " + subjectName + " grade " + grade);
                    e.printStackTrace();
                }
            }
        }
    }

    private String getFileName(String subject, int grade) {
        switch (subject.toLowerCase()) {
            case "toán":
            case "toan":
                return "toan_" + grade + ".json";
            case "khoahoctunhien":
                return "khoahoctunhien_" + grade + ".json";
            case "văn":
            case "nguvan":
                return "nguvan_" + grade + ".json";
            case "anh":
            case "tienganh":
                return "tienganh_" + grade + ".json";
            default:
                throw new IllegalArgumentException("Môn học không hợp lệ: " + subject);
        }
    }
}
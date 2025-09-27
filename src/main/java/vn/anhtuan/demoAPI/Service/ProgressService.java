package vn.anhtuan.demoAPI.Service;

import vn.anhtuan.demoAPI.Entity.LessonCompletion;
import vn.anhtuan.demoAPI.Entity.Progress;
import vn.anhtuan.demoAPI.POJO.ProgressResponsePOJO;

import java.util.List;

public interface ProgressService {
    // Đánh dấu bài học đã hoàn thành
    LessonCompletion completeLesson(Long userId, Long lessonId);

    // Hủy đánh dấu hoàn thành
    void uncompleteLesson(Long userId, Long lessonId);

    // Kiểm tra bài học đã hoàn thành chưa
    boolean isLessonCompleted(Long userId, Long lessonId);

    // Lấy tiến trình của user theo subject
    Progress getProgressByUserAndSubject(Long userId, Integer subjectId);

    // Lấy tất cả tiến trình của user
    List<Progress> getProgressByUser(Long userId);

    // Lấy tiến trình của user theo grade
    List<Progress> getProgressByUserAndGrade(Long userId, Integer grade);

    // Cập nhật tổng số bài học khi có thay đổi
    void updateTotalLessonsForSubject(Integer subjectId);

    // Chuyển đổi Progress sang DTO
    ProgressResponsePOJO convertToProgressResponseDTO(Progress progress);

}

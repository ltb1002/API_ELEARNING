package vn.anhtuan.demoAPI.REST;

import org.springframework.format.annotation.DateTimeFormat;
import vn.anhtuan.demoAPI.Entity.LessonCompletion;
import vn.anhtuan.demoAPI.Entity.Progress;
import vn.anhtuan.demoAPI.POJO.LessonCompletionRequestPOJO;
import vn.anhtuan.demoAPI.POJO.LessonCompletionResponsePOJO;
import vn.anhtuan.demoAPI.POJO.ProgressResponsePOJO;
import vn.anhtuan.demoAPI.POJO.QuizProgressPOJO;
import vn.anhtuan.demoAPI.Service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = "*")
public class ProgressController {

    @Autowired
    private ProgressService progressService;
    @Autowired
    private vn.anhtuan.demoAPI.Service.QuizResultService quizResultService;

    /**
     * Đánh dấu bài học đã hoàn thành
     */
    @PostMapping("/complete-lesson")
    public ResponseEntity<?> completeLesson(@RequestBody LessonCompletionRequestPOJO request) {
        try {
            LessonCompletion completion = progressService.completeLesson(request.getUserId(), request.getLessonId());

            // SỬA: Chuyển đổi sang DTO đơn giản
            LessonCompletionResponsePOJO responseDTO = new LessonCompletionResponsePOJO(
                    completion.getId(),
                    completion.getUser().getId(), // Chỉ lấy ID, không lấy cả User object
                    completion.getLesson().getId(), // Chỉ lấy ID, không lấy cả Lesson object
                    completion.getCompletedAt()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bài học đã được đánh dấu hoàn thành");
            response.put("data", responseDTO); // SỬA: dùng DTO thay vì Entity

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Hủy đánh dấu hoàn thành bài học
     */
    @DeleteMapping("/uncomplete-lesson/user/{userId}/lesson/{lessonId}")
    public ResponseEntity<?> uncompleteLesson(
            @PathVariable Long userId,
            @PathVariable Long lessonId) {
        try {
            progressService.uncompleteLesson(userId, lessonId);
            return ResponseEntity.ok().body(
                    new ApiResponse(true, "Đã hủy đánh dấu hoàn thành bài học", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(false, e.getMessage(), null)
            );
        }
    }

    /**
     * Kiểm tra trạng thái hoàn thành của một bài học
     */
    @GetMapping("/check-completion/user/{userId}/lesson/{lessonId}")
    public ResponseEntity<?> checkLessonCompletion(
            @PathVariable Long userId,
            @PathVariable Long lessonId) {
        try {
            // Giả sử bạn có service method để kiểm tra
            boolean isCompleted = progressService.isLessonCompleted(userId, lessonId);
            return ResponseEntity.ok().body(
                    new ApiResponse(true, "Thành công", new LessonCompletionStatus(isCompleted))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(false, e.getMessage(), null)
            );
        }
    }

    /**
     * Lấy tiến trình của user theo subject
     */
    @GetMapping("/user/{userId}/subject/{subjectId}")
    public ResponseEntity<?> getProgressByUserAndSubject(
            @PathVariable Long userId,
            @PathVariable Integer subjectId) {
        try {
            Progress progress = progressService.getProgressByUserAndSubject(userId, subjectId);
            ProgressResponsePOJO responseDTO = progressService.convertToProgressResponseDTO(progress);
            return ResponseEntity.ok().body(
                    new ApiResponse(true, "Thành công", responseDTO)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(false, e.getMessage(), null)
            );
        }
    }

    /**
     * Lấy tất cả tiến trình của user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllProgressByUser(@PathVariable Long userId) {
        try {
            List<Progress> progressList = progressService.getProgressByUser(userId);
            List<ProgressResponsePOJO> responseDTOs = progressList.stream()
                    .map(progressService::convertToProgressResponseDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(
                    new ApiResponse(true, "Thành công", responseDTOs)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(false, e.getMessage(), null)
            );
        }
    }

    /**
     * Lấy tiến trình của user theo grade
     */
    @GetMapping("/user/{userId}/grade/{grade}")
    public ResponseEntity<?> getProgressByUserAndGrade(
            @PathVariable Long userId,
            @PathVariable Integer grade) {
        try {
            List<Progress> progressList = progressService.getProgressByUserAndGrade(userId, grade);
            List<ProgressResponsePOJO> responseDTOs = progressList.stream()
                    .map(progressService::convertToProgressResponseDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(
                    new ApiResponse(true, "Thành công", responseDTOs)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(false, e.getMessage(), null)
            );
        }
    }

    /**
     * Cập nhật tổng số bài học cho subject (Admin only)
     */
    @PostMapping("/admin/update-total-lessons/subject/{subjectId}")
    public ResponseEntity<?> updateTotalLessonsForSubject(@PathVariable Integer subjectId) {
        try {
            progressService.updateTotalLessonsForSubject(subjectId);
            return ResponseEntity.ok().body(
                    new ApiResponse(true, "Đã cập nhật tổng số bài học cho môn học", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(false, e.getMessage(), null)
            );
        }
    }

    @PostMapping("/lessons/{lessonId}/complete")
    public ResponseEntity<Void> complete(@RequestParam Long userId,
                                         @PathVariable Long lessonId) {
        progressService.completeLesson(userId, lessonId);
        return ResponseEntity.ok().build();
    }

    // ===== DTO Classes for Response =====

    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;

        public ApiResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }

    public static class LessonCompletionStatus {
        private boolean completed;

        public LessonCompletionStatus(boolean completed) {
            this.completed = completed;
        }

        // Getters and Setters
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }
}
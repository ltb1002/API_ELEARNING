package vn.anhtuan.demoAPI.REST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.demoAPI.Entity.Quiz;
import vn.anhtuan.demoAPI.Entity.QuizResult;
import vn.anhtuan.demoAPI.POJO.ChoicePOJO;
import vn.anhtuan.demoAPI.POJO.QuestionPOJO;
import vn.anhtuan.demoAPI.POJO.QuizPOJO;
import vn.anhtuan.demoAPI.POJO.QuizSubmissionPOJO;
import vn.anhtuan.demoAPI.Service.QuizResultService;
import vn.anhtuan.demoAPI.Service.QuizService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = "*")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizResultService quizResultService;

    /**
     * Lấy tất cả quiz (chỉ thông tin cơ bản, không kèm câu hỏi/đáp án)
     */
    @GetMapping
    public ResponseEntity<List<QuizPOJO>> getAllQuizzes() {
        return ResponseEntity.ok(
                quizService.getAllQuizzes().stream()
                        .map(quizService::convertToQuizPOJO)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Lấy quiz theo id (thông tin cơ bản)
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuizPOJO> getQuizById(@PathVariable Integer id) {
        Quiz quiz = quizService.getQuizById(id);
        if (quiz != null) {
            QuizPOJO pojo = quizService.convertinfoQuizPOJO(quiz);
            return ResponseEntity.ok(pojo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lấy quiz theo code (bao gồm luôn câu hỏi + lựa chọn)
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<QuizPOJO> getQuizByCode(@PathVariable String code) {
        var quiz = quizService.getQuizByCode(code);
        if (quiz == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(quizService.convertToQuizPOJO(quiz));
    }

    /**
     * Lọc quiz theo grade, subject, quizType
     */
    @GetMapping("/filter")
    public ResponseEntity<List<QuizPOJO>> getQuizzesByFilter(
            @RequestParam Integer gradeId,
            @RequestParam Integer subjectId,
            @RequestParam(required = false) Integer quizTypeId) {

        var quizzes = (quizTypeId != null)
                ? quizService.getQuizzesByGradeSubjectAndType(gradeId, subjectId, quizTypeId)
                : quizService.getQuizzesByGradeAndSubject(gradeId, subjectId);

        return ResponseEntity.ok(
                quizzes.stream()
                        .map(quizService::convertToQuizPOJO)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Lấy danh sách câu hỏi của 1 quiz
     */
    @GetMapping("/{quizId}/questions")
    public ResponseEntity<List<QuestionPOJO>> getQuizQuestions(@PathVariable Integer quizId) {
        return ResponseEntity.ok(
                quizService.getQuizQuestions(quizId).stream()
                        .map(quizService::convertToQuestionPOJO)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Lấy danh sách lựa chọn của 1 câu hỏi
     */
    @GetMapping("/questions/{questionId}/choices")
    public ResponseEntity<List<ChoicePOJO>> getQuestionChoices(@PathVariable Integer questionId) {
        return ResponseEntity.ok(
                quizService.getQuestionChoices(questionId).stream()
                        .map(quizService::convertToChoicePOJO)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Nộp quiz
     */
    @PostMapping("/{quizId}/submit")
    public ResponseEntity<Map<String, Object>> submitQuiz(
            @PathVariable Integer quizId,
            @RequestParam Long userId,
            @RequestBody Map<String, Integer> userAnswers) {

        try {
            Map<Integer, Integer> integerKeyMap = new HashMap<>();
            for (Map.Entry<String, Integer> entry : userAnswers.entrySet()) {
                integerKeyMap.put(Integer.parseInt(entry.getKey()), entry.getValue());
            }

            QuizResult result = quizResultService.submitQuiz(userId, quizId, integerKeyMap);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("score", result.getScore());
            response.put("correctAnswers", result.getCorrectAnswers());
            response.put("totalQuestions", result.getTotalQuestions());
            response.put("quizResult", result);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", "Internal server error"
            ));
        }
    }

    /**
     * Lấy thống kê quiz
     */
    @GetMapping("/{quizId}/statistics")
    public ResponseEntity<Map<String, Object>> getQuizStatistics(@PathVariable Integer quizId) {
        return ResponseEntity.ok(quizResultService.getQuizStatistics(quizId));
    }

    /**
     * Lấy thống kê người dùng
     */
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable Long userId) {
        return ResponseEntity.ok(quizResultService.getUserStatistics(userId));
    }
}
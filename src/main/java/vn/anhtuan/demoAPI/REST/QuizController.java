package vn.anhtuan.demoAPI.REST;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.demoAPI.Entity.Grade;
import vn.anhtuan.demoAPI.Entity.Quiz;
import vn.anhtuan.demoAPI.Entity.QuizResult;
import vn.anhtuan.demoAPI.Entity.Subject;
import vn.anhtuan.demoAPI.POJO.*;
import vn.anhtuan.demoAPI.Service.QuizResultService;
import vn.anhtuan.demoAPI.Service.QuizService;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = "*")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizResultService quizResultService;

    @Autowired
    private ObjectMapper mapper;

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
                quizService.getQuizQuestions(quizId.longValue()).stream()
                        .map(quizService::convertToQuestionPOJO)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Lấy danh sách lựa chọn của 1 câu hỏi
     */
    @GetMapping("/questions/{questionId}/choices")
    public ResponseEntity<List<ChoicePOJO>> getQuestionChoices(@PathVariable Long questionId) {
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
            @RequestBody QuizSubmissionPOJO submission) {

        try {
            QuizResult result = quizResultService.submitQuiz(
                    submission.getUserId(),
                    quizId,
                    submission.getAnswers(),
                    submission.getDurationSeconds()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("score", result.getScore());
            response.put("correctAnswers", result.getCorrectAnswers());
            response.put("totalQuestions", result.getTotalQuestions());
            response.put("attemptNo", result.getAttemptNo());
            response.put("durationSeconds", result.getDurationSeconds());
            response.put("passed", result.getScore().compareTo(new BigDecimal("5.0")) >= 0);
            response.put("completedAt", result.getCompletedAt());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "error", "Internal server error: " + e.getMessage()
            ));
        }
    }

    /**
     * Lấy thống kê quiz
     */
    /**
     * Lấy lịch sử làm bài của tất cả người dùng cho một quiz cụ thể
     */
    @GetMapping("/{quizId}/history")
    public ResponseEntity<List<QuizHistoryPOJO>> getQuizHistory(
            @PathVariable Integer quizId) {

        try {
            List<QuizResult> results = quizResultService.getQuizResultsForQuiz(quizId);

            List<QuizHistoryPOJO> history = results.stream()
                    .map(result -> new QuizHistoryPOJO(
                            result.getAttemptNo(),
                            result.getScore(),
                            result.getCorrectAnswers(),
                            result.getTotalQuestions(),
                            result.getDurationSeconds(),
                            result.getCompletedAt(),
                            result.getStatus().toString()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Lấy thống kê người dùng
     */
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable Long userId) {
        return ResponseEntity.ok(quizResultService.getUserStatistics(userId));
    }

    /**
     * Lấy lịch sử làm bài của một người dùng cho một quiz cụ thể
     */
    @GetMapping("/{quizId}/users/{userId}/history")
    public ResponseEntity<List<QuizHistoryPOJO>> getQuizHistoryForUser(
            @PathVariable Integer quizId,
            @PathVariable Long userId) {

        try {
            List<QuizResult> results = quizResultService.getUserQuizResultsForQuiz(userId, quizId);

            List<QuizHistoryPOJO> history = results.stream()
                    .map(result -> new QuizHistoryPOJO(
                            result.getAttemptNo(),
                            result.getScore(),
                            result.getCorrectAnswers(),
                            result.getTotalQuestions(),
                            result.getDurationSeconds(),
                            result.getCompletedAt(),
                            result.getStatus().toString()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/grades")
    public ResponseEntity<List<GradePOJO>> getAllGrades() {
        try {
            // Lấy tất cả subjects và extract grades duy nhất
            List<Subject> subjects = quizService.getAllSubjects();
            Set<Integer> gradeIds = subjects.stream()
                    .map(Subject::getGrade)
                    .collect(Collectors.toSet());

            List<GradePOJO> gradePOJOs = gradeIds.stream()
                    .map(gradeId -> {
                        Grade grade = quizService.getGradeById(gradeId);
                        return grade != null ? convertToGradePOJO(grade) :
                                new GradePOJO(gradeId, "Lớp " + gradeId, "GRADE_" + gradeId);
                    })
                    .sorted(Comparator.comparing(GradePOJO::getId))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(gradePOJOs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Lấy danh sách môn học theo khối lớp
     */
    @GetMapping("/grades/{gradeId}/subjects")
    public ResponseEntity<List<SubjectPOJO>> getSubjectsByGrade(@PathVariable Integer gradeId) {
        try {
            List<Subject> subjects = quizService.getAllSubjects().stream()
                    .filter(subject -> subject.getGrade() == gradeId)
                    .collect(Collectors.toList());

            List<SubjectPOJO> subjectPOJOs = subjects.stream()
                    .map(this::convertToSubjectPOJO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(subjectPOJOs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Lấy danh sách quiz theo khối lớp và môn học
     */
    @GetMapping("/grades/{gradeId}/subjects/{subjectId}")
    public ResponseEntity<List<QuizPOJO>> getQuizzesByGradeAndSubject(
            @PathVariable Integer gradeId,
            @PathVariable Integer subjectId) {

        try {
            // Verify subject belongs to grade
            Subject subject = quizService.getSubjectById(subjectId);
            if (subject == null || subject.getGrade() != gradeId) {
                return ResponseEntity.notFound().build();
            }

            List<Quiz> quizzes = quizService.getQuizzesByGradeAndSubject(gradeId, subjectId);
            List<QuizPOJO> quizPOJOs = quizzes.stream()
                    .map(quizService::convertToQuizPOJO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(quizPOJOs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Lấy danh sách quiz theo môn học (không cần grade vì subject đã có grade)
     */
    @GetMapping("/subjects/{subjectId}")
    public ResponseEntity<List<QuizPOJO>> getQuizzesBySubject(@PathVariable Integer subjectId) {
        try {
            Subject subject = quizService.getSubjectById(subjectId);
            if (subject == null) {
                return ResponseEntity.notFound().build();
            }

            List<Quiz> quizzes = quizService.getQuizzesByGradeAndSubject(subject.getGrade(), subjectId);
            List<QuizPOJO> quizPOJOs = quizzes.stream()
                    .map(quizService::convertToQuizPOJO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(quizPOJOs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Lấy thông tin chi tiết một quiz cụ thể
     */
    @GetMapping("/{quizId}/details")
    public ResponseEntity<QuizPOJO> getQuizDetails(@PathVariable Integer quizId) {
        try {
            Quiz quiz = quizService.getQuizById(quizId);
            if (quiz == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(quizService.convertToQuizPOJO(quiz));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Các method convert POJO
    private GradePOJO convertToGradePOJO(Grade grade) {
        GradePOJO pojo = new GradePOJO();
        pojo.setId(grade.getId());
        pojo.setName(grade.getName());
        pojo.setCode(grade.getCode());
        return pojo;
    }

    private SubjectPOJO convertToSubjectPOJO(Subject subject) {
        SubjectPOJO pojo = new SubjectPOJO();
        pojo.setId(subject.getId());
        pojo.setCode(subject.getCode());
        pojo.setName(subject.getName());
        pojo.setGrade(subject.getGrade());
        return pojo;
    }

}
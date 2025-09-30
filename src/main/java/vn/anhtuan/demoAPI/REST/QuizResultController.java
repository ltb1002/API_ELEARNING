package vn.anhtuan.demoAPI.REST;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.demoAPI.Entity.QuizResult;
import vn.anhtuan.demoAPI.POJO.QuizProgressPOJO;
import vn.anhtuan.demoAPI.Service.QuizResultService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizresults")
@CrossOrigin(origins = "*")
public class QuizResultController {

    private final QuizResultService quizResultService;

    public QuizResultController(QuizResultService quizResultService) {
        this.quizResultService = quizResultService;
    }

    // 1) Overall accuracy (tổng đúng/tổng câu, % tổng)
    @GetMapping("/accuracy")
    public ResponseEntity<?> overall(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer gradeId,
            @RequestParam(required = false) Integer subjectId,
            @RequestParam(required = false) Integer quizTypeId
    ) {
        var dto = quizResultService
                .getAccuracyFromProgress(userId, gradeId, subjectId, quizTypeId)
                .orElseGet(() -> new QuizProgressPOJO(0, 0, 0.0, null));

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("gradeId", gradeId);
        data.put("subjectId", subjectId);
        data.put("quizTypeId", quizTypeId);
        data.put("correctSum", dto.getCorrectSum());
        data.put("totalSum", dto.getTotalSum());
        data.put("percentAccuracy", dto.getPercentAccuracy());
        data.put("updatedAt", dto.getUpdatedAt());

        return ResponseEntity.ok(new ApiResponse(true, "OK", data));
    }

    // 2) Daily accuracy (từ 1 mốc thời gian)
    @GetMapping("/accuracy/daily")
    public ResponseEntity<?> daily(
            @RequestParam Long userId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate
    ) {
        var data = quizResultService.getDailyAccuracy(userId, fromDate, null, null, null, null);
        return ResponseEntity.ok(new ApiResponse(true, "OK", data));
    }

    // 3) Daily accuracy theo khoảng ngày (YYYY-MM-DD)
    @GetMapping("/accuracy/daily/range")
    public ResponseEntity<?> dailyRange(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        var data = quizResultService.getDailyAccuracyByRange(userId, fromDate, toDate);
        return ResponseEntity.ok(new ApiResponse(true, "OK", data));
    }
    @GetMapping("/quizresults/quiz/{quizId}/history")
    public ResponseEntity<?> getHistory(
            @RequestParam Long userId,
            @PathVariable Integer quizId) {
        List<QuizResult> results = quizResultService.getUserQuizResultsForQuiz(userId, quizId);
        return ResponseEntity.ok(Map.of("success", true, "data", results));
    }

    // 4) Average of daily percentages (trung bình cộng theo ngày)
    @GetMapping("/accuracy/daily/average")
    public ResponseEntity<?> getAverageDailyAccuracy(@RequestParam Long userId) {
        Double avg = quizResultService.getAverageDailyAccuracy(userId);
        Map<String, Object> data = Map.of(
                "userId", userId,
                "averageDailyPercentage", avg == null ? 0.0 : Math.round(avg * 100.0) / 100.0
        );
        return ResponseEntity.ok(new ApiResponse(true, "OK", data));
    }

    public record ApiResponse(boolean success, String message, Object data) {}
}


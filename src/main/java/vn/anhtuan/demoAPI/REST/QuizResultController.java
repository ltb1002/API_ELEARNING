// package vn.anhtuan.demoAPI.REST;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.demoAPI.POJO.QuizProgressPOJO;
import vn.anhtuan.demoAPI.Service.QuizResultService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/quizresults")
@CrossOrigin(origins = "*")
public class QuizResultController {

    private final QuizResultService quizResultService;

    public QuizResultController(QuizResultService quizResultService) {
        this.quizResultService = quizResultService;
    }

    // Overall accuracy: trả JSON có trường correctSum/totalSum/percentAccuracy, giống /api/progress/accuracy hiện tại
    @GetMapping("/accuracy")
    public ResponseEntity<?> overall(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer gradeId,
            @RequestParam(required = false) Integer subjectId,
            @RequestParam(required = false) Integer quizTypeId
    ) {
        var dtoOpt = quizResultService.getAccuracyFromProgress(userId, gradeId, subjectId, quizTypeId);
        var dto = dtoOpt.orElseGet(() -> new QuizProgressPOJO(0, 0, 0.0, null));

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

    // Daily accuracy: trả "day/correctSum/totalSum/percentAccuracy" (y chang /api/progress/accuracy/daily)
    @GetMapping("/accuracy/daily")
    public ResponseEntity<?> daily(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer gradeId,
            @RequestParam(required = false) Integer subjectId,
            @RequestParam(required = false) Integer quizTypeId,
            @RequestParam(required = false) Long chapterId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate
    ) {
        if (fromDate == null) fromDate = LocalDateTime.now().minusDays(30);
        var data = quizResultService.getDailyAccuracy(userId, fromDate, gradeId, subjectId, quizTypeId, chapterId);
        return ResponseEntity.ok(new ApiResponse(true, "OK", data));
    }

    // Dùng lại class ApiResponse như trong ProgressController để đồng nhất shape
    public static class ApiResponse {
        private boolean success; private String message; private Object data;
        public ApiResponse(boolean s, String m, Object d){ this.success=s; this.message=m; this.data=d; }
        public boolean isSuccess(){return success;} public String getMessage(){return message;} public Object getData(){return data;}
    }
}

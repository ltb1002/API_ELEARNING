package vn.anhtuan.demoAPI.REST;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.anhtuan.demoAPI.POJO.QuizProgressPOJO;
import vn.anhtuan.demoAPI.Service.QuizHistoryService;
import vn.anhtuan.demoAPI.Service.QuizProgressService;

@RestController
@RequestMapping("/api/quizzes")
public class QuizProgressController {
    private final QuizHistoryService historyService;
    private final QuizProgressService service;
    public QuizProgressController(QuizProgressService service, QuizHistoryService historyService) {
        this.service = service;
        this.historyService = historyService;}

//    @GetMapping("/progress")
//    public ResponseEntity<QuizProgressPOJO> progress(
//            @RequestParam Long userId,
//            @RequestParam(required = false) Integer gradeId,
//            @RequestParam(required = false) Integer subjectId,
//            @RequestParam(required = false) Integer quizTypeId,
//            @RequestParam(required = false) Long chapterId
//    ) {
//        return ResponseEntity.ok(service.getProgress(userId, gradeId, subjectId, quizTypeId, chapterId));
//    }

    @GetMapping("/history")
    public ResponseEntity<java.util.List<vn.anhtuan.demoAPI.POJO.QuizDailyStatPOJO>> history(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "7") Integer days,
            @RequestParam(required = false) Integer gradeId,
            @RequestParam(required = false) Integer subjectId,
            @RequestParam(required = false) Integer quizTypeId,
            @RequestParam(required = false) Long chapterId
    ) {
        return ResponseEntity.ok(
                historyService.getDailyAccuracy(userId, days, gradeId, subjectId, quizTypeId, chapterId)
        );
    }

}


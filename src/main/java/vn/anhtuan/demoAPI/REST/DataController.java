package vn.anhtuan.demoAPI.REST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.demoAPI.Entity.Grade;
import vn.anhtuan.demoAPI.Entity.QuizType;
import vn.anhtuan.demoAPI.Entity.Subject;
import vn.anhtuan.demoAPI.Service.QuizService;

import java.util.List;

@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "*")
public class DataController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/grades")
    public ResponseEntity<List<Grade>> getAllGrades() {
        return ResponseEntity.ok(quizService.getAllGrades());
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(quizService.getAllSubjects());
    }

    @GetMapping("/quiz-types")
    public ResponseEntity<List<QuizType>> getAllQuizTypes() {
        return ResponseEntity.ok(quizService.getAllQuizTypes());
    }

    @GetMapping("/grades/{id}")
    public ResponseEntity<Grade> getGradeById(@PathVariable Integer id) {
        Grade grade = quizService.getGradeById(id);
        if (grade != null) {
            return ResponseEntity.ok(grade);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/subjects/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Integer id) {
        Subject subject = quizService.getSubjectById(id);
        if (subject != null) {
            return ResponseEntity.ok(subject);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/quiz-types/{id}")
    public ResponseEntity<QuizType> getQuizTypeById(@PathVariable Integer id) {
        QuizType quizType = quizService.getQuizTypeById(id);
        if (quizType != null) {
            return ResponseEntity.ok(quizType);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

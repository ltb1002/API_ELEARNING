package vn.anhtuan.demoAPI.REST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.demoAPI.Entity.User;
import vn.anhtuan.demoAPI.Service.QuizResultService;
import vn.anhtuan.demoAPI.Service.UserService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private QuizResultService quizResultService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.findByEmailIgnoreCase(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/{userId}/results")
    public ResponseEntity<Map<String, Object>> getUserResults(@PathVariable Long userId) {
        Map<String, Object> stats = quizResultService.getUserStatistics(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{userId}/results/{subjectId}")
    public ResponseEntity<Double> getUserSubjectAverage(
            @PathVariable Long userId,
            @PathVariable Integer subjectId) {

        Double average = quizResultService.getUserAverageScoreBySubject(userId, subjectId);
        return ResponseEntity.ok(average != null ? average : 0.0);
    }
}


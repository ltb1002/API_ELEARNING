package vn.anhtuan.demoAPI.REST;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.demoAPI.Entity.UserStreak;
import vn.anhtuan.demoAPI.POJO.UserStreakPOJO;
import vn.anhtuan.demoAPI.Service.UserStreakService;

@RestController
@RequestMapping("/api/streak")
public class StreakController {

    private final UserStreakService streakService;

    public StreakController(UserStreakService streakService) {
        this.streakService = streakService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserStreakPOJO> getStreak(@PathVariable Long userId) {
        UserStreak s = streakService.getStreak(userId);
        return ResponseEntity.ok(toPOJO(s));
    }

    @PostMapping("/update")
    public ResponseEntity<UserStreakPOJO> updateStreak(@RequestParam Long userId) {
        UserStreak s = streakService.updateStreak(userId);
        return ResponseEntity.ok(new UserStreakPOJO(
                s.getStreakCount(), s.getBestStreak(), s.getTotalDays(), s.getLastActiveDate()
        ));
    }

    private UserStreakPOJO toPOJO(UserStreak s) {
        return new UserStreakPOJO(
                s.getStreakCount(),     // dùng streakCount thay cho currentStreak
                s.getBestStreak(),
                s.getTotalDays(),
                s.getLastActiveDate()
        );
    }
}

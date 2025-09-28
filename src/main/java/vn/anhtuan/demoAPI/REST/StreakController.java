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

    // Lấy thông tin streak hiện tại
    @GetMapping("/{userId}")
    public ResponseEntity<UserStreakPOJO> getStreak(@PathVariable Long userId) {
        UserStreak s = streakService.getStreak(userId);
        return ResponseEntity.ok(toPOJO(s));
    }

    // "Chạm" vào streak hôm nay (đồng bộ với FE: POST /api/streak/{userId}/touch)
    @PostMapping("/{userId}/touch")
    public ResponseEntity<UserStreakPOJO> touch(@PathVariable Long userId) {
        UserStreak s = streakService.touch(userId);
        return ResponseEntity.ok(toPOJO(s));
    }

    // Giữ endpoint cũ nếu nơi khác còn dùng: POST /api/streak/update?userId=8
    @PostMapping("/update")
    public ResponseEntity<UserStreakPOJO> updateStreak(@RequestParam Long userId) {
        UserStreak s = streakService.touch(userId); // dùng chung logic touch
        return ResponseEntity.ok(toPOJO(s));
    }

    private static UserStreakPOJO toPOJO(UserStreak s) {
        return new UserStreakPOJO(
                s.getStreakCount(),
                s.getBestStreak(),
                s.getTotalDays(),
                s.getLastActiveDate()
        );
    }
}

package vn.anhtuan.demoAPI.REST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.demoAPI.Service.UserSessionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user-session")
public class UserSessionController {

    @Autowired
    private UserSessionService userSessionService;

    @PostMapping("/record")
    public ResponseEntity<?> recordSession(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        var session = userSessionService.saveSession(userId, start, end);
        int totalMinutes = userSessionService.getTotalMinutesByDate(userId, start.toLocalDate());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", session);
        response.put("date", start.toLocalDate());
        response.put("totalMinutes", totalMinutes);
        response.put("isStudiedDay", totalMinutes >= 15);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/date/{date}")
    public ResponseEntity<?> getSessionsByDate(
            @PathVariable Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(userSessionService.getSessionsByDate(userId, date));
    }

    @GetMapping("/{userId}/daily-detail/{date}")
    public ResponseEntity<?> getDailyDetail(
            @PathVariable Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var sessions = userSessionService.getSessionsByDate(userId, date);
        int totalMinutes = userSessionService.getTotalMinutesByDate(userId, date);

        Map<String, Object> response = new HashMap<>();
        response.put("date", date);
        response.put("totalMinutes", totalMinutes);
        response.put("isStudiedDay", totalMinutes >= 15);
        response.put("sessions", sessions);

        return ResponseEntity.ok(response);
    }
}

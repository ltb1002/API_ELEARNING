package vn.anhtuan.demoAPI.REST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.demoAPI.Entity.UserActivity;
import vn.anhtuan.demoAPI.POJO.UserActivityPOJO;
import vn.anhtuan.demoAPI.POJO.UserStreakResponsePOJO;
import vn.anhtuan.demoAPI.Service.UserActivityService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user-activity")
public class UserActivityController {

    @Autowired
    private UserActivityService userActivityService;

    // Ghi nhận activity mới
    @PostMapping
    public ResponseEntity<?> recordActivity(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate activityDate,
            @RequestParam Integer additionalMinutes) {

        try {
            // Validate input
            if (additionalMinutes< 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Minutes used cannot be negative");
                return ResponseEntity.badRequest().body(response);
            }


            // Lấy tổng thời gian hiện tại trước khi cập nhật
            Integer currentTotalMinutes = userActivityService.getTotalMinutesByDate(userId, activityDate);

            UserActivity activity = userActivityService.saveOrUpdateActivity(userId, activityDate, additionalMinutes);

            // Kiểm tra xem có được tính là đã học không
            boolean wasStudiedBefore = userActivityService.isStudiedDay(currentTotalMinutes);
            boolean isStudied = userActivityService.isStudiedDay(activity.getMinutesUsed());



            // Convert entity to DTO for response
            UserActivityPOJO responseDTO = new UserActivityPOJO();
            responseDTO.setId(activity.getId());
            responseDTO.setUserId(activity.getUserId());
            responseDTO.setActivityDate(activity.getActivityDate());
            responseDTO.setMinutesUsed(activity.getMinutesUsed());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", responseDTO);
            response.put("previousTotalMinutes", currentTotalMinutes);
            response.put("additionalMinutes", additionalMinutes);
            response.put("newTotalMinutes", activity.getMinutesUsed());
            response.put("isStudiedDay", isStudied);
            response.put("wasStudiedBefore", wasStudiedBefore);
            response.put("statusChanged", (wasStudiedBefore != isStudied));
            response.put("message", "Activity recorded successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error recording activity: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Lấy tổng thời gian học trong ngày
    @GetMapping("/{userId}/total-minutes/{date}")
    public ResponseEntity<?> getTotalMinutesByDate(
            @PathVariable Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        try {
            Integer totalMinutes = userActivityService.getTotalMinutesByDate(userId, date);
            boolean isStudied = userActivityService.isStudiedDay(totalMinutes);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("date", date);
            response.put("totalMinutes", totalMinutes);
            response.put("isStudiedDay", isStudied);
            response.put("minStudyMinutes", 15);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error fetching total minutes: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Lấy thông tin streak và calendar
    @GetMapping("/streak/{userId}")
    public ResponseEntity<?> getUserStreakAndCalendar(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "3") int months) {

        try {
            UserStreakResponsePOJO streakData = userActivityService.getUserStreakAndCalendar(userId, months);
            System.out.println("DEBUG streakData = " + streakData); // hoặc log JSON

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", streakData);
            response.put("minStudyMinutes", 15); // Thông báo ngưỡng cho frontend

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error fetching streak data: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Lấy activity theo ngày
    @GetMapping("/{userId}/date/{date}")
    public ResponseEntity<?> getActivityByDate(
            @PathVariable Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        try {
            UserActivityPOJO activity = userActivityService.getActivityByDate(userId, date);
            boolean isStudied = activity != null && userActivityService.isStudiedDay(activity.getMinutesUsed());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", activity);
            response.put("isStudiedDay", isStudied);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error fetching activity: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Lấy activities trong khoảng thời gian
    @GetMapping("/{userId}/period")
    public ResponseEntity<?> getActivitiesInPeriod(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            if (startDate.isAfter(endDate)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Start date cannot be after end date");
                return ResponseEntity.badRequest().body(response);
            }

            var activities = userActivityService.getActivitiesInDateRange(userId, startDate, endDate);
            Integer totalMinutes = userActivityService.getTotalValidMinutesInPeriod(userId, startDate, endDate);
            Long validStudyDays = userActivityService.getValidStudyDaysCount(userId, startDate, endDate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", activities);
            response.put("totalMinutes", totalMinutes);
            response.put("totalDays", activities.size());
            response.put("validStudyDays", validStudyDays); // Số ngày học hợp lệ (>15 phút)

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error fetching activities: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Lấy tổng số phút học trong tháng (chỉ tính những ngày học trên 15 phút)
    @GetMapping("/{userId}/monthly-stats")
    public ResponseEntity<?> getMonthlyStats(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month) {

        try {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            var activities = userActivityService.getActivitiesInDateRange(userId, startDate, endDate);
            Integer totalMinutes = userActivityService.getTotalValidMinutesInPeriod(userId, startDate, endDate);
            Long studiedDays = userActivityService.getValidStudyDaysCount(userId, startDate, endDate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                    "year", year,
                    "month", month,
                    "totalMinutes", totalMinutes,
                    "studiedDays", studiedDays, // Số ngày học hợp lệ (>15 phút)
                    "totalDays", endDate.getDayOfMonth(),
                    "activities", activities,
                    "minStudyMinutes", 15
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error fetching monthly stats: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // API để kiểm tra xem một ngày cụ thể có được tính là đã học không
    @GetMapping("/{userId}/check-studied/{date}")
    public ResponseEntity<?> checkIfStudiedDay(
            @PathVariable Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        try {
            UserActivityPOJO activity = userActivityService.getActivityByDate(userId, date);
            boolean isStudied = activity != null && userActivityService.isStudiedDay(activity.getMinutesUsed());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("date", date);
            response.put("hasActivity", activity != null);
            response.put("minutesUsed", activity != null ? activity.getMinutesUsed() : 0);
            response.put("isStudiedDay", isStudied);
            response.put("minStudyMinutes", 15);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error checking study status: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
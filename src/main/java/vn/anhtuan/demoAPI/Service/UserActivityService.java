package vn.anhtuan.demoAPI.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.anhtuan.demoAPI.Entity.UserActivity;
import vn.anhtuan.demoAPI.Entity.UserSession;
import vn.anhtuan.demoAPI.POJO.CalendarDayPOJO;
import vn.anhtuan.demoAPI.POJO.UserActivityPOJO;
import vn.anhtuan.demoAPI.POJO.UserStreakResponsePOJO;
import vn.anhtuan.demoAPI.REST.UserActivityController;
import vn.anhtuan.demoAPI.Repository.UserActivityRepository;
import vn.anhtuan.demoAPI.Repository.UserSessionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserActivityService {

    private static final int MIN_STUDY_MINUTES = 15; // Ngưỡng tối thiểu 15 phút

    @Autowired
    private UserActivityRepository userActivityRepository;
    @Autowired
    private UserSessionRepository userSessionRepository;


    // Tạo hoặc cập nhật activity (CỘNG DỒN PHÚT)
    public UserActivity saveNewActivity(Long userId, LocalDate activityDate, Integer minutes) {
        UserActivity newActivity = new UserActivity(userId, activityDate, minutes);
        return userActivityRepository.save(newActivity);
    }
    public UserActivity saveOrUpdateActivity(Long userId, LocalDate activityDate, Integer additionalMinutes) {
        Optional<UserActivity> existingActivity = userActivityRepository.findByUserIdAndActivityDate(userId, activityDate);
        UserActivity activity;
        if (existingActivity.isPresent()) {
            activity = existingActivity.get();
            int currentMinutes = activity.getMinutesUsed();
            activity.setMinutesUsed(currentMinutes + additionalMinutes);
            activity = userActivityRepository.save(activity);
        } else {
            activity = new UserActivity(userId, activityDate, additionalMinutes);
            activity = userActivityRepository.save(activity);
        }
        // 🔹 Ghi thêm vào user_session (mỗi lần học là 1 session riêng)
        UserSession session = new UserSession();
        session.setUserId(userId);
        session.setActivityDate(activityDate);
        session.setMinutesUsed(additionalMinutes);
        session.setStartTime(LocalDateTime.now());
        session.setEndTime(LocalDateTime.now().plusMinutes(additionalMinutes));
        userSessionRepository.save(session);

        return activity;
    }
// ✅ Tính tổng phút học mỗi ngày
    public Map<LocalDate, Integer> getDailyTotals(Long userId, LocalDate startDate, LocalDate endDate) {
    List<Object[]> results = userActivityRepository.getDailyTotalMinutes(userId, startDate, endDate);
    Map<LocalDate, Integer> dailyTotals = new HashMap<>();
    for (Object[] row : results) {
        LocalDate date = (LocalDate) row[0];
        Long totalMinutes = (Long) row[1];
        dailyTotals.put(date, totalMinutes.intValue());
    }
    return dailyTotals;
}
    // ⚠️ SỬA: ≥ 15 phút mới được tính là đã học (trước đó bạn dùng > 15)
    public boolean isStudiedDay(Integer minutesUsed) {
        return minutesUsed != null && minutesUsed >= MIN_STUDY_MINUTES;
    }

    // Lấy tổng thời gian học trong ngày
    public Integer getTotalMinutesByDate(Long userId, LocalDate date) {
        Optional<UserActivity> activity = userActivityRepository.findByUserIdAndActivityDate(userId, date);
        return activity.map(UserActivity::getMinutesUsed).orElse(0);
    }


    // Lấy activity theo user và ngày
    public UserActivityPOJO getActivityByDate(Long userId, LocalDate date) {
        Optional<UserActivity> activity = userActivityRepository.findByUserIdAndActivityDate(userId, date);
        return activity.map(this::convertToDTO).orElse(null);
    }

    // Lấy tất cả activities của user trong khoảng thời gian
    public List<UserActivityPOJO> getActivitiesInDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        List<UserActivity> activities = userActivityRepository.findByUserIdAndActivityDateBetween(userId, startDate, endDate);
        return activities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lấy thông tin streak và calendar
    public UserStreakResponsePOJO getUserStreakAndCalendar(Long userId, int monthsToShow) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(monthsToShow);

        // Lấy dữ liệu activities trong khoảng thời gian
        List<UserActivity> activities = userActivityRepository.findByUserIdAndActivityDateBetween(userId, startDate, endDate);

        // Tính streak
        int currentStreak = calculateCurrentStreak(userId, activities);

        // Tạo dữ liệu calendar
        List<CalendarDayPOJO> calendarDays = generateCalendarData(userId, startDate, endDate, activities, currentStreak);

        // Lấy danh sách ngày trong streak hiện tại
        List<LocalDate> streakDays = getCurrentStreakDays(userId, activities, currentStreak);

        UserStreakResponsePOJO response = new UserStreakResponsePOJO();
        response.setUserId(userId);
        response.setCurrentStreak(currentStreak);
        response.setCalendarDays(calendarDays);
        response.setStreakDays(streakDays);

        // Set start và end date của streak
        if (!streakDays.isEmpty()) {
            response.setStreakStartDate(streakDays.get(streakDays.size() - 1)); // Ngày đầu streak (cũ nhất)
            response.setStreakEndDate(streakDays.get(0)); // Ngày cuối streak (mới nhất)
        }

        return response;
    }

    // Tính current streak - CHỈ tính những ngày học trên 15 phút
    private int calculateCurrentStreak(Long userId, List<UserActivity> activities) {
        // Lọc chỉ những ngày học trên 15 phút
        List<UserActivity> validActivities = activities.stream()
                .filter(activity -> isStudiedDay(activity.getMinutesUsed()))
                .collect(Collectors.toList());

        if (validActivities.isEmpty()) {
            return 0;
        }

        // Sắp xếp activities theo ngày giảm dần
        validActivities.sort((a1, a2) -> a2.getActivityDate().compareTo(a1.getActivityDate()));

        LocalDate currentDate = LocalDate.now();
        int streak = 0;

        // Kiểm tra nếu hôm nay có học VÀ học trên 15 phút
        boolean todayStudied = validActivities.stream()
                .anyMatch(activity -> activity.getActivityDate().equals(currentDate));

        LocalDate checkDate = todayStudied ? currentDate : currentDate.minusDays(1);

        // Kiểm tra streak liên tục
        while (true) {
            final LocalDate dateToCheck = checkDate;
            boolean studiedOnDate = validActivities.stream()
                    .anyMatch(activity -> activity.getActivityDate().equals(dateToCheck));

            if (studiedOnDate) {
                streak++;
                checkDate = checkDate.minusDays(1);
            } else {
                break;
            }
        }

        return streak;
    }

    // Lấy danh sách ngày trong current streak - CHỈ những ngày học trên 15 phút
    private List<LocalDate> getCurrentStreakDays(Long userId, List<UserActivity> activities, int currentStreak) {
        if (currentStreak == 0) {
            return new ArrayList<>();
        }

        // Lọc chỉ những ngày học trên 15 phút
        List<UserActivity> validActivities = activities.stream()
                .filter(activity -> isStudiedDay(activity.getMinutesUsed()))
                .collect(Collectors.toList());

        List<LocalDate> streakDays = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        // Sắp xếp activities theo ngày giảm dần
        validActivities.sort((a1, a2) -> a2.getActivityDate().compareTo(a1.getActivityDate()));

        // Kiểm tra nếu hôm nay có học VÀ học trên 15 phút
        boolean todayStudied = validActivities.stream()
                .anyMatch(activity -> activity.getActivityDate().equals(currentDate));

        LocalDate checkDate = todayStudied ? currentDate : currentDate.minusDays(1);

        // Thu thập các ngày trong streak
        for (int i = 0; i < currentStreak; i++) {
            streakDays.add(checkDate);
            checkDate = checkDate.minusDays(1);
        }

        return streakDays;
    }

    // Tạo dữ liệu cho calendar - CHỈ đánh dấu studied nếu học trên 15 phút
    private List<CalendarDayPOJO> generateCalendarData(Long userId, LocalDate startDate, LocalDate endDate,
                                                      List<UserActivity> activities, int currentStreak) {
        List<CalendarDayPOJO> calendarDays = new ArrayList<>();
        LocalDate current = startDate;

        // Lấy danh sách ngày trong streak hiện tại (chỉ những ngày học trên 15 phút)
        List<LocalDate> streakDays = getCurrentStreakDays(userId, activities, currentStreak);

        // Tạo map để tra cứu nhanh thông tin activity theo ngày
        Map<LocalDate, Integer> activityMap = activities.stream()
                .collect(Collectors.toMap(
                        UserActivity::getActivityDate,
                        UserActivity::getMinutesUsed
                ));

        while (!current.isAfter(endDate)) {
            Integer minutesStudied = activityMap.getOrDefault(current, 0);
            // CHỈ đánh dấu là đã học nếu số phút > 15
            boolean studied = isStudiedDay(minutesStudied);
            boolean isInCurrentStreak = streakDays.contains(current);

            CalendarDayPOJO dayDTO = new CalendarDayPOJO();
            dayDTO.setDate(current);
            dayDTO.setStudied(studied);
            dayDTO.setMinutesStudied(minutesStudied);
            dayDTO.setInCurrentStreak(isInCurrentStreak);

            calendarDays.add(dayDTO);
            current = current.plusDays(1);
        }

        return calendarDays;
    }

    // Lấy tổng số phút học trong khoảng thời gian (chỉ tính những ngày học trên 15 phút)
    public Integer getTotalValidMinutesInPeriod(Long userId, LocalDate startDate, LocalDate endDate) {
        List<UserActivity> activities = userActivityRepository.findByUserIdAndActivityDateBetween(userId, startDate, endDate);
        return activities.stream()
                .filter(activity -> isStudiedDay(activity.getMinutesUsed()))
                .mapToInt(UserActivity::getMinutesUsed)
                .sum();
    }

    // Lấy số ngày học hợp lệ (trên 15 phút) trong khoảng thời gian
    public Long getValidStudyDaysCount(Long userId, LocalDate startDate, LocalDate endDate) {
        List<UserActivity> activities = userActivityRepository.findByUserIdAndActivityDateBetween(userId, startDate, endDate);
        return activities.stream()
                .filter(activity -> isStudiedDay(activity.getMinutesUsed()))
                .count();
    }

    // Convert Entity to DTO
    private UserActivityPOJO convertToDTO(UserActivity entity) {
        UserActivityPOJO dto = new UserActivityPOJO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setActivityDate(entity.getActivityDate());
        dto.setMinutesUsed(entity.getMinutesUsed());
        return dto;
    }

    // Convert DTO to Entity
    private UserActivity convertToEntity(UserActivityPOJO dto) {
        UserActivity entity = new UserActivity();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setActivityDate(dto.getActivityDate());
        entity.setMinutesUsed(dto.getMinutesUsed());
        return entity;
    }
}
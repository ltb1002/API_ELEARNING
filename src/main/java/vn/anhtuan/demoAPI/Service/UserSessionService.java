package vn.anhtuan.demoAPI.Service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.anhtuan.demoAPI.Entity.UserActivity;
import vn.anhtuan.demoAPI.Entity.UserSession;
import vn.anhtuan.demoAPI.Repository.UserActivityRepository;
import vn.anhtuan.demoAPI.Repository.UserSessionRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserSessionService {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UserActivityRepository userActivityRepository;


    private UserSession currentSession; // giữ session đang chạy (cách đơn giản)

    private LocalDateTime pauseStart; // lưu thời điểm pause bắt đầu
    /**
     * Bắt đầu session -> chỉ lưu startTime
     */
    public UserSession startSession(Long userId) {
        currentSession = new UserSession();
        currentSession.setUserId(userId);
        currentSession.setActivityDate(LocalDate.now());
        currentSession.setStartTime(LocalDateTime.now());
        return currentSession;
    }
    // Pause session
    public void pauseSession() {
        if (currentSession != null) {
            pauseStart = LocalDateTime.now();
        }
    }


    /**
     * Dừng session -> tính phút, lưu DB (user_session + user_activity)
     */
    @Transactional
    public UserSession stopSession(Long userId) {
        if (currentSession == null || !currentSession.getUserId().equals(userId)) {
            throw new IllegalStateException("No active session found for user " + userId);
        }

        currentSession.setEndTime(LocalDateTime.now());
        int minutes = (int) Duration.between(currentSession.getStartTime(), currentSession.getEndTime()).toMinutes();
        if (minutes <= 0) {
            throw new IllegalArgumentException("Session duration must be greater than 0 minutes");
        }
        currentSession.setMinutesUsed(minutes);

        // Lưu user_session
        UserSession saved = userSessionRepository.save(currentSession);

        // Đồng bộ sang user_activity
        userActivityRepository.findByUserIdAndActivityDate(userId, currentSession.getActivityDate())
                .ifPresentOrElse(activity -> {
                    activity.setMinutesUsed(activity.getMinutesUsed() + minutes);
                    userActivityRepository.save(activity);
                }, () -> {
                    userActivityRepository.save(new UserActivity(userId, currentSession.getActivityDate(), minutes));
                });

        currentSession = null; // reset
        return saved;
    }

    /**
     * Record session khi có start + end từ client
     */
    @Transactional
    public UserSession saveSession(Long userId, LocalDateTime start, LocalDateTime end) {
        int minutes = (int) Duration.between(start, end).toMinutes();
        if (minutes <= 0) {
            throw new IllegalArgumentException("Session time must be greater than 0 minutes");
        }

        LocalDate activityDate = start.toLocalDate();

        UserSession session = new UserSession();
        session.setUserId(userId);
        session.setActivityDate(activityDate);
        session.setMinutesUsed(minutes);
        session.setStartTime(start);
        session.setEndTime(end);

        UserSession saved = userSessionRepository.save(session);

        userActivityRepository.findByUserIdAndActivityDate(userId, activityDate)
                .ifPresentOrElse(activity -> {
                    activity.setMinutesUsed(activity.getMinutesUsed() + minutes);
                    userActivityRepository.save(activity);
                }, () -> {
                    userActivityRepository.save(new UserActivity(userId, activityDate, minutes));
                });

        return saved;
    }

    /**
     * Lấy danh sách session trong ngày
     */
    public List<UserSession> getSessionsByDate(Long userId, LocalDate date) {
        return userSessionRepository.findByUserIdAndActivityDate(userId, date);
    }

    /**
     * Lấy tổng số phút học trong ngày từ bảng user_activity
     */
    public int getTotalMinutesByDate(Long userId, LocalDate date) {
        return userActivityRepository.findByUserIdAndActivityDate(userId, date)
                .map(UserActivity::getMinutesUsed)
                .orElse(0);
    }
}

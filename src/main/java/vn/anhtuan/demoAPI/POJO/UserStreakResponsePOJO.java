package vn.anhtuan.demoAPI.POJO;

import java.time.LocalDate;
import java.util.List;

public class UserStreakResponsePOJO {
    private Long userId;
    private int currentStreak;
    private LocalDate streakStartDate;
    private LocalDate streakEndDate;
    private List<LocalDate> streakDays;
    private List<CalendarDayPOJO> calendarDays;

    // Constructors
    public UserStreakResponsePOJO() {}

    public UserStreakResponsePOJO(Long userId, int currentStreak) {
        this.userId = userId;
        this.currentStreak = currentStreak;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public LocalDate getStreakStartDate() {
        return streakStartDate;
    }

    public void setStreakStartDate(LocalDate streakStartDate) {
        this.streakStartDate = streakStartDate;
    }

    public LocalDate getStreakEndDate() {
        return streakEndDate;
    }

    public void setStreakEndDate(LocalDate streakEndDate) {
        this.streakEndDate = streakEndDate;
    }

    public List<LocalDate> getStreakDays() {
        return streakDays;
    }

    public void setStreakDays(List<LocalDate> streakDays) {
        this.streakDays = streakDays;
    }

    public List<CalendarDayPOJO> getCalendarDays() {
        return calendarDays;
    }

    public void setCalendarDays(List<CalendarDayPOJO> calendarDays) {
        this.calendarDays = calendarDays;
    }
}

package vn.anhtuan.demoAPI.POJO;

import java.time.LocalDate;

public class CalendarDayPOJO {
    private LocalDate date;
    private boolean studied;
    private Integer minutesStudied;
    private boolean isInCurrentStreak;

    // Constructors
    public CalendarDayPOJO() {}

    public CalendarDayPOJO(LocalDate date, boolean studied, Integer minutesStudied) {
        this.date = date;
        this.studied = studied;
        this.minutesStudied = minutesStudied;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isStudied() {
        return studied;
    }

    public void setStudied(boolean studied) {
        this.studied = studied;
    }

    public Integer getMinutesStudied() {
        return minutesStudied;
    }

    public void setMinutesStudied(Integer minutesStudied) {
        this.minutesStudied = minutesStudied;
    }

    public boolean isInCurrentStreak() {
        return isInCurrentStreak;
    }

    public void setInCurrentStreak(boolean inCurrentStreak) {
        isInCurrentStreak = inCurrentStreak;
    }
}

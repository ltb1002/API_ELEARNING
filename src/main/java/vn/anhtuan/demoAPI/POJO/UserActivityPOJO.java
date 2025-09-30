package vn.anhtuan.demoAPI.POJO;

import java.time.LocalDate;

public class UserActivityPOJO {
    private Long id;
    private Long userId;
    private LocalDate activityDate;
    private Integer minutesUsed;

    // Constructors
    public UserActivityPOJO() {}

    public UserActivityPOJO(Long userId, LocalDate activityDate, Integer minutesUsed) {
        this.userId = userId;
        this.activityDate = activityDate;
        this.minutesUsed = minutesUsed;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(LocalDate activityDate) {
        this.activityDate = activityDate;
    }

    public Integer getMinutesUsed() {
        return minutesUsed;
    }

    public void setMinutesUsed(Integer minutesUsed) {
        this.minutesUsed = minutesUsed;
    }
}

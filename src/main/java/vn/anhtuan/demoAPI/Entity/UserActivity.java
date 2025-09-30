package vn.anhtuan.demoAPI.Entity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_activity",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "activity_date"}, name = "uq_user_date"))
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    @Column(name = "minutes_used", nullable = false)
    private Integer minutesUsed = 0;

    // Constructors
    public UserActivity() {}

    public UserActivity(Long userId, LocalDate activityDate, Integer minutesUsed) {
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

    @Override
    public String toString() {
        return "UserActivity{" +
                "id=" + id +
                ", userId=" + userId +
                ", activityDate=" + activityDate +
                ", minutesUsed=" + minutesUsed +
                '}';
    }
}

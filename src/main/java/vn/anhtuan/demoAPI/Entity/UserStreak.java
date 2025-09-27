package vn.anhtuan.demoAPI.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_streaks")
public class UserStreak {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = true)
    private int streakCount = 0;   // số ngày liên tiếp

    @Column(nullable = true)
    private int bestStreak = 0;    // kỷ lục dài nhất

    @Column(nullable = true)
    private int totalDays = 0;     // tổng số ngày học

    @Column(nullable = true)
    private LocalDate lastActiveDate;

    public UserStreak() {}


    public UserStreak(User user) {
        this.user = user;
    }

    public UserStreak(User user,LocalDate today) {
        this.user = user;
        this.streakCount = 0;
        this.bestStreak = 0;
        this.totalDays = 0;
        this.lastActiveDate = today;
    }

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public int getStreakCount() { return streakCount; }
    public void setStreakCount(int streakCount) { this.streakCount = streakCount; }
    public int getBestStreak() { return bestStreak; }
    public void setBestStreak(int bestStreak) { this.bestStreak = bestStreak; }
    public int getTotalDays() { return totalDays; }
    public void setTotalDays(int totalDays) { this.totalDays = totalDays; }
    public LocalDate getLastActiveDate() { return lastActiveDate; }
    public void setLastActiveDate(LocalDate lastActiveDate) { this.lastActiveDate = lastActiveDate; }
}
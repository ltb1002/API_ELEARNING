package vn.anhtuan.demoAPI.Service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import vn.anhtuan.demoAPI.Entity.User;
import vn.anhtuan.demoAPI.Entity.UserStreak;
import vn.anhtuan.demoAPI.Repository.UserRepository;
import vn.anhtuan.demoAPI.Repository.UserStreakRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class UserStreakService {
    // Gợi ý: cấu hình Clock ở VN timezone trong @Configuration (Clock.system(ZoneId.of("Asia/Ho_Chi_Minh")))
    private static final ZoneId VN = ZoneId.of("Asia/Ho_Chi_Minh");

    private final UserRepository userRepo;
    private final UserStreakRepository streakRepo;
    private final Clock appClock;

    public UserStreakService(UserStreakRepository streakRepo, UserRepository userRepo, Clock appClock) {
        this.streakRepo = streakRepo;
        this.userRepo = userRepo;
        this.appClock = appClock;
    }

    @Transactional
    public UserStreak getStreak(Long userId) {
        return getOrCreate(userId);
    }

    /** Ghi nhận hoạt động có ý nghĩa (15' online / hoàn thành lesson / hoàn thành quiz) */
    @Transactional
    public UserStreak touch(Long userId) {
        LocalDate today = LocalDate.now(VN);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // khóa ghi để tránh race-condition tăng trùng
        UserStreak s = streakRepo.findByUserIdForUpdate(userId)
                .orElseGet(() -> {
                    UserStreak ns = new UserStreak();
                    ns.setUser(user);
                    ns.setStreakCount(0);
                    ns.setBestStreak(0);
                    ns.setTotalDays(0);
                    ns.setLastActiveDate(today.minusDays(1));
                    return streakRepo.save(ns);
                });

        LocalDate last = s.getLastActiveDate();

        if (last != null) {
            if (last.isEqual(today)) {
                // đã ghi nhận hôm nay -> không tăng nữa
                return s;
            } else if (last.isEqual(today.minusDays(1))) {
                // liền ngày -> +1
                s.setStreakCount(s.getStreakCount() + 1);
            } else {
                // đứt quãng -> reset về 1
                s.setStreakCount(1);
            }
        } else {
            // phòng hờ nếu last null
            s.setStreakCount(1);
        }

        s.setLastActiveDate(today);
        s.setBestStreak(Math.max(s.getBestStreak(), s.getStreakCount()));
        s.setTotalDays(s.getTotalDays() + 1);

        return streakRepo.save(s);
    }


    @Transactional
    public UserStreak getOrCreate(Long userId) {
        return streakRepo.findByUser_Id(userId).orElseGet(() -> {
            LocalDate today = LocalDate.now(VN);

            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

            UserStreak s = new UserStreak();
            s.setUser(user);
            s.setStreakCount(0);
            s.setBestStreak(0);
            s.setTotalDays(0);
            s.setLastActiveDate(today.minusDays(1));
            return streakRepo.save(s);
        });
    }
}

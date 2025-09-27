package vn.anhtuan.demoAPI.Service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import vn.anhtuan.demoAPI.Entity.User;
import vn.anhtuan.demoAPI.Entity.UserStreak;
import vn.anhtuan.demoAPI.Repository.UserRepository;
import vn.anhtuan.demoAPI.Repository.UserStreakRepository;

import java.time.Clock;
import java.time.LocalDate;

@Service
public class UserStreakService {

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

    /** Gọi khi người dùng có hoạt động trong ngày (mở app, hoàn thành bài, nộp quiz, v.v.) */
    @Transactional
    public UserStreak updateStreak(Long userId) {
        UserStreak s = getOrCreate(userId);
        LocalDate today = LocalDate.now(appClock);
        LocalDate last  = s.getLastActiveDate();

        // Nếu đã ghi nhận hôm nay thì bỏ qua
        if (last != null && last.isEqual(today)) {
            return s;
        }

        // Liền ngày → +1; Đứt quãng → reset về 1
        if (last != null && last.plusDays(1).isEqual(today)) {
            s.setStreakCount(s.getStreakCount() + 1);
        } else {
            s.setStreakCount(1);
        }

        // Mỗi lần hoạt động hợp lệ đều +1 tổng ngày học
        s.setTotalDays(s.getTotalDays() + 1);

        if (s.getStreakCount() > s.getBestStreak()) {
            s.setBestStreak(s.getStreakCount());
        }

        s.setLastActiveDate(today);
        return streakRepo.save(s);
    }

    @Transactional
    public UserStreak getOrCreate(Long userId) {
        return streakRepo.findByUser_Id(userId).orElseGet(() -> {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

            LocalDate today = LocalDate.now(appClock);

            // Khởi tạo mặc định để lần update đầu tiên tăng đúng về 1 ngày
            UserStreak s = new UserStreak();
            s.setStreakCount(0);
            s.setBestStreak(0);
            s.setTotalDays(0);
            s.setLastActiveDate(today.minusDays(1));

            return streakRepo.save(s);
        });
    }
}

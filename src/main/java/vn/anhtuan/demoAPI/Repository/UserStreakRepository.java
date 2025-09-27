package vn.anhtuan.demoAPI.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.anhtuan.demoAPI.Entity.User;
import vn.anhtuan.demoAPI.Entity.UserStreak;

import java.util.Optional;

@Repository
public interface UserStreakRepository extends JpaRepository<UserStreak, Long> {
    Optional<UserStreak> findByUser_Id(Long userId);
    boolean existsByUser(User user);
}




// NHỚ UNDO nẾU KHÔNG CHẠY ĐƯỢC
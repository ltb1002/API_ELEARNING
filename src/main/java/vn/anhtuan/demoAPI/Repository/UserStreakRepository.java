package vn.anhtuan.demoAPI.Repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.anhtuan.demoAPI.Entity.User;
import vn.anhtuan.demoAPI.Entity.UserStreak;

import java.util.Optional;

@Repository
public interface UserStreakRepository extends JpaRepository<UserStreak, Long> {

    // đọc bình thường
    Optional<UserStreak> findByUser_Id(Long userId);

    // đọc để update (khóa ghi)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from UserStreak s where s.user.id = :userId")
    Optional<UserStreak> findByUserIdForUpdate(@Param("userId") Long userId);
}




// NHỚ UNDO nẾU KHÔNG CHẠY ĐƯỢC
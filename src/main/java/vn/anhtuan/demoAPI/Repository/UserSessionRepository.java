package vn.anhtuan.demoAPI.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.anhtuan.demoAPI.Entity.UserSession;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    List<UserSession> findByUserIdAndActivityDate(Long userId, LocalDate activityDate);
}

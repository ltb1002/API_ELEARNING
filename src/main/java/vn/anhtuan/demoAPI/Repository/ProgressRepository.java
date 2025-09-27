package vn.anhtuan.demoAPI.Repository;

import vn.anhtuan.demoAPI.Entity.Progress;
import vn.anhtuan.demoAPI.Entity.User;
import vn.anhtuan.demoAPI.Entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {

    // Tìm progress theo user và subject
    Optional<Progress> findByUserAndSubject(User user, Subject subject);

    // Tìm progress theo user, subject và grade
    Optional<Progress> findByUserAndSubjectAndGrade(User user, Subject subject, Integer grade);

    // Lấy tất cả progress của một user
    List<Progress> findByUser(User user);

    // Lấy progress của user theo subjectId
    @Query("SELECT p FROM Progress p WHERE p.user.id = :userId AND p.subject.id = :subjectId")
    Optional<Progress> findByUserIdAndSubjectId(@Param("userId") Long userId, @Param("subjectId") Integer subjectId);

    // Lấy tất cả progress của user theo grade
    List<Progress> findByUserAndGrade(User user, Integer grade);

    // Kiểm tra xem progress đã tồn tại chưa
    boolean existsByUserAndSubject(User user, Subject subject);

    // Tìm tất cả progress records theo subject
    List<Progress> findBySubject(Subject subject);

    // Tìm tất cả progress records theo subjectId
    @Query("SELECT p FROM Progress p WHERE p.subject.id = :subjectId")
    List<Progress> findBySubjectId(@Param("subjectId") Integer subjectId);
}
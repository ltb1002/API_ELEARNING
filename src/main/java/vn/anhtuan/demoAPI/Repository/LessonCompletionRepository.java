package vn.anhtuan.demoAPI.Repository;

import vn.anhtuan.demoAPI.Entity.LessonCompletion;
import vn.anhtuan.demoAPI.Entity.User;
import vn.anhtuan.demoAPI.Entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface LessonCompletionRepository extends JpaRepository<LessonCompletion, Long> {

    // Tìm completion theo user và lesson
    Optional<LessonCompletion> findByUserAndLesson(User user, Lesson lesson);

    // Kiểm tra xem user đã hoàn thành lesson chưa
    boolean existsByUserAndLesson(User user, Lesson lesson);

    // Đếm số lesson đã hoàn thành của user trong một subject
    @Query("SELECT COUNT(lc) FROM LessonCompletion lc WHERE lc.user.id = :userId AND lc.lesson.chapter.subject.id = :subjectId")
    long countCompletedLessonsByUserAndSubject(@Param("userId") Long userId, @Param("subjectId") Integer subjectId);

    // Lấy tất cả completions của user trong một subject
    @Query("SELECT lc FROM LessonCompletion lc WHERE lc.user.id = :userId AND lc.lesson.chapter.subject.id = :subjectId")
    List<LessonCompletion> findByUserAndSubject(@Param("userId") Long userId, @Param("subjectId") Integer subjectId);

    // Đếm tổng số completions của user
    long countByUser(User user);
}
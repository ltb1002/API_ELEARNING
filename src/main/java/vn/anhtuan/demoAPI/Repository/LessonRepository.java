package vn.anhtuan.demoAPI.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.anhtuan.demoAPI.Entity.Lesson;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByChapterId(Long chapterId);

    // Phiên bản tối ưu cho tiếng Việt - thêm tham số grade
    @Query("SELECT l FROM Lesson l JOIN l.chapter c JOIN c.subject s " +
            "WHERE (:keyword IS NULL OR LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:subjectId IS NULL OR s.id = :subjectId) " +
            "AND (:grade IS NULL OR s.grade = :grade)")
    List<Lesson> searchLessons(@Param("keyword") String keyword,
                               @Param("subjectId") Long subjectId,
                               @Param("grade") Integer grade);

    // Thêm phương thức tìm kiếm không phân biệt dấu (optional - nếu cần)
    @Query(value = "SELECT l.* FROM lessons l " +
            "JOIN chapters c ON l.chapter_id = c.id " +
            "JOIN subjects s ON c.subject_id = s.id " +
            "WHERE (:keyword IS NULL OR l.title COLLATE utf8mb4_unicode_ci LIKE CONCAT('%', :keyword, '%')) " +
            "AND (:subjectId IS NULL OR s.id = :subjectId) " +
            "AND (:grade IS NULL OR s.grade = :grade)",
            nativeQuery = true)
    List<Lesson> searchLessonsVietnamese(@Param("keyword") String keyword,
                                         @Param("subjectId") Long subjectId,
                                         @Param("grade") Integer grade);
}
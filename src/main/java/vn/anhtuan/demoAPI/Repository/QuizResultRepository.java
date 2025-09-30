package vn.anhtuan.demoAPI.Repository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.anhtuan.demoAPI.Entity.QuizResult;
import vn.anhtuan.demoAPI.POJO.DailyAccuracyViewPOJO;

import java.util.List;
import java.util.Map;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Integer> {

    List<QuizResult> findByUserId(Long userId);
    List<QuizResult> findByQuizId(Integer quizId);

//    List<QuizResult> findByUser_Id(Long userId);      // ✅
//    List<QuizResult> findByQuiz_Id(Integer quizId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.user.id = :userId AND qr.quiz.grade.id = :gradeId")
    List<QuizResult> findByUserIdAndGradeId(@Param("userId") Long userId, @Param("gradeId") Integer gradeId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.user.id = :userId AND qr.quiz.subject.id = :subjectId")
    List<QuizResult> findByUserIdAndSubjectId(@Param("userId") Long userId, @Param("subjectId") Integer subjectId);

    @Query("SELECT AVG(qr.score) FROM QuizResult qr WHERE qr.user.id = :userId AND qr.quiz.subject.id = :subjectId")
    Double findAverageScoreByUserIdAndSubjectId(@Param("userId") Long userId, @Param("subjectId") Integer subjectId);

    @Query("SELECT COALESCE(MAX(qr.attemptNo), 0) FROM QuizResult qr WHERE qr.user.id = :userId AND qr.quiz.id = :quizId")
    Integer findMaxAttemptNoByUserAndQuiz(@Param("userId") Long userId, @Param("quizId") Integer quizId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.user.id = :userId AND qr.quiz.id = :quizId ORDER BY qr.attemptNo DESC")
    List<QuizResult> findByUserIdAndQuizId(@Param("userId") Long userId, @Param("quizId") Integer quizId);

    // ====== Aggregate theo NGÀY từ quiz_results (phục vụ biểu đồ daily nếu cần) ======
    @Query(value = """
    SELECT DATE(qr.completed_at) AS day,
           SUM(qr.correct_answers) AS correct_sum,
           SUM(qr.total_questions) AS total_sum
    FROM quiz_results qr
    JOIN quizzes q ON q.id = qr.quiz_id
    WHERE qr.user_id = :userId  
      AND qr.status = 'COMPLETED'
      AND qr.completed_at >= :fromDate
      AND (:gradeId IS NULL OR q.grade_id = :gradeId)
      AND (:subjectId IS NULL OR q.subject_id = :subjectId)
      AND (:quizTypeId IS NULL OR q.quiz_type_id = :quizTypeId)
      AND (:chapterId IS NULL OR q.chapter_id = :chapterId)
    GROUP BY DATE(qr.completed_at)
    ORDER BY day
""", nativeQuery = true)
    List<Object[]> aggregateDailyAccuracyNative(
            @Param("userId") Long userId,
            @Param("fromDate") java.time.LocalDateTime fromDate,
            @Param("gradeId") Integer gradeId,
            @Param("subjectId") Integer subjectId,
            @Param("quizTypeId") Integer quizTypeId,
            @Param("chapterId") Long chapterId
    );

    // ====== Tổng kỳ (tính trực tiếp từ quiz_results) để cập nhật progress ======
    @Query(value = """
        SELECT COALESCE(SUM(qr.correct_answers), 0) AS correct_sum,
               COALESCE(SUM(qr.total_questions), 0) AS total_sum
        FROM quiz_results qr
        JOIN quizzes q ON q.id = qr.quiz_id
        WHERE qr.user_id = :userId
          AND qr.status = 'COMPLETED'
          AND (:gradeId IS NULL OR q.grade_id = :gradeId)
          AND (:subjectId IS NULL OR q.subject_id = :subjectId)
          AND (:quizTypeId IS NULL OR q.quiz_type_id = :quizTypeId)
        """, nativeQuery = true)
    Object[] sumCorrectAndTotalByUserAndFilters(@Param("userId") Long userId,
                                                @Param("gradeId") Integer gradeId,
                                                @Param("subjectId") Integer subjectId,
                                                @Param("quizTypeId") Integer quizTypeId);

    @Transactional(readOnly = true)
    @Query(value = """
        SELECT correct_sum, total_sum, progress_percent, updated_at
        FROM quiz_progress
        WHERE user_id = :userId
          AND (:gradeId    IS NULL OR grade_id    = :gradeId)
          AND (:subjectId  IS NULL OR subject_id  = :subjectId)
          AND (:quizTypeId IS NULL OR quiz_type_id = :quizTypeId)
        """, nativeQuery = true)
    List<Object[]> findAccuracyRows(@Param("userId") Long userId,
                                    @Param("gradeId") Integer gradeId,
                                    @Param("subjectId") Integer subjectId,
                                    @Param("quizTypeId") Integer quizTypeId);


    @Modifying
    @Transactional
    @Query(value = """
  INSERT INTO quiz_progress (user_id, subject_id, grade_id, quiz_type_id,
                             correct_sum, total_sum, progress_percent, updated_at)
  SELECT
      qr.user_id, q.subject_id, q.grade_id, q.quiz_type_id,
      COALESCE(SUM(qr.correct_answers), 0),
      COALESCE(SUM(qr.total_questions), 0),
      CASE WHEN COALESCE(SUM(qr.total_questions),0)=0
           THEN 0
           ELSE (COALESCE(SUM(qr.correct_answers),0) * 100.0 / COALESCE(SUM(qr.total_questions),0))
      END,
      NOW()
  FROM quiz_results qr
  JOIN quizzes q ON q.id = qr.quiz_id
  WHERE qr.user_id = :userId AND qr.status = 'COMPLETED'
  GROUP BY qr.user_id, q.subject_id, q.grade_id, q.quiz_type_id
  ON DUPLICATE KEY UPDATE
    correct_sum = VALUES(correct_sum),
    total_sum = VALUES(total_sum),
    progress_percent = VALUES(progress_percent),
    updated_at = NOW()
""", nativeQuery = true)
    void recomputeProgressForUser(@Param("userId") Long userId);

    // (1) % theo NGÀY của 1 user trong khoảng ngày
    @Query(value = """
        SELECT 
          DATE(completed_at)    AS quizDate,
          ROUND(SUM(correct_answers) * 100.0 / NULLIF(SUM(total_questions), 0), 2) AS dailyPercentage
        FROM quiz_results
        WHERE status = 'COMPLETED'
          AND completed_at IS NOT NULL
          AND user_id = :userId
          AND DATE(completed_at) BETWEEN :fromDate AND :toDate
        GROUP BY DATE(completed_at)
        ORDER BY quizDate
    """, nativeQuery = true)
    List<DailyAccuracyViewPOJO> findDailyAccuracyByUserAndRange(
            @Param("userId") Long userId,
            @Param("fromDate") String fromDate,  // "YYYY-MM-DD"
            @Param("toDate") String toDate       // "YYYY-MM-DD"
    );

    // (2) Trung bình cộng theo NGÀY của user (bình quân % các ngày)
    @Query(value = """
        SELECT ROUND(AVG(pct), 2) AS average_daily_percentage
        FROM (
          SELECT 
            SUM(correct_answers) * 100.0 / NULLIF(SUM(total_questions), 0) AS pct
          FROM quiz_results
          WHERE status = 'COMPLETED' 
            AND completed_at IS NOT NULL
            AND user_id = :userId
          GROUP BY DATE(completed_at)
        ) t
    """, nativeQuery = true)
    Double findAverageDailyAccuracyByUser(@Param("userId") Long userId);
}




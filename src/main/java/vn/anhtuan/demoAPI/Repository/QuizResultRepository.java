package vn.anhtuan.demoAPI.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.anhtuan.demoAPI.Entity.QuizResult;
import vn.anhtuan.demoAPI.Entity.QuizStatus;

import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Integer> {
    List<QuizResult> findByUserId(Long userId);
    List<QuizResult> findByQuizId(Integer quizId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.user.id = :userId AND qr.quiz.grade.id = :gradeId")
    List<QuizResult> findByUserIdAndGradeId(@Param("userId") Long userId, @Param("gradeId") Integer gradeId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.user.id = :userId AND qr.quiz.subject.id = :subjectId")
    List<QuizResult> findByUserIdAndSubjectId(@Param("userId") Long userId, @Param("subjectId") Integer subjectId);

    @Query("SELECT AVG(qr.score) FROM QuizResult qr WHERE qr.user.id = :userId AND qr.quiz.subject.id = :subjectId")
    Double findAverageScoreByUserIdAndSubjectId(@Param("userId") Long userId, @Param("subjectId") Integer subjectId);

    // Thêm phương thức mới
    @Query("SELECT COALESCE(MAX(qr.attemptNo), 0) FROM QuizResult qr WHERE qr.user.id = :userId AND qr.quiz.id = :quizId")
    Integer findMaxAttemptNoByUserAndQuiz(@Param("userId") Long userId, @Param("quizId") Integer quizId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.user.id = :userId AND qr.quiz.id = :quizId ORDER BY qr.attemptNo DESC")
    List<QuizResult> findByUserIdAndQuizId(@Param("userId") Long userId, @Param("quizId") Integer quizId);

//đếm completed DISTINCT trực tiếp để DB xử lý DISTINCT và filter hộ bạn; service chỉ ghép số liệu.
    @Query("""
        SELECT COUNT(DISTINCT qr.quiz.id)
        FROM QuizResult qr
        WHERE qr.user.id = :userId
          AND qr.status = :completedStatus
          AND (:gradeId IS NULL OR qr.quiz.grade.id = :gradeId)
          AND (:subjectId IS NULL OR qr.quiz.subject.id = :subjectId)
          AND (:quizTypeId IS NULL OR qr.quiz.quizType.id = :quizTypeId)
          AND (:chapterId IS NULL OR qr.quiz.chapter.id = :chapterId)
    """)
    long countCompletedDistinctQuizForUser(@Param("userId") Long userId,
                                           @Param("completedStatus") QuizStatus completedStatus,
                                           @Param("gradeId") Integer gradeId,
                                           @Param("subjectId") Integer subjectId,
                                           @Param("quizTypeId") Integer quizTypeId,
                                           @Param("chapterId") Long chapterId);



//Lấy danh sách gradeId mà user có kết quả, sắp xếp theo tần suất giảm dần :
    // QuizResultRepository.java
    @Query("""
  SELECT qr.quiz.grade.id
  FROM QuizResult qr
  WHERE qr.user.id = :userId
    AND qr.quiz.grade.id IS NOT NULL
  GROUP BY qr.quiz.grade.id
  ORDER BY COUNT(qr.id) DESC
""")
    java.util.List<Integer> findUserTopGradeIds(@Param("userId") Long userId);


    // import @Query, @Param như các method khác bạn đang dùng
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
    java.util.List<Object[]> aggregateDailyAccuracyNative(
            @Param("userId") Long userId,
            @Param("fromDate") java.time.LocalDateTime fromDate,
            @Param("gradeId") Integer gradeId,
            @Param("subjectId") Integer subjectId,
            @Param("quizTypeId") Integer quizTypeId,
            @Param("chapterId") Long chapterId
    );
}

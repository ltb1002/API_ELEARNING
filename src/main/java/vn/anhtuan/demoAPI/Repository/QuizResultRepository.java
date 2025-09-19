package vn.anhtuan.demoAPI.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.anhtuan.demoAPI.Entity.QuizResult;

import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Integer> {
    List<QuizResult> findByUserId(Long userId);
    List<QuizResult> findByQuizId(Integer quizId);
    QuizResult findByUserIdAndQuizId(Long userId, Integer quizId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.user.id = :userId AND qr.quiz.grade.id = :gradeId")
    List<QuizResult> findByUserIdAndGradeId(@Param("userId") Long userId, @Param("gradeId") Integer gradeId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.user.id = :userId AND qr.quiz.subject.id = :subjectId")
    List<QuizResult> findByUserIdAndSubjectId(@Param("userId") Long userId, @Param("subjectId") Integer subjectId);

    @Query("SELECT AVG(qr.score) FROM QuizResult qr WHERE qr.user.id = :userId AND qr.quiz.subject.id = :subjectId")
    Double findAverageScoreByUserIdAndSubjectId(@Param("userId") Long userId, @Param("subjectId") Integer subjectId);
}

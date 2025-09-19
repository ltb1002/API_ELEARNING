package vn.anhtuan.demoAPI.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.anhtuan.demoAPI.Entity.Question;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findByQuizId(Integer quizId);

    @Query("SELECT q FROM Question q WHERE q.quiz.id = :quizId ORDER BY q.id")
    List<Question> findQuestionsByQuizIdOrdered(@Param("quizId") Integer quizId);
}

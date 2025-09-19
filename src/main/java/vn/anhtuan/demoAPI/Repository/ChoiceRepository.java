package vn.anhtuan.demoAPI.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.anhtuan.demoAPI.Entity.Choice;

import java.util.List;

@Repository
public interface ChoiceRepository extends JpaRepository<Choice, Integer> {
    List<Choice> findByQuestionId(Integer questionId);

    @Query("SELECT c FROM Choice c WHERE c.question.id IN :questionIds")
    List<Choice> findByQuestionIds(@Param("questionIds") List<Integer> questionIds);

    @Query("SELECT c FROM Choice c WHERE c.question.id = :questionId AND c.isCorrect = true")
    List<Choice> findCorrectChoicesByQuestionId(@Param("questionId") Integer questionId);

    @Query("SELECT c FROM Choice c WHERE c.question.id IN :questionIds AND c.isCorrect = true")
    List<Choice> findCorrectChoicesByQuestionIds(@Param("questionIds") List<Integer> questionIds);
}

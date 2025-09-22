package vn.anhtuan.demoAPI.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.anhtuan.demoAPI.Entity.Choice;

import java.util.List;

@Repository
public interface ChoiceRepository extends JpaRepository<Choice, Long> { // Đổi Integer -> Long
    List<Choice> findByQuestionId(Long questionId); // Đổi Integer -> Long

    @Query("SELECT c FROM Choice c WHERE c.question.id IN :questionIds")
    List<Choice> findByQuestionIds(@Param("questionIds") List<Long> questionIds); // Đổi Integer -> Long

    @Query("SELECT c FROM Choice c WHERE c.question.id = :questionId AND c.isCorrect = true")
    List<Choice> findCorrectChoicesByQuestionId(@Param("questionId") Long questionId); // Đổi Integer -> Long

    @Query("SELECT c FROM Choice c WHERE c.question.id IN :questionIds AND c.isCorrect = true")
    List<Choice> findCorrectChoicesByQuestionIds(@Param("questionIds") List<Long> questionIds); // Đổi Integer -> Long
}
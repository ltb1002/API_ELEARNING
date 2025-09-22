package vn.anhtuan.demoAPI.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.anhtuan.demoAPI.Entity.QuestionContent;

import java.util.List;

@Repository
public interface QuestionContentRepository extends JpaRepository<QuestionContent, Long> {
    List<QuestionContent> findByQuestionId(Long questionId);

    @Query("SELECT qc FROM QuestionContent qc WHERE qc.question.id IN :questionIds")
    List<QuestionContent> findByQuestionIds(@Param("questionIds") List<Long> questionIds);

    @Query("SELECT qc FROM QuestionContent qc WHERE qc.question.id = :questionId ORDER BY qc.id")
    List<QuestionContent> findByQuestionIdOrdered(@Param("questionId") Long questionId);

    @Query("SELECT qc FROM QuestionContent qc WHERE qc.contentType = :contentType AND qc.question.id = :questionId")
    List<QuestionContent> findByContentTypeAndQuestionId(@Param("contentType") String contentType,
                                                         @Param("questionId") Long questionId);
}
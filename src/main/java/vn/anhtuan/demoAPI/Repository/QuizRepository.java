package vn.anhtuan.demoAPI.Repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.anhtuan.demoAPI.Entity.Quiz;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {

    @EntityGraph(attributePaths = {"chapter"}) // Ensure chapter is loaded
    Quiz findByCode(String code);

    @EntityGraph(attributePaths = {"chapter"})
    @Query("SELECT q FROM Quiz q WHERE q.grade.id = :gradeId AND q.subject.id = :subjectId")
    List<Quiz> findByGradeIdAndSubjectId(@Param("gradeId") Integer gradeId,
                                         @Param("subjectId") Integer subjectId);

    @Query("SELECT q FROM Quiz q WHERE q.grade.id = :gradeId AND q.subject.id = :subjectId AND q.quizType.id = :quizTypeId")
    List<Quiz> findByGradeIdAndSubjectIdAndQuizTypeId(@Param("gradeId") Integer gradeId,
                                                      @Param("subjectId") Integer subjectId,
                                                      @Param("quizTypeId") Integer quizTypeId);


}

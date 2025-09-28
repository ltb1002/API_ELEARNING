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


    //đếm total bằng JPQL COUNT

    @Query("""
        SELECT COUNT(q)
        FROM Quiz q
        WHERE (:gradeId IS NULL OR q.grade.id = :gradeId)
          AND (:subjectId IS NULL OR q.subject.id = :subjectId)
          AND (:quizTypeId IS NULL OR q.quizType.id = :quizTypeId)
          AND (:chapterId IS NULL OR q.chapter.id = :chapterId)
    """)
    long countByFilters(@Param("gradeId") Integer gradeId,
                        @Param("subjectId") Integer subjectId,
                        @Param("quizTypeId") Integer quizTypeId,
                        @Param("chapterId") Long chapterId);


    // QuizRepository.java
    @Query("""
  SELECT q.grade.id
  FROM Quiz q
  WHERE q.grade.id IS NOT NULL
  GROUP BY q.grade.id
  ORDER BY COUNT(q.id) DESC
""")
    java.util.List<Integer> findTopGradeIdsByQuizCount();


}

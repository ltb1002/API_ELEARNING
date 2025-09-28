package vn.anhtuan.demoAPI.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.demoAPI.Entity.QuizStatus;
import vn.anhtuan.demoAPI.POJO.QuizProgressPOJO;
import vn.anhtuan.demoAPI.Repository.QuizRepository;
import vn.anhtuan.demoAPI.Repository.QuizResultRepository;

@Service
public class QuizProgressService {

    private final QuizRepository quizRepository;
    private final QuizResultRepository quizResultRepository;

    public QuizProgressService(QuizRepository quizRepository,
                               QuizResultRepository quizResultRepository) {
        this.quizRepository = quizRepository;
        this.quizResultRepository = quizResultRepository;
    }

    /**
     * Tính tiến độ bằng COUNT trực tiếp ở DB (khuyên dùng).
     * Auto-fill gradeId nếu client không truyền, dựa theo dữ liệu quizzes/quiz_results.
     */
    @Transactional(readOnly = true)
    public QuizProgressPOJO getProgress(Long userId,
                                        Integer gradeId,
                                        Integer subjectId,
                                        Integer quizTypeId,
                                        Long chapterId) {

        // --- AUTO-FILL gradeId nếu thiếu ---
        if (gradeId == null) {
            // 1) Ưu tiên khối lớp mà user tương tác nhiều nhất
            var userTopGrades = quizResultRepository.findUserTopGradeIds(userId);
            if (userTopGrades != null && !userTopGrades.isEmpty()) {
                gradeId = userTopGrades.get(0);
            } else {
                // 2) Fallback: khối có nhiều quiz nhất trong toàn bộ quizzes
                var topGradesByQuiz = quizRepository.findTopGradeIdsByQuizCount();
                if (topGradesByQuiz != null && !topGradesByQuiz.isEmpty()) {
                    gradeId = topGradesByQuiz.get(0);
                }
                // 3) Nếu vẫn null -> để null (tính trên toàn bộ)
            }
        }

        final long total = quizRepository.countByFilters(gradeId, subjectId, quizTypeId, chapterId);
        if (total == 0) return new QuizProgressPOJO(0, 0);

        final long completed = quizResultRepository.countCompletedDistinctQuizForUser(
                userId, QuizStatus.COMPLETED, gradeId, subjectId, quizTypeId, chapterId
        );

        return new QuizProgressPOJO(total, completed);
    }
}

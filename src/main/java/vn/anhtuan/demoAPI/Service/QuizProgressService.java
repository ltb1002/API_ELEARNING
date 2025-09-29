package vn.anhtuan.demoAPI.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
     * Trả tiến độ theo % số câu đúng/tổng số câu (accuracy),
     * KHÔNG còn đếm "số quiz đã hoàn thành".
     * Giữ nguyên chữ ký method để không phá cấu trúc cũ.
     */
    @Transactional(readOnly = true)
    public QuizProgressPOJO getProgress(Long userId,
                                        Integer gradeId,
                                        Integer subjectId,
                                        Integer quizTypeId,
                                        Long chapterId) {

        // Gộp tổng đúng/tổng câu từ quiz_results theo các bộ lọc
        // (hàm sumCorrectAndTotalByUserAndFilters hiện chưa có chapter, nên ta chỉ filter theo grade/subject/quizType)
        Object[] sums = quizResultRepository.sumCorrectAndTotalByUserAndFilters(
                userId,
                gradeId,
                subjectId,
                quizTypeId
        );

        long correctSum = 0L;
        long totalSum = 0L;
        if (sums != null && sums.length >= 2) {
            if (sums[0] instanceof Number) correctSum = ((Number) sums[0]).longValue();
            if (sums[1] instanceof Number) totalSum   = ((Number) sums[1]).longValue();
        }

        double percent = (totalSum == 0) ? 0.0 : (correctSum * 100.0 / totalSum);
        percent = Math.round(percent * 100.0) / 100.0;

        // updatedAt: nếu muốn chính xác, có thể đọc từ bảng quiz_progress; ở đây để null cho đơn giản
        return new QuizProgressPOJO(correctSum, totalSum, percent, null);
    }
}

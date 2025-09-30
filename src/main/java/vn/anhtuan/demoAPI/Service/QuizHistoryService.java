package vn.anhtuan.demoAPI.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.demoAPI.POJO.QuizDailyStatPOJO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Wrapper mỏng: chuyển "days" -> fromDate, rồi ủy quyền sang QuizResultService.getDailyAccuracy(...)
 * KHÔNG tự truy vấn DB nữa (tránh trùng với QuizResultService / Repository).
 */
@Service
public class QuizHistoryService {

    private final QuizResultService quizResultService;

    public QuizHistoryService(QuizResultService quizResultService) {
        this.quizResultService = quizResultService;
    }

    @Transactional(readOnly = true)
    public List<QuizDailyStatPOJO> getDailyAccuracy(Long userId,
                                                    Integer days,
                                                    Integer gradeId,
                                                    Integer subjectId,
                                                    Integer quizTypeId,
                                                    Long chapterId) {
        if (days == null || days <= 0) days = 7;

        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(days - 1);
        LocalDateTime fromDate = start.atStartOfDay();

        // Gọi service chuẩn (nguồn dữ liệu gốc)
        // Trả về list map có các khóa: day, correctSum, totalSum, percentAccuracy
        var rows = quizResultService.getDailyAccuracy(userId, fromDate, gradeId, subjectId, quizTypeId, chapterId);

        // Map -> POJO + densify cho đủ ngày (giữ hành vi cũ)
        Map<LocalDate, long[]> map = new HashMap<>();
        for (Map<String, Object> m : rows) {
            LocalDate d = (m.get("day") instanceof java.time.LocalDate)
                    ? (LocalDate) m.get("day")
                    : LocalDate.parse(m.get("day").toString());
            long correct = ((Number) m.get("correctSum")).longValue();
            long total   = ((Number) m.get("totalSum")).longValue();
            map.put(d, new long[]{correct, total});
        }

        List<QuizDailyStatPOJO> out = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate d = start.plusDays(i);
            long[] v = map.getOrDefault(d, new long[]{0, 0});
            out.add(new QuizDailyStatPOJO(d, v[0], v[1]));
        }
        return out;
    }
}

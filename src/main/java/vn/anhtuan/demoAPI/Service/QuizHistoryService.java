package vn.anhtuan.demoAPI.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.demoAPI.POJO.QuizDailyStatPOJO;
import vn.anhtuan.demoAPI.Repository.QuizResultRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuizHistoryService {
    private final QuizResultRepository quizResultRepository;

    public QuizHistoryService(QuizResultRepository quizResultRepository) {
        this.quizResultRepository = quizResultRepository;
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

        var rows = quizResultRepository.aggregateDailyAccuracyNative(
                userId, fromDate, gradeId, subjectId, quizTypeId, chapterId
        );

        Map<LocalDate, long[]> map = new HashMap<>();
        for (Object[] r : rows) {
            LocalDate d = (r[0] instanceof java.sql.Date)
                    ? ((java.sql.Date) r[0]).toLocalDate()
                    : LocalDate.parse(r[0].toString());
            long correct = ((Number) r[1]).longValue();
            long total   = ((Number) r[2]).longValue();
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

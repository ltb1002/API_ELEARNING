package vn.anhtuan.demoAPI.Service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.anhtuan.demoAPI.Entity.*;
import vn.anhtuan.demoAPI.POJO.DailyAccuracyViewPOJO;
import vn.anhtuan.demoAPI.POJO.QuizProgressPOJO;
import vn.anhtuan.demoAPI.Repository.ChoiceRepository;
import vn.anhtuan.demoAPI.Repository.QuestionRepository;
import vn.anhtuan.demoAPI.Repository.QuizResultRepository;
import vn.anhtuan.demoAPI.Repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;                    // ✅ đúng package
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizResultService {

    @Autowired private QuizResultRepository quizResultRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private QuizService quizService;
    @Autowired private ChoiceRepository choiceRepository;
    @Autowired private QuestionRepository questionRepository;

    public QuizResult getQuizResultById(Integer id) {
        return quizResultRepository.findById(id).orElse(null);
    }

    public List<QuizResult> getUserQuizResults(Long userId) {
        return quizResultRepository.findByUserId(userId);
    }

    public List<QuizResult> getQuizResultsForQuiz(Integer quizId) {
        return quizResultRepository.findByQuizId(quizId);
    }

    public List<QuizResult> getUserQuizResultsForQuiz(Long userId, Integer quizId) {
        return quizResultRepository.findByUserIdAndQuizId(userId, quizId);
    }

    public List<QuizResult> getUserQuizResultsByGrade(Long userId, Integer gradeId) {
        return quizResultRepository.findByUserIdAndGradeId(userId, gradeId);
    }

    public List<QuizResult> getUserQuizResultsBySubject(Long userId, Integer subjectId) {
        return quizResultRepository.findByUserIdAndSubjectId(userId, subjectId);
    }

    public Double getUserAverageScoreBySubject(Long userId, Integer subjectId) {
        return quizResultRepository.findAverageScoreByUserIdAndSubjectId(userId, subjectId);
    }

    @Transactional
    public QuizResult submitQuiz(Long userId, Integer quizId,
                                 Map<Long, List<Long>> userAnswers,
                                 Integer durationSeconds) {

        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
        if (userAnswers == null) throw new IllegalArgumentException("User answers cannot be null");
        if (durationSeconds == null || durationSeconds < 0)
            throw new IllegalArgumentException("Duration must be a positive value");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Quiz quiz = quizService.getQuizById(quizId);
        if (quiz == null) throw new IllegalArgumentException("Quiz not found with id: " + quizId);

        List<Question> questions = quizService.getQuizQuestions(quizId.longValue());
        if (questions.isEmpty()) throw new IllegalArgumentException("Quiz has no questions");

        int totalQuestions = questions.size();
        int correctAnswers = 0;

        // đáp án đúng
        List<Long> qIds = questions.stream().map(Question::getId).toList();
        Map<Long, Set<Long>> correctChoiceIdsMap = quizService.getCorrectChoiceIdsForQuestions(qIds);

        for (Question q : questions) {
            List<Long> picked = userAnswers.get(q.getId());
            Set<Long> correct = correctChoiceIdsMap.getOrDefault(q.getId(), Collections.emptySet());
            Set<Long> pickedSet = (picked != null) ? new HashSet<>(picked) : Collections.emptySet();

            if (correct.isEmpty()) {
                if (pickedSet.isEmpty()) correctAnswers++;
            } else if (correct.equals(pickedSet)) {
                correctAnswers++;
            }
        }

        BigDecimal score = (totalQuestions > 0)
                ? new BigDecimal(correctAnswers)
                .divide(new BigDecimal(totalQuestions), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("10"))
                .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        int attemptNo = quizResultRepository.findByUserIdAndQuizId(userId, quizId).size() + 1;

        QuizResult quizResult = new QuizResult(
                user, quiz, attemptNo, score, correctAnswers, totalQuestions,
                durationSeconds, QuizStatus.COMPLETED
        );

        QuizResult saved = quizResultRepository.save(quizResult);
        quizResultRepository.flush();
        return saved;
    }

    public Map<String, Object> getQuizStatistics(Integer quizId) {
        List<QuizResult> results = quizResultRepository.findByQuizId(quizId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAttempts", results.size());

        if (!results.isEmpty()) {
            double averageScore = results.stream()
                    .mapToDouble(r -> r.getScore().doubleValue())
                    .average().orElse(0.0);
            stats.put("averageScore", averageScore);

            long completedCount = results.stream()
                    .filter(r -> r.getStatus() == QuizStatus.COMPLETED)
                    .count();
            stats.put("completionRate", (double) completedCount / results.size() * 100);

            Map<Integer, Long> attemptsDistribution = results.stream()
                    .collect(Collectors.groupingBy(QuizResult::getAttemptNo, Collectors.counting()));
            stats.put("attemptsDistribution", attemptsDistribution);
        } else {
            stats.put("averageScore", 0);
            stats.put("completionRate", 0);
            stats.put("attemptsDistribution", new HashMap<>());
        }
        return stats;
    }

    public Map<String, Object> getUserStatistics(Long userId) {
        List<QuizResult> results = quizResultRepository.findByUserId(userId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalQuizzesTaken", results.size());

        if (!results.isEmpty()) {
            double averageScore = results.stream()
                    .mapToDouble(r -> r.getScore().doubleValue())
                    .average().orElse(0.0) * 10;
            stats.put("averageScore", averageScore);

            long completedCount = results.stream()
                    .filter(r -> r.getStatus() == QuizStatus.COMPLETED)
                    .count();
            stats.put("completionRate", (double) completedCount / results.size() * 100);

            Map<String, Long> quizzesBySubject = new HashMap<>();
            for (QuizResult r : results) {
                String subjectName = r.getQuiz().getSubject().getName();
                quizzesBySubject.put(subjectName, quizzesBySubject.getOrDefault(subjectName, 0L) + 1);
            }
            stats.put("quizzesBySubject", quizzesBySubject);

            double averageDuration = results.stream()
                    .filter(r -> r.getDurationSeconds() != null)
                    .mapToInt(QuizResult::getDurationSeconds)
                    .average().orElse(0.0);
            stats.put("averageDurationSeconds", averageDuration);
        } else {
            stats.put("averageScore", 0);
            stats.put("completionRate", 0);
            stats.put("quizzesBySubject", new HashMap<>());
            stats.put("averageDurationSeconds", 0);
        }
        return stats;
    }

    public QuizResult getBestQuizResultForUser(Long userId, Integer quizId) {
        List<QuizResult> results = getUserQuizResultsForQuiz(userId, quizId);
        if (results.isEmpty()) return null;
        return results.stream()
                .max(Comparator.comparing(QuizResult::getScore)
                        .thenComparing(QuizResult::getCorrectAnswers))
                .orElse(null);
    }

    /** OVERALL accuracy: tính trực tiếp từ quiz_results */
    public Optional<QuizProgressPOJO> getAccuracyFromProgress(Long userId,
                                                              Integer gradeId,
                                                              Integer subjectId,
                                                              Integer quizTypeId) {
        Object[] row = quizResultRepository.aggregateOverallByUserAndFilters(
                userId, gradeId, subjectId, quizTypeId).toArray();

        if (row == null) {
            return Optional.of(new QuizProgressPOJO(0L, 0L, 0.0, null));
        }

        long correctSum = ((Number) row[0]).longValue();
        long totalSum   = ((Number) row[1]).longValue();

        LocalDateTime updatedAt = null;
        Object tsObj = row[2];
        if (tsObj instanceof Timestamp ts) {
            updatedAt = ts.toLocalDateTime();
        } else if (tsObj instanceof LocalDateTime ldt) {
            updatedAt = ldt;
        } else if (tsObj instanceof java.sql.Date d) {
            updatedAt = d.toLocalDate().atStartOfDay();
        }

        double percent = (totalSum == 0)
                ? 0.0
                : Math.round((correctSum * 100.0 / totalSum) * 100.0) / 100.0;

        return Optional.of(new QuizProgressPOJO(correctSum, totalSum, percent, updatedAt));
    }

    /** Daily accuracy: group theo DATE(completed_at) */
    public List<Map<String, Object>> getDailyAccuracy(Long userId,
                                                      LocalDateTime fromDate,
                                                      Integer gradeId,
                                                      Integer subjectId,
                                                      Integer quizTypeId,
                                                      Long chapterId) {
        List<Object[]> rows = quizResultRepository.aggregateDailyAccuracyNative(
                userId, fromDate, gradeId, subjectId, quizTypeId, chapterId);

        Map<LocalDate, long[]> dailyTotals = new LinkedHashMap<>();
        for (Object[] r : rows) {
            LocalDate day = (r[0] instanceof java.sql.Date d)
                    ? d.toLocalDate()
                    : LocalDate.parse(r[0].toString());
            long correct = ((Number) r[1]).longValue();
            long total   = ((Number) r[2]).longValue();

            dailyTotals.compute(day, (k, v) -> {
                if (v == null) v = new long[]{0, 0};
                v[0] += correct;
                v[1] += total;
                return v;
            });
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (var e : dailyTotals.entrySet()) {
            LocalDate day = e.getKey();
            long correct = e.getValue()[0];
            long total   = e.getValue()[1];
            double percent = (total == 0) ? 0.0 : Math.round((correct * 100.0 / total) * 100.0) / 100.0;

            result.add(Map.of(
                    "day", day,
                    "correctSum", correct,
                    "totalSum", total,
                    "percentAccuracy", percent
            ));
        }
        return result;
    }

    /** View range (nếu controller dùng projection DailyAccuracyViewPOJO) */
    public List<Map<String, Object>> getDailyAccuracyByRange(Long userId, LocalDate from, LocalDate to) {
        List<DailyAccuracyViewPOJO> rows = quizResultRepository
                .findDailyAccuracyByUserAndRange(userId, from.toString(), to.toString());

        List<Map<String, Object>> result = new ArrayList<>();
        for (DailyAccuracyViewPOJO r : rows) {
            LocalDate day = (r.getQuizDate() == null) ? null : r.getQuizDate().toLocalDate();
            Double pct = (r.getDailyPercentage() == null) ? 0.0 : r.getDailyPercentage();
            result.add(Map.of("day", day, "dailyPercentage", pct));
        }
        return result;
    }

    public Double getAverageDailyAccuracy(Long userId) {
        Double avg = quizResultRepository.findAverageDailyAccuracyByUser(userId);
        return (avg == null) ? 0.0 : avg;
    }
}

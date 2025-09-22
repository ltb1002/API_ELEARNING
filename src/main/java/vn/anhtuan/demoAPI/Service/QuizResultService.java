package vn.anhtuan.demoAPI.Service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.anhtuan.demoAPI.Entity.*;
import vn.anhtuan.demoAPI.Repository.QuizResultRepository;
import vn.anhtuan.demoAPI.Repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizResultService {

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizService quizService;

    public QuizResult getQuizResultById(Integer id) {
        Optional<QuizResult> quizResult = quizResultRepository.findById(id);
        return quizResult.orElse(null);
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
    public QuizResult submitQuiz(Long userId, Integer quizId, Map<Long, List<Long>> userAnswers, Integer durationSeconds) { // SỬA: Integer → Long
        // Validation
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (userAnswers == null) {
            throw new IllegalArgumentException("User answers cannot be null");
        }

        if (durationSeconds == null || durationSeconds < 0) {
            throw new IllegalArgumentException("Duration must be a positive value");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Quiz quiz = quizService.getQuizById(quizId);
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz not found with id: " + quizId);
        }

        List<Question> questions = quizService.getQuizQuestions(quizId.longValue());
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("Quiz has no questions");
        }

        int totalQuestions = questions.size();
        int correctAnswers = 0;

        // SỬA: Đổi Integer thành Long cho questionIds
        List<Long> questionIds = questions.stream().map(Question::getId).collect(Collectors.toList());
        Map<Long, Set<Long>> correctChoiceIdsMap = quizService.getCorrectChoiceIdsForQuestions(questionIds);

        for (Question question : questions) {
            List<Long> userSelectedChoices = userAnswers.get(question.getId()); // SỬA: Integer → Long
            Set<Long> correctChoiceIds = correctChoiceIdsMap.get(question.getId()); // SỬA: Integer → Long
            if (correctChoiceIds == null) {
                correctChoiceIds = new HashSet<>(); // Xử lý câu hỏi không có đáp án đúng
            }

            Set<Long> userSelectedIds = userSelectedChoices != null ? // SỬA: Integer → Long
                    new HashSet<>(userSelectedChoices) : new HashSet<>();

            if (correctChoiceIds.isEmpty()) {
                // Nếu không có đáp án đúng, người dùng phải không chọn gì mới tính đúng
                if (userSelectedIds.isEmpty()) {
                    correctAnswers++;
                }
            } else {
                // So sánh đáp án người dùng với đáp án đúng
                if (correctChoiceIds.equals(userSelectedIds)) {
                    correctAnswers++;
                }
            }
        }

        // Tính score và tránh chia cho zero
        BigDecimal score = totalQuestions > 0
                ? new BigDecimal(correctAnswers)
                .divide(new BigDecimal(totalQuestions), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("10"))
                .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Tìm attempt number tiếp theo
        List<QuizResult> existingResults = quizResultRepository.findByUserIdAndQuizId(userId, quizId);
        int attemptNo = existingResults.isEmpty() ? 1 : existingResults.size() + 1;

        // Tạo mới QuizResult
        QuizResult quizResult = new QuizResult(
                user,
                quiz,
                attemptNo,
                score,
                correctAnswers,
                totalQuestions,
                durationSeconds,
                QuizStatus.COMPLETED
        );

        return quizResultRepository.save(quizResult);
    }

    public Map<String, Object> getQuizStatistics(Integer quizId) {
        List<QuizResult> results = quizResultRepository.findByQuizId(quizId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAttempts", results.size());

        if (!results.isEmpty()) {
            double averageScore = results.stream()
                    .mapToDouble(r -> r.getScore().doubleValue())
                    .average()
                    .orElse(0.0);
            stats.put("averageScore", averageScore);

            long completedCount = results.stream()
                    .filter(r -> r.getStatus() == QuizStatus.COMPLETED)
                    .count();
            stats.put("completionRate", (double) completedCount / results.size() * 100);

            // Thêm thông tin về số lần attempt
            Map<Integer, Long> attemptsDistribution = results.stream()
                    .collect(Collectors.groupingBy(
                            QuizResult::getAttemptNo,
                            Collectors.counting()
                    ));
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
                    .average()
                    .orElse(0.0);
            stats.put("averageScore", averageScore);

            long completedCount = results.stream()
                    .filter(r -> r.getStatus() == QuizStatus.COMPLETED)
                    .count();
            stats.put("completionRate", (double) completedCount / results.size() * 100);

            // Count by subject
            Map<String, Long> quizzesBySubject = new HashMap<>();
            for (QuizResult result : results) {
                String subjectName = result.getQuiz().getSubject().getName();
                quizzesBySubject.put(subjectName, quizzesBySubject.getOrDefault(subjectName, 0L) + 1);
            }
            stats.put("quizzesBySubject", quizzesBySubject);

            // Thêm thông tin về thời gian làm bài trung bình
            double averageDuration = results.stream()
                    .filter(r -> r.getDurationSeconds() != null)
                    .mapToInt(QuizResult::getDurationSeconds)
                    .average()
                    .orElse(0.0);
            stats.put("averageDurationSeconds", averageDuration);
        } else {
            stats.put("averageScore", 0);
            stats.put("completionRate", 0);
            stats.put("quizzesBySubject", new HashMap<>());
            stats.put("averageDurationSeconds", 0);
        }

        return stats;
    }
}
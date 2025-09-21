package vn.anhtuan.demoAPI.Service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.anhtuan.demoAPI.Entity.*;
import vn.anhtuan.demoAPI.Repository.QuizResultRepository;
import vn.anhtuan.demoAPI.Repository.UserRepository;

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

    public QuizResult getUserQuizResultForQuiz(Long userId, Integer quizId) {
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
    public QuizResult submitQuiz(Long userId, Integer quizId, Map<Integer, List<Integer>> userAnswers) {
        // Validation
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (userAnswers == null) {
            throw new IllegalArgumentException("User answers cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Quiz quiz = quizService.getQuizById(quizId);
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz not found with id: " + quizId);
        }

        List<Question> questions = quizService.getQuizQuestions(quizId);
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("Quiz has no questions");
        }

        int totalQuestions = questions.size();
        int correctAnswers = 0;

        // Lấy tất cả đáp án đúng cho tất cả câu hỏi trong một lần truy vấn
        List<Integer> questionIds = questions.stream().map(Question::getId).collect(Collectors.toList());
        Map<Integer, Set<Integer>> correctChoiceIdsMap = quizService.getCorrectChoiceIdsForQuestions(questionIds);

        for (Question question : questions) {
            List<Integer> userSelectedChoices = userAnswers.get(question.getId());
            Set<Integer> correctChoiceIds = correctChoiceIdsMap.get(question.getId());
            if (correctChoiceIds == null) {
                correctChoiceIds = new HashSet<>(); // Xử lý câu hỏi không có đáp án đúng
            }

            Set<Integer> userSelectedIds = userSelectedChoices != null ?
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
        float score = totalQuestions > 0 ? (float) correctAnswers / totalQuestions * 10 : 0;

        // Cập nhật hoặc tạo mới QuizResult
        QuizResult quizResult = quizResultRepository.findByUserIdAndQuizId(userId, quizId);
        if (quizResult == null) {
            quizResult = new QuizResult(user, quiz, score, correctAnswers, totalQuestions,
                    QuizResult.QuizStatus.COMPLETED);
        } else {
            quizResult.updateResult(score, correctAnswers, totalQuestions);
        }

        return quizResultRepository.save(quizResult);
    }

    public Map<String, Object> getQuizStatistics(Integer quizId) {
        List<QuizResult> results = quizResultRepository.findByQuizId(quizId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAttempts", results.size());

        if (!results.isEmpty()) {
            double averageScore = results.stream()
                    .mapToDouble(QuizResult::getScore)
                    .average()
                    .orElse(0.0);
            stats.put("averageScore", averageScore);

            long completedCount = results.stream()
                    .filter(r -> r.getStatus() == QuizResult.QuizStatus.COMPLETED)
                    .count();
            stats.put("completionRate", (double) completedCount / results.size() * 100);
        } else {
            stats.put("averageScore", 0);
            stats.put("completionRate", 0);
        }

        return stats;
    }

    public Map<String, Object> getUserStatistics(Long userId) {
        List<QuizResult> results = quizResultRepository.findByUserId(userId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalQuizzesTaken", results.size());

        if (!results.isEmpty()) {
            double averageScore = results.stream()
                    .mapToDouble(QuizResult::getScore)
                    .average()
                    .orElse(0.0);
            stats.put("averageScore", averageScore);

            long completedCount = results.stream()
                    .filter(r -> r.getStatus() == QuizResult.QuizStatus.COMPLETED)
                    .count();
            stats.put("completionRate", (double) completedCount / results.size() * 100);

            // Count by subject
            Map<String, Long> quizzesBySubject = new HashMap<>();
            for (QuizResult result : results) {
                String subjectName = result.getQuiz().getSubject().getName();
                quizzesBySubject.put(subjectName, quizzesBySubject.getOrDefault(subjectName, 0L) + 1);
            }
            stats.put("quizzesBySubject", quizzesBySubject);
        } else {
            stats.put("averageScore", 0);
            stats.put("completionRate", 0);
            stats.put("quizzesBySubject", new HashMap<>());
        }

        return stats;
    }
}
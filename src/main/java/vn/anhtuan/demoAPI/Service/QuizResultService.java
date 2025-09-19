package vn.anhtuan.demoAPI.Service;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.anhtuan.demoAPI.Entity.*;
import vn.anhtuan.demoAPI.Repository.QuizResultRepository;
import vn.anhtuan.demoAPI.Repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public QuizResult submitQuiz(Long userId, Integer quizId, Map<Integer, Integer> userAnswers) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Quiz quiz = quizService.getQuizById(quizId);
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz not found with id: " + quizId);
        }

        // Lấy tất cả câu hỏi của quiz
        List<Question> questions = quizService.getQuizQuestions(quizId);
        int totalQuestions = questions.size();
        int correctAnswers = 0;

        // Preload tất cả correct choices cho tất cả câu hỏi trong quiz
        Map<Integer, List<Integer>> correctChoicesMap = new HashMap<>();
        for (Question question : questions) {
            List<Choice> correctChoices = quizService.getCorrectChoicesForQuestion(question.getId());
            correctChoicesMap.put(question.getId(),
                    correctChoices.stream().map(Choice::getId).collect(Collectors.toList()));
        }

        // Tính điểm
        for (Question question : questions) {
            Integer selectedChoiceId = userAnswers.get(question.getId());

            // Nếu user không trả lời, bỏ qua
            if (selectedChoiceId == null) {
                continue;
            }

            List<Integer> correctChoiceIds = correctChoicesMap.get(question.getId());

            // Kiểm tra nếu selected choice nằm trong danh sách correct choices
            if (correctChoiceIds.contains(selectedChoiceId)) {
                correctAnswers++;
            }
        }

        float score = totalQuestions > 0 ? (float) correctAnswers / totalQuestions * 10 : 0;

        // Create or update quiz result
        QuizResult existingResult = quizResultRepository.findByUserIdAndQuizId(userId, quizId);
        QuizResult quizResult;

        if (existingResult != null) {
            quizResult = existingResult;
            quizResult.setScore(score);
            quizResult.setCorrectAnswers(correctAnswers);
            quizResult.setTotalQuestions(totalQuestions);
            quizResult.setStatus(QuizResult.QuizStatus.COMPLETED);
        } else {
            quizResult = new QuizResult(user, quiz, score, correctAnswers, totalQuestions,
                    QuizResult.QuizStatus.COMPLETED);
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

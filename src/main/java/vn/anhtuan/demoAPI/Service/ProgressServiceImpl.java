package vn.anhtuan.demoAPI.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.anhtuan.demoAPI.Entity.*;
import vn.anhtuan.demoAPI.POJO.ProgressResponsePOJO;
import vn.anhtuan.demoAPI.Repository.*;
import org.springframework.dao.DataIntegrityViolationException;


import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProgressServiceImpl implements ProgressService {

    @Autowired
    private LessonCompletionRepository lessonCompletionRepository;

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private UserStreakService userStreakService; // ✅ thêm streak service


    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public LessonCompletion completeLesson(Long userId, Long lessonId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + lessonId));

        // Nếu đã hoàn thành rồi -> vẫn "touch" streak cho idempotent theo ngày
        Optional<LessonCompletion> existingCompletion = lessonCompletionRepository.findByUserAndLesson(user, lesson);
        if (existingCompletion.isPresent()) {
            // ✅ đảm bảo ghi nhận hoạt động hôm nay
            userStreakService.touch(userId);
            return existingCompletion.get();
        }

        // Tạo mới lesson completion
        LessonCompletion lessonCompletion = new LessonCompletion(user, lesson);
        try {
            lessonCompletionRepository.save(lessonCompletion);
        } catch (DataIntegrityViolationException e) {
            // Trong TH hiếm có race-condition -> coi như đã có record
            // vẫn tiếp tục update progress + touch streak
        }

        // Cập nhật progress
        updateProgress(user, lesson.getChapter().getSubject());

        // ✅ ghi nhận streak (idempotent theo ngày – không cộng trùng)
        userStreakService.touch(userId);

        return lessonCompletion;
    }

    @Override
    public void uncompleteLesson(Long userId, Long lessonId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + lessonId));

        // Tìm và xóa lesson completion
        Optional<LessonCompletion> lessonCompletion = lessonCompletionRepository.findByUserAndLesson(user, lesson);
        if (lessonCompletion.isPresent()) {
            lessonCompletionRepository.delete(lessonCompletion.get());

            // Cập nhật progress
            updateProgress(user, lesson.getChapter().getSubject());
        }
    }

    @Override
    public boolean isLessonCompleted(Long userId, Long lessonId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + lessonId));

        return lessonCompletionRepository.existsByUserAndLesson(user, lesson);
    }

    private void updateProgress(User user, Subject subject) {
        // Tính tổng số lesson trong subject
        long totalLessons = lessonRepository.countByChapterSubject(subject);

        // Tính số lesson đã hoàn thành
        long completedLessons = lessonCompletionRepository.countCompletedLessonsByUserAndSubject(user.getId(), subject.getId());

        // Tìm hoặc tạo mới progress
        Progress progress = progressRepository.findByUserAndSubject(user, subject)
                .orElse(new Progress(user, subject));

        // Cập nhật thông tin
        progress.setTotalLessons((int) totalLessons);
        progress.setCompletedLessons((int) completedLessons);
        progress.calculateProgress();

        progressRepository.save(progress);
    }

    @Override
    public Progress getProgressByUserAndSubject(Long userId, Integer subjectId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));

        return progressRepository.findByUserAndSubject(user, subject)
                .orElse(new Progress(user, subject));
    }

    @Override
    public List<Progress> getProgressByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return progressRepository.findByUser(user);
    }

    @Override
    public List<Progress> getProgressByUserAndGrade(Long userId, Integer grade) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return progressRepository.findByUserAndGrade(user, grade);
    }

    @Override
    public void updateTotalLessonsForSubject(Integer subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));

        long totalLessons = lessonRepository.countByChapterSubject(subject);

        // Cập nhật tất cả progress records cho subject này
        List<Progress> progressList = progressRepository.findBySubject(subject);
        for (Progress progress : progressList) {
            progress.setTotalLessons((int) totalLessons);
            progress.calculateProgress();
        }
        progressRepository.saveAll(progressList);
    }

    @Override
    public ProgressResponsePOJO convertToProgressResponseDTO(Progress progress) {
        ProgressResponsePOJO dto = new ProgressResponsePOJO();
        dto.setId(progress.getId());
        dto.setUserId(progress.getUser().getId());
        dto.setSubjectId(progress.getSubject().getId());
        dto.setSubjectName(progress.getSubject().getName());
        dto.setSubjectCode(progress.getSubject().getCode());
        dto.setGrade(progress.getGrade());
        dto.setCompletedLessons(progress.getCompletedLessons());
        dto.setTotalLessons(progress.getTotalLessons());
        dto.setProgressPercent(progress.getProgressPercent());
        dto.setUpdatedAt(progress.getUpdatedAt());
        return dto;
    }
}
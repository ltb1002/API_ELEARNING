package vn.anhtuan.demoAPI.POJO;

import java.time.LocalDateTime;

public class QuizProgressPOJO {
    // ---- filter input ----
    private Long userId;          // id của user
    private Integer gradeId;      // nullable
    private Integer subjectId;    // nullable
    private Integer quizTypeId;   // nullable

    // ---- output result ----
    private final long correctSum;        // tổng số câu đúng
    private final long totalSum;          // tổng số câu đã làm
    private final double percentAccuracy; // % chính xác = correctSum / totalSum * 100
    private final LocalDateTime updatedAt; // thời gian cập nhật cuối

    // Constructor cho output
    public QuizProgressPOJO(long correctSum, long totalSum, double percentAccuracy, LocalDateTime updatedAt) {
        this.correctSum = correctSum;
        this.totalSum = totalSum;
        this.percentAccuracy = percentAccuracy;
        this.updatedAt = updatedAt;
    }

    // Constructor full (bao gồm filter input)
    public QuizProgressPOJO(Long userId,
                            Integer gradeId,
                            Integer subjectId,
                            Integer quizTypeId,
                            long correctSum,
                            long totalSum,
                            double percentAccuracy,
                            LocalDateTime updatedAt) {
        this.userId = userId;
        this.gradeId = gradeId;
        this.subjectId = subjectId;
        this.quizTypeId = quizTypeId;
        this.correctSum = correctSum;
        this.totalSum = totalSum;
        this.percentAccuracy = percentAccuracy;
        this.updatedAt = updatedAt;
    }

    // ---- getter & setter ----
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getGradeId() { return gradeId; }
    public void setGradeId(Integer gradeId) { this.gradeId = gradeId; }

    public Integer getSubjectId() { return subjectId; }
    public void setSubjectId(Integer subjectId) { this.subjectId = subjectId; }

    public Integer getQuizTypeId() { return quizTypeId; }
    public void setQuizTypeId(Integer quizTypeId) { this.quizTypeId = quizTypeId; }

    public long getCorrectSum() { return correctSum; }
    public long getTotalSum() { return totalSum; }
    public double getPercentAccuracy() { return percentAccuracy; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

package vn.anhtuan.demoAPI.POJO;

public class QuizProgressPOJO {
    // ---- filter input ----
    private Long userId;          // id của user
    private Integer gradeId;      // nullable
    private Integer subjectId;    // nullable
    private Integer quizTypeId;   // nullable
    private Long chapterId;       // nullable

    // ---- output result ----
    private final long total;     // tổng số quiz
    private final long completed; // số quiz đã làm (COMPLETED)
    private final double percent; // % hoàn thành

    // Constructor cho phần output (total + completed)
    public QuizProgressPOJO(long total, long completed) {
        this.total = total;
        this.completed = completed;
        double raw = (total == 0) ? 0.0 : (completed * 100.0) / total;
        this.percent = Math.round(raw * 100.0) / 100.0;
    }

    // Constructor full (filter + output)
    public QuizProgressPOJO(Long userId,
                            Integer gradeId,
                            Integer subjectId,
                            Integer quizTypeId,
                            Long chapterId,
                            long total,
                            long completed) {
        this.userId = userId;
        this.gradeId = gradeId;
        this.subjectId = subjectId;
        this.quizTypeId = quizTypeId;
        this.chapterId = chapterId;
        this.total = total;
        this.completed = completed;
        double raw = (total == 0) ? 0.0 : (completed * 100.0) / total;
        this.percent = Math.round(raw * 100.0) / 100.0;
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

    public Long getChapterId() { return chapterId; }
    public void setChapterId(Long chapterId) { this.chapterId = chapterId; }

    public long getTotal() { return total; }
    public long getCompleted() { return completed; }
    public double getPercent() { return percent; }
}

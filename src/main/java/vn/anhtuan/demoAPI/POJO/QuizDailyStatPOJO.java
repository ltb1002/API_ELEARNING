package vn.anhtuan.demoAPI.POJO;

import java.time.LocalDate;

public class QuizDailyStatPOJO {
    private final LocalDate day;
    private final long correct;
    private final long total;
    private final double percent;

    public QuizDailyStatPOJO(LocalDate day, long correct, long total) {
        this.day = day;
        this.correct = correct;
        this.total = total;
        double raw = (total == 0) ? 0.0 : (correct * 100.0) / total;
        this.percent = Math.round(raw * 100.0) / 100.0;
    }

    public LocalDate getDay() { return day; }
    public long getCorrect() { return correct; }
    public long getTotal() { return total; }
    public double getPercent() { return percent; }
}

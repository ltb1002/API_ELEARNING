package vn.anhtuan.demoAPI.Service;

public interface DailyAccuracyRow {
    @org.springframework.beans.factory.annotation.Value("#{T(java.time.LocalDate).parse(target.quizDate)}")
    String getQuizDate();         // hoặc LocalDate và bỏ @Value nếu MySQL trả đúng định dạng

    Double getDailyPercentage();
}

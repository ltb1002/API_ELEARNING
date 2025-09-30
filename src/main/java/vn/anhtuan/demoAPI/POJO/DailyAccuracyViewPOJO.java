package vn.anhtuan.demoAPI.POJO;

import java.sql.Date;

public interface DailyAccuracyViewPOJO {
    Date   getQuizDate();        // mapping alias: quizDate
    Double getDailyPercentage(); // mapping alias: dailyPercentage
}

package vn.anhtuan.demoAPI.POJO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStreakPOJO {
    private int currentStreak;
    private int bestStreak;
    private int totalDays;

    @JsonFormat(pattern = "yyyy-MM-dd") // tuỳ chọn: format ngày trong JSON
    private LocalDate lastActiveDate;
}

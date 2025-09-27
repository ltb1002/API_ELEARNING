// src/main/java/vn/anhtuan/demoAPI/Config/TimeConfig.java
package vn.anhtuan.demoAPI.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TimeConfig {

    @Bean
    public Clock appClock() {
        // Không còn đọc app.time.override -> luôn dùng clock hệ thống
        return Clock.systemDefaultZone();
    }
}
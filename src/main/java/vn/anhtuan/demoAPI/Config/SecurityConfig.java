package vn.anhtuan.demoAPI.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import vn.anhtuan.demoAPI.Security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC ENDPOINTS
                        .requestMatchers(
                                // Auth
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password",

                                // Data
                                "/api/data/**",

                                // PDF
                                "/api/pdf/**",



                                "/api/streak/**",


                                // Quizzes - public
                                "/api/quizzes",
                                "/api/quizzes/*",
                                "/api/quizzes/*/details",
                                "/api/quizzes/code/**",
                                "/api/quizzes/filter",
                                "/api/quizzes/grades/**",
                                "/api/quizzes/grades/*/subjects/**",
                                "/api/quizzes/subjects/**",

                                // Search
                                "/api/search/**",

                                // Subjects & Content - public
                                "/api/subjects/**",
                                "/api/grades/**",
                                "/api/chapters/**",
                                "/api/lessons/**",
                                "/api/exercises/**",

                                // H2 Console
                                "/h2-console/**"
                        ).permitAll()

                        // ADMIN ENDPOINTS
                        .requestMatchers(
                                "/api/admin/**",
                                "/api/progress/admin/**"  // THÊM ENDPOINT ADMIN CỦA PROGRESS
                        ).hasRole("ADMIN")

                        // AUTHENTICATED ENDPOINTS (require login)
                        .requestMatchers(
                                "/api/auth/user-profile",
                                "/api/auth/change-password",
                                "/api/auth/validate-token",
                                "/api/users/**",

                                "/api/quizzes/*/submit",
                                "/api/quizzes/*/history",
                                "/api/quizzes/*/users/*/history",
                                "/api/quizzes/*/users/*/best-score",
                                "/api/quizzes/*/questions",

                                "/api/progress/complete-lesson",
                                "/api/progress/uncomplete-lesson/**",
                                "/api/progress/check-completion/**",
                                "/api/progress/user/**"
                        ).authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }
}
package vn.anhtuan.demoAPI.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.anhtuan.demoAPI.Entity.UserActivity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    // Tìm activity theo user và ngày cụ thể
    Optional<UserActivity> findByUserIdAndActivityDate(Long userId, LocalDate activityDate);

    // Lấy tất cả activities của user trong khoảng thời gian
    List<UserActivity> findByUserIdAndActivityDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    // Lấy tất cả activities của user, sắp xếp theo ngày giảm dần
    List<UserActivity> findByUserIdOrderByActivityDateDesc(Long userId);

    // Lấy các ngày có học trong khoảng thời gian
    @Query("SELECT ua.activityDate FROM UserActivity ua WHERE ua.userId = :userId AND ua.activityDate BETWEEN :startDate AND :endDate ORDER BY ua.activityDate DESC")
    List<LocalDate> findActivityDatesByUserIdAndDateRange(@Param("userId") Long userId,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);

    // Lấy số phút học tập theo ngày trong khoảng thời gian
    @Query("SELECT ua.activityDate, ua.minutesUsed FROM UserActivity ua WHERE ua.userId = :userId AND ua.activityDate BETWEEN :startDate AND :endDate")
    List<Object[]> findActivityMinutesByUserIdAndDateRange(@Param("userId") Long userId,
                                                           @Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate);

    // Lấy ngày học gần nhất
    @Query("SELECT MAX(ua.activityDate) FROM UserActivity ua WHERE ua.userId = :userId")
    Optional<LocalDate> findLatestActivityDateByUserId(@Param("userId") Long userId);

    // Kiểm tra xem user có học trong ngày cụ thể không
    @Query("SELECT COUNT(ua) > 0 FROM UserActivity ua WHERE ua.userId = :userId AND ua.activityDate = :date")
    boolean existsByUserIdAndActivityDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    @Query("SELECT ua.activityDate, SUM(ua.minutesUsed) " +
            "FROM UserActivity ua " +
            "WHERE ua.userId = :userId AND ua.activityDate BETWEEN :startDate AND :endDate " +
            "GROUP BY ua.activityDate")
    List<Object[]> getDailyTotalMinutes(Long userId, LocalDate startDate, LocalDate endDate);

}

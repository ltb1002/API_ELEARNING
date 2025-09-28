package vn.anhtuan.demoAPI.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.anhtuan.demoAPI.Entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmailIgnoreCase(@Param("email") String email);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    Optional<User> findByUsernameIgnoreCase(String username);

    // Tồn tại (để validate trước khi update)
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByUsernameIgnoreCase(String username);

    // Tồn tại nhưng loại trừ chính mình (đặc biệt quan trọng khi update)
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);
}
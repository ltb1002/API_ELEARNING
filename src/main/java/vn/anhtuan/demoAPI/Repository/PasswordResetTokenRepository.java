package vn.anhtuan.demoAPI.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import vn.anhtuan.demoAPI.Entity.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
}
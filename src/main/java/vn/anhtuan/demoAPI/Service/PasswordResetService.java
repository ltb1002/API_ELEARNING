package vn.anhtuan.demoAPI.Service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.anhtuan.demoAPI.Entity.PasswordResetToken;
import vn.anhtuan.demoAPI.Entity.User;
import vn.anhtuan.demoAPI.Repository.PasswordResetTokenRepository;
import vn.anhtuan.demoAPI.Repository.UserRepository;
import vn.anhtuan.demoAPI.Security.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // Trong PasswordResetService
    public String createPasswordResetToken(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        // Sử dụng JWT token thay vì UUID
        String token = jwtTokenProvider.createPasswordResetToken(email);

        // Lưu token vào database (optional, có thể chỉ dùng JWT)
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email.toLowerCase().trim());
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        tokenRepository.save(resetToken);

        System.out.println("[PASSWORD_RESET_TOKEN] " + token);
        return token;
    }

    public boolean resetPassword(String token, String newPassword) {
        // Validate JWT token first
        if (!jwtTokenProvider.validatePasswordResetToken(token)) {
            throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn");
        }

        String email = jwtTokenProvider.getEmailFromPasswordResetToken(token);
        if (email == null) {
            throw new RuntimeException("Token không hợp lệ");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        // Encode password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Xóa token khỏi database sau khi sử dụng
        tokenRepository.findByToken(token).ifPresent(tokenRepository::delete);

        return true;
    }

    public void saveToken(PasswordResetToken token) {
        tokenRepository.save(token);
    }

}
package vn.anhtuan.demoAPI.Service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.anhtuan.demoAPI.Entity.PasswordResetToken;
import vn.anhtuan.demoAPI.Entity.User;
import vn.anhtuan.demoAPI.Repository.PasswordResetTokenRepository;
import vn.anhtuan.demoAPI.Repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String createPasswordResetToken(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email.toLowerCase().trim());
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        tokenRepository.save(resetToken);

        System.out.println("[TOKEN] " + token); // dùng debug
        return token;
    }

    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Token đã hết hạn");

        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        // Encode password tại đây 1 lần duy nhất
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);

        return true;
    }

    public void saveToken(PasswordResetToken token) {
        tokenRepository.save(token);
    }

}
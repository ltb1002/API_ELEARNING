package vn.anhtuan.demoAPI.REST;


import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.anhtuan.demoAPI.Entity.PasswordResetToken;
import vn.anhtuan.demoAPI.Entity.User;
import vn.anhtuan.demoAPI.Service.PasswordResetService;
import vn.anhtuan.demoAPI.Service.UserService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService,
                          PasswordResetService passwordResetService,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String,Object>> register(@RequestBody Map<String,String> body){
        String email = body.get("email");
        String password = body.get("password");

        if(email==null || email.isBlank() || password==null || password.isBlank()){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Email & password cannot be empty"));
        }

        if(userService.findByEmailIgnoreCase(email).isPresent()){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Email already exists"));
        }

        User user = new User(email.trim().toLowerCase(), passwordEncoder.encode(password));
        userService.save(user);

        return ResponseEntity.ok(Map.of("success",true,"message","User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody Map<String,String> body){
        String email = body.get("email");
        String password = body.get("password");

        if(email==null || email.isBlank() || password==null || password.isBlank()){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Email & password cannot be empty"));
        }

        Optional<User> userOpt = userService.findByEmailIgnoreCase(email.trim());
        if(userOpt.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Email not found"));
        }

        User user = userOpt.get();
        if(!passwordEncoder.matches(password, user.getPassword())){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Password incorrect"));
        }

        // Generate dummy token (JWT nên tạo thực tế)
        String token = UUID.randomUUID().toString();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Login successful",
                "token", token,
                "username", user.getEmail()
        ));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String,Object>> forgotPassword(@RequestBody Map<String,String> body){
        String email = body.get("email");
        if(email==null || email.isBlank()){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Email cannot be empty"));
        }

        Optional<User> userOpt = userService.findByEmailIgnoreCase(email);
        if(userOpt.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Email not found"));
        }

        // Generate token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        passwordResetService.saveToken(resetToken);

        // TODO: Send email here

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Reset token generated",
                "token", token
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String,Object>> resetPassword(@RequestBody Map<String,String> body){
        String token = body.get("token");
        String newPassword = body.get("newPassword");

        if(token==null || token.isBlank() || newPassword==null || newPassword.isBlank()){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Token & newPassword required"));
        }

        try {
            // Chỉ gửi raw password, service sẽ encode
            passwordResetService.resetPassword(token, newPassword.trim());
            return ResponseEntity.ok(Map.of("success",true,"message","Password reset successfully"));
        } catch(RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message",e.getMessage()));
        }
    }
}
package vn.anhtuan.demoAPI.REST;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
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
@CrossOrigin(origins = "*")
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
        String username = body.get("username");

        if(email==null || email.isBlank() || password==null || password.isBlank() || username==null || username.isBlank()){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Email, username & password cannot be empty"));
        }

        if(userService.findByEmailIgnoreCase(email).isPresent()){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Email already exists"));
        }

        if(userService.findByUsername(username).isPresent()){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Username already exists"));
        }

        User user = new User(email.trim().toLowerCase(), passwordEncoder.encode(password), username.trim());
        userService.save(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User registered successfully",
                "user", Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "username", user.getUsername()
                )
        ));
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
                "username", user.getUsername(),
                "userId", user.getId()
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

    @GetMapping("/user-profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(@RequestHeader("Authorization") String token) {
        // Extract user ID from token (simplified implementation)
        // In a real application, you would decode the JWT token
        try {
            // This is a simplified approach - in a real app, you'd use JWT
            String userIdStr = token.replace("Bearer ", "");
            Long userId = Long.parseLong(userIdStr);

            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "User not found"));
            }

            User user = userOpt.get();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "user", Map.of(
                            "id", user.getId(),
                            "email", user.getEmail(),
                            "username", user.getUsername()
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid token"));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> body) {

        try {
            String userIdStr = token.replace("Bearer ", "");
            Long userId = Long.parseLong(userIdStr);

            String currentPassword = body.get("currentPassword");
            String newPassword = body.get("newPassword");

            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Current and new password are required"));
            }

            boolean success = userService.changePassword(userId, currentPassword, newPassword);
            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Password changed successfully"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Current password is incorrect"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid token"));
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String userIdStr = token.replace("Bearer ", "");
            Long userId = Long.parseLong(userIdStr);

            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid token"));
            }

            return ResponseEntity.ok(Map.of("success", true, "message", "Token is valid"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid token"));
        }
    }
}
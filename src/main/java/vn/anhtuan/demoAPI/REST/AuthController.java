package vn.anhtuan.demoAPI.REST;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.demoAPI.Entity.PasswordResetToken;
import vn.anhtuan.demoAPI.Entity.User;
import vn.anhtuan.demoAPI.Security.JwtTokenProvider;
import vn.anhtuan.demoAPI.Service.PasswordResetService;
import vn.anhtuan.demoAPI.Service.UserService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider; // Thêm dòng này

    public AuthController(UserService userService,
                          PasswordResetService passwordResetService,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtTokenProvider) { // Thêm parameter
        this.userService = userService;
        this.passwordResetService = passwordResetService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider; // Gán giá trị
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

        // Sử dụng JWT token
        String token = jwtTokenProvider.generateToken(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Login successful",
                "token", token,
                "username", user.getUsername(),
                "userId", user.getId(),
                "role", user.getRole().name()
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
        String token = jwtTokenProvider.createPasswordResetToken(email); // Sử dụng service

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
            passwordResetService.resetPassword(token, newPassword.trim());
            return ResponseEntity.ok(Map.of("success",true,"message","Password reset successfully"));
        } catch(RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message",e.getMessage()));
        }
    }

    // XÓA METHOD getUserProfile CŨ (có @RequestHeader) VÀ GIỮ LẠI METHOD MỚI DƯỚI ĐÂY
    @GetMapping("/user-profile")
    public ResponseEntity<Map<String, Object>> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Not authenticated"));
        }

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "user", Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "username", user.getUsername(),
                        "role", user.getRole().name()
                )
        ));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> body) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Not authenticated"));
        }

        User user = (User) authentication.getPrincipal();

        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Current and new password are required"));
        }

        boolean success = userService.changePassword(user.getId(), currentPassword, newPassword);
        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Password changed successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Current password is incorrect"));
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid token"));
        }

        return ResponseEntity.ok(Map.of("success", true, "message", "Token is valid"));
    }
}
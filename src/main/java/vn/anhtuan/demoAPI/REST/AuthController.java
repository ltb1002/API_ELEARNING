package vn.anhtuan.demoAPI.REST;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.demoAPI.Entity.User;
import vn.anhtuan.demoAPI.POJO.ChangePasswordPOJO;
import vn.anhtuan.demoAPI.POJO.UpdateProfileRequestPOJO;
import vn.anhtuan.demoAPI.Security.JwtTokenProvider;
import vn.anhtuan.demoAPI.Service.PasswordResetService;
import vn.anhtuan.demoAPI.Service.UserService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserService userService,
                          PasswordResetService passwordResetService,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // ===== REGISTER =====
    @PostMapping("/register")
    public ResponseEntity<Map<String,Object>> register(@RequestBody Map<String,String> body){
        String email = body.get("email");
        String password = body.get("password");
        String username = body.get("username");

        if (email==null || email.isBlank() || password==null || password.isBlank() || username==null || username.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Email, username & password cannot be empty"));
        }

        if (userService.findByEmailIgnoreCase(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Email already exists"));
        }
        if (userService.findByUsername(username).isPresent()) {
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

    // ===== LOGIN =====
    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody Map<String,String> body){
        String email = body.get("email");
        String password = body.get("password");

        if (email==null || email.isBlank() || password==null || password.isBlank()){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Email & password cannot be empty"));
        }

        Optional<User> userOpt = userService.findByEmailIgnoreCase(email.trim());
        if (userOpt.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Email not found"));
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Password incorrect"));
        }

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

    // ===== FORGOT PASSWORD =====
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String,Object>> forgotPassword(@RequestBody Map<String,String> body){
        String email = body.get("email");
        if (email==null || email.isBlank()){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Email cannot be empty"));
        }

        if (userService.findByEmailIgnoreCase(email).isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Email not found"));
        }

        String token = jwtTokenProvider.createPasswordResetToken(email);
        // TODO: gửi email token

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

        if (token==null || token.isBlank() || newPassword==null || newPassword.isBlank()){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message","Token & newPassword required"));
        }

        try {
            passwordResetService.resetPassword(token, newPassword.trim());
            return ResponseEntity.ok(Map.of("success",true,"message","Password reset successfully"));
        } catch(RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("success",false,"message",e.getMessage()));
        }
    }

    // ===== TOKEN CHECK =====
    @GetMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid token"));
        }
        return ResponseEntity.ok(Map.of("success", true, "message", "Token is valid"));
    }

    // ===== GET PROFILE (DUY NHẤT 1 METHOD) =====
    @GetMapping("/user-profile")
    public ResponseEntity<Map<String, Object>> userProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Not authenticated"));
        }
        User u = (auth.getPrincipal() instanceof User)
                ? (User) auth.getPrincipal()
                : userService.findByEmailIgnoreCase(auth.getName())
                .or(() -> userService.findByUsername(auth.getName()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(Map.of(
                "success", true,
                "user", Map.of(
                        "id", u.getId(),
                        "email", u.getEmail(),
                        "username", u.getUsername(),
                        "role", u.getRole().name()
                )
        ));
    }

    // ===== UPDATE PROFILE (username + email, và TÙY CHỌN đổi mật khẩu tại đây) =====
    @PutMapping("/user-profile")
    public ResponseEntity<Map<String, Object>> updateUserProfile(@Valid @RequestBody UpdateProfileRequestPOJO body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Not authenticated"));
        }
        User u = (User) auth.getPrincipal();

        String newEmail = body.getEmail().trim().toLowerCase();
        String newUsername = body.getUsername().trim();

        // unique email nếu đổi
        var emailOwner = userService.findByEmailIgnoreCase(newEmail);
        if (!u.getEmail().equalsIgnoreCase(newEmail) && emailOwner.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email already exists"));
        }
        // unique username nếu đổi
        var userByName = userService.findByUsername(newUsername);
        if (!u.getUsername().equalsIgnoreCase(newUsername) && userByName.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Username already exists"));
        }

        u.setEmail(newEmail);
        u.setUsername(newUsername);

        // Nếu client gửi newPassword -> yêu cầu currentPassword khớp rồi mới đổi
        if (body.getNewPassword() != null && !body.getNewPassword().isBlank()) {
            if (body.getCurrentPassword() == null || !passwordEncoder.matches(body.getCurrentPassword(), u.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Current password incorrect"));
            }
            if (body.getNewPassword().length() < 6) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "New password must be at least 6 characters"));
            }
            u.setPassword(passwordEncoder.encode(body.getNewPassword()));
        }

        userService.save(u);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "user", Map.of(
                        "id", u.getId(),
                        "email", u.getEmail(),
                        "username", u.getUsername(),
                        "role", u.getRole().name()
                )
        ));
    }

    // ===== CHANGE PASSWORD (endpoint riêng, nếu bạn muốn tách biệt với update profile) =====
    @PutMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@Valid @RequestBody ChangePasswordPOJO req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Not authenticated"));
        }
        User u = (User) auth.getPrincipal();

        if (!passwordEncoder.matches(req.getCurrentPassword(), u.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Current password incorrect"));
        }
        if (req.getNewPassword().length() < 8) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "New password must be at least 8 characters"));
        }

        u.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userService.save(u);

        return ResponseEntity.ok(Map.of("success", true, "message", "Password changed successfully"));
    }
}
